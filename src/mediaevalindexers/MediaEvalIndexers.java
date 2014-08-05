/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediaevalindexers;

import DataObjects.FlickrImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Mihai Lupu <mihai at mihailupu.net>
 */
public class MediaEvalIndexers {

    private static final Logger LOG = Logger.getLogger(MediaEvalIndexers.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            MediaEvalIndexers mei = new MediaEvalIndexers();
            mei.run(args);
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void run(String[] args) throws ParserConfigurationException, SAXException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        String folderPath = args[0];
        String document = args[1];
        File folder = new File(folderPath);
        File outputFolder = new File(folder.getParentFile().getAbsolutePath() + File.separator + folder.getName() + "4solr_" + document);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }

        File[] xmlFiles = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("xml");
            }
        });
        if (xmlFiles == null) {
            Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, "No files found", "No files found in " + folderPath);
            return;
        }
        HashMap<String, String> userDoc = new HashMap();
        if (document.equalsIgnoreCase("user")) {
            for (File xmlFile : xmlFiles) {
                try {
                    System.out.println("reading file:" + xmlFile.getAbsolutePath());
                    PointOfInterestSAXHandler handler = new PointOfInterestSAXHandler();
                    parser.parse(xmlFile, handler);
                    Iterator<FlickrImage> imagesIterator = handler.poi.iterator();
                    //just genearate a hashmap, because a user might be present in multiple xml files
                    while (imagesIterator.hasNext()) {
                        FlickrImage image = imagesIterator.next();
                        String user = image.getUserid();
                        //for the 2013 collection, there was no userid, so we put a hash of the name
                        if (user == null) {
                            user = Integer.toString(image.getUsername().hashCode());
                        }
                        String text = "<field name=\"date_taken\">" + image.getDate_taken().replace(' ', 'T') + "Z</field>"
                                + "<field name=\"description\">" + image.getDescription() + "</field> "
                                + "<field name=\"id\">" + image.getId() + "</field> "
                                + "<field name=\"location\">" + image.getLatitude() + "," + image.getLongitude() + "</field>"
                                + "<field name=\"license\">" + image.getLicense() + "</field>"
                                + "<field name=\"nbComments\">" + image.getNbComments() + "</field>"
                                + "<field name=\"rank\">" + image.getRank() + "</field>"
                                + "<field name=\"tags\">" + image.getTags() + "</field>"
                                + "<field name=\"title\">" + image.getTitle() + "</field>"
                                + "<field name=\"url_b\">" + image.getUrl_b() + "</field>"
                                + "<field name=\"views\">" + image.getViews() + "</field>"
                                + "<field name=\"username\">" + image.getUsername() + "</field>"
                                + "<field name=\"poi\">" + image.getPoi() + "</field>";
                        if (userDoc.keySet().contains(user)) {
                            String oldText = userDoc.get(user);
                            userDoc.put(user, oldText + text);
                        } else {
                            userDoc.put(user, text);
                        }
                    }
                } catch (IOException ioex) {
                    Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, null, ioex);
                }
            }
            //in this case, we have only generated a hashmap, we have to write it to file now
            for (String user : userDoc.keySet()) {
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + File.separator + user + ".xml"));
                    bw.write("<add><doc>");
                    bw.write(userDoc.get(user));
                    bw.write("<field name=\"userid\">" + user + "</field>");
                    bw.write("</doc></add>");
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (document.equalsIgnoreCase("stats")) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + File.separator + "stats.txt"));
                 bw.write("poi\tdate taken\tid\tdescriptionSize\thasLocation\tnbComments\ttagsSize\ttitleSize\tviews\tuserid\n");
                for (File xmlFile : xmlFiles) {

                    System.out.println("reading file:" + xmlFile.getAbsolutePath());
                    PointOfInterestSAXHandler handler = new PointOfInterestSAXHandler();
                    parser.parse(xmlFile, handler);
                    Iterator<FlickrImage> imagesIterator = handler.poi.iterator();

                   
                    String poi = "";
                    while (imagesIterator.hasNext()) {
                        FlickrImage image = imagesIterator.next();
                        String imageStr = "\"" + image.getPoi() + "\"" + "\t"
                                + image.getDate_taken().replace(' ', 'T') + "\t"
                                + image.getId() + "\t"
                                + image.getDescription().length() + "\t"
                                + (!image.getLatitude().equals("0") || !image.getLongitude().equals("0")) + "\t"
                                + image.getNbComments() + "\t"
                                + image.getTags().length() + "\t"
                                + image.getTitle().length() + "\t"
                                + image.getViews() + "\t"
                                + image.getUserid() + "\n";
                        bw.write(imageStr);
                    }
                }
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else { //for image or poi documents
            for (File xmlFile : xmlFiles) {
                try {
                    System.out.println("reading file:" + xmlFile.getAbsolutePath());
                    PointOfInterestSAXHandler handler = new PointOfInterestSAXHandler();
                    parser.parse(xmlFile, handler);
                    Iterator<FlickrImage> imagesIterator = handler.poi.iterator();

                    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + File.separator + xmlFile.getName()));
                    bw.write("<add>\n");
                    if (document.equalsIgnoreCase("image")) {
                        while (imagesIterator.hasNext()) {
                            bw.write(imagesIterator.next() + "\n");
                        }
                    } else if (document.equalsIgnoreCase("poi")) {
                        bw.write("<doc>");
                        String poi = "";
                        while (imagesIterator.hasNext()) {
                            FlickrImage image = imagesIterator.next();
                            String imageStr = "<field name=\"date_taken\">" + image.getDate_taken().replace(' ', 'T') + "Z</field>"
                                    + "<field name=\"description\">" + image.getDescription() + "</field> "
                                    + "<field name=\"id\">" + image.getId() + "</field> "
                                    + "<field name=\"location\">" + image.getLatitude() + "," + image.getLongitude() + "</field>"
                                    + "<field name=\"license\">" + image.getLicense() + "</field>"
                                    + "<field name=\"nbComments\">" + image.getNbComments() + "</field>"
                                    + "<field name=\"rank\">" + image.getRank() + "</field>"
                                    + "<field name=\"tags\">" + image.getTags() + "</field>"
                                    + "<field name=\"title\">" + image.getTitle() + "</field>"
                                    + "<field name=\"url_b\">" + image.getUrl_b() + "</field>"
                                    + "<field name=\"username\">" + image.getUsername() + "</field>"
                                    + "<field name=\"views\">" + image.getViews() + "</field>"
                                    + "<field name=\"userid\">" + image.getUserid() + "</field>";
                            poi = image.getPoi();
                            bw.write(imageStr + "\n");
                        }
                        bw.write("<field name=\"poi\">" + poi + "</field>");
                        bw.write("</doc>");
                    } else if (document.equalsIgnoreCase("userpoi")) {
                        throw new RuntimeException("User-poi indexing has not been implemented yet");
                    }

                    bw.write("</add>\n");
                    bw.close();

                } catch (IOException ex) {
                    Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }
}
