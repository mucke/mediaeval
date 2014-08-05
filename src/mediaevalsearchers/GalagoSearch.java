/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediaevalsearchers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import static org.lemurproject.galago.core.tools.apps.BatchSearch.collectQueries;
import static org.lemurproject.galago.core.tools.apps.BatchSearch.logger;
import org.lemurproject.galago.tupleflow.Parameters;

/**
 * Sends a galago query to the search engine, and processes the result to keep
 * only the files of the POI
 *
 * @author mihailupu
 */
public class GalagoSearch {

    private TreeSet<String> allowedImages = new TreeSet<>();
    private String topicsFile;
    private File parentFolder;
    private static final Logger LOG = Logger.getLogger(GalagoSearch.class.getName());

    public static void main(String args[]) {
        GalagoSearch gs = new GalagoSearch();
        try {
            gs.run(args);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

  

    private void run(String[] args) throws IOException, Exception {
        topicsFile = args[0];

        parentFolder = new File(topicsFile).getParentFile();
        BufferedReader br = new BufferedReader(new FileReader(topicsFile));
        String line = "";
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, "|");
            String topic = st.nextToken();
            String latitude = st.nextToken();
            String longitude = st.nextToken();
            String url = st.nextToken();
            String query = st.nextToken();
            allowedImages = new TreeSet<>();
            String imageListFilename = parentFolder.getAbsolutePath() + File.separator + "xml4solr.imagesLists" + File.separator + query.replace(" ","_") + ".xml.images.txt";
            BufferedReader br1 = new BufferedReader(new FileReader(imageListFilename));
            String line1 = "";
            while ((line1 = br1.readLine()) != null) {
                allowedImages.add(line1.trim());
            }
            br1.close();

            search(Arrays.copyOfRange(args, 1, args.length), query, topic);

        }
        br.close();
    }

    private void search(String[] args, String queryString, String topic) throws FileNotFoundException, Exception {
        String queryFile = args[0];
        Parameters params = Parameters.parseFile(queryFile);

        List<ScoredDocument> results = null;

        PrintStream out = System.out;

        // ensure we can print to a file instead of the commandline
        if (params.isString("outputFile")) {
            boolean append = params.get("appendFile", false);
            out = new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(params.getString("outputFile"), append)));
        }

        // get queries
        List<Parameters> queries = collectQueries(params);

        // open index
        Retrieval retrieval = RetrievalFactory.instance(params);

        // record results requested
        int requested = (int) params.get("requested", 1000);

        //actually, we only have one query, which we will replace with our own text
        for (Parameters query : queries) {
            String queryText = query.getString("text").replace("placeholder", queryString);            
            String queryNumber = topic;

            query.setBackoff(params);
            query.set("requested", requested);

            // option to fold query cases -- note that some parameters may require upper case
            if (query.get("casefold", false)) {
                queryText = queryText.toLowerCase();
            }

            if (params.get("verbose", false)) {
                logger.info("RUNNING: " + queryNumber + " : " + queryText);
            }

            // parse and transform query into runnable form
            Node root = StructuredQuery.parse(queryText);
            Node transformed = retrieval.transformQuery(root, query);

            if (params.get("verbose", false)) {
                logger.info("Transformed Query:\n" + transformed.toPrettyString());
            }

            // run query
            results = retrieval.executeQuery(transformed, query).scoredDocuments;


       
        // Print out results.
        int ranking = 0;
        if (!results.isEmpty()) {
            LOG.info("Search output follows ...\n");
            for (ScoredDocument result : results) {
                String imageID;
                int i1 = result.documentName.lastIndexOf(File.separatorChar);
                int i2 = result.documentName.lastIndexOf(".");
                if (i2<0) i2=result.documentName.length();
                imageID=result.documentName.substring(i1+1, i2);
                if (allowedImages.contains(imageID) && ranking < 50) {
                    ++ranking;
                    out.println(String.format("%s Q0 %s %d %s galago", topic, imageID, ranking, result.score));                            
                }

            }
        } else {
            LOG.info("No search output.\n");
        }
                
        
        }
         if (params.isString("outputFile")) {
            out.close();
        }
    }
}
