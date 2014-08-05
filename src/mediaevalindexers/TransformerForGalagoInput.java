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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author adminuser
 */
public class TransformerForGalagoInput {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        String folderPath = args[0];
        File folder = new File(folderPath);
        File outputFolder = new File(folder.getParentFile().getAbsolutePath() + File.separator + folder.getName() + "4galago_image" );
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }

        File[] xmlFiles = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("xml");
            }
        });
        /*if (xmlFiles == null) {
         Logger.getLogger(MediaEvalIndexers.class.getName()).log(Level.SEVERE, "No files found", "No files found in " + folderPath);
         return;
         }*/
        for (File xmlFile : xmlFiles) {
            System.out.println("reading file:" + xmlFile.getAbsolutePath());
            PointOfInterestSAXHandler handler = new PointOfInterestSAXHandler();
            parser.parse(xmlFile, handler);
            String poi = "";
            Iterator<FlickrImage> imagesIterator = handler.poi.iterator();

//this iterates though all the images in a file
            while (imagesIterator.hasNext()) {
                FlickrImage image = imagesIterator.next();
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + File.separator + image.getId()+".xml"));

                String imageStr = "<DOC>"
                        + "<DOCNO>" + image.getId() + "</DOCNO> "
                        + "<date_taken>" + image.getDate_taken().replace(' ', 'T') + "Z</date_taken>"
                        + "<description>" + image.getDescription() + "</description> "
                        + "<location>" + image.getLatitude() + "," + image.getLongitude() + "</location>"
                        + "<license>" + image.getLicense() + "</license>"
                        + "<nbComments>" + image.getNbComments() + "</nbComments>"
                        + "<rank>" + image.getRank() + "</rank>"
                        + "<tags>" + image.getTags() + "</tags>"
                        + "<title>" + image.getTitle() + "</title>"
                        + "<url_b>" + image.getUrl_b() + "</url_b>"
                        + "<username>" + image.getUsername() + "</username>"
                        + "<views>" + image.getViews() + "</views>"
                        + "<userid>" + image.getUserid() + "</userid>"
                        + "<poi>" + image.getPoi() + "</poi>"
                        + "</DOC>";
                bw.write(imageStr + "\n");
                bw.close();
            }
        }

    }

}
