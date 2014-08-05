/*
 * Copyright (C) 2014 Mihai Lupu <mihai at mihailupu.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mediaevalindexers;

import DataObjects.FlickrImage;
import DataObjects.PointOfInterest;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Mihai Lupu <mihai at mihailupu.net>
 */
class PointOfInterestSAXHandler extends DefaultHandler{
    private static final Logger LOG = Logger.getLogger(PointOfInterestSAXHandler.class.getName());
    PointOfInterest poi = new PointOfInterest();

    public PointOfInterest getPoi() {
        return poi;
    }
    
  FlickrImage fImage ;  
  @Override
  //Triggered when the start of tag is found.
  public void startElement(String uri, String localName, 
                           String qName, Attributes attributes) 
                           throws SAXException {

    switch(qName){
      case "photos":
        poi.setName(attributes.getValue("monument"));
        break;
      case "photo":
          fImage = new FlickrImage(poi.getName());
          fImage.setDate_taken(attributes.getValue("date_taken"));
          //& is a problem. Often is part of special characters like &nbsp; or &amp; or &gt;, but sometimes it is on its own. 
          //the regular expression with lookahead checks and replaces only those & which are NOT followed by between 2 and 4 lowercase letters and a semi-column.
          //it will replace string like &.. &a.. &a; but not strings like &ab; &abc;, or &abcd;
          fImage.setDescription(removeNonXMLCharacters(attributes.getValue("description").replace("<", "&lt;").replace(">", "&gt;").replaceAll("&(?![a-z]{2,4};)", "&amp;")));
          fImage.setId(attributes.getValue("id"));
          fImage.setLatitude(attributes.getValue("latitude"));
          fImage.setLicense(attributes.getValue("license"));
          fImage.setLongitude(attributes.getValue("longitude"));
          fImage.setNbComments(attributes.getValue("nbComments"));
          fImage.setRank(Integer.parseInt(attributes.getValue("rank")!=null?attributes.getValue("rank"):"-1"));
          fImage.setTags(removeNonXMLCharacters(attributes.getValue("tags")));
          fImage.setTitle(removeNonXMLCharacters(attributes.getValue("title").replaceAll("&(?![a-z]{2,4};)", "&amp;").replace("<", "&lt;").replace(">", "&gt;")));
          fImage.setUrl_b(attributes.getValue("url_b"));
          fImage.setUserid(attributes.getValue("userid"));
          fImage.setUsername(removeNonXMLCharacters(attributes.getValue("username").replaceAll("&(?![a-z]{2,4};)", "&amp;").replace("<", "&lt;").replace(">", "&gt;")));          
          poi.add(fImage);
    }
  }
  
  String removeNonXMLCharacters(String s){
      return s.replace("&nbsp;", " ").replace("&copy;", " ").replace("&trade;"," ").replace("&bull;", " ").replace("&mdash;", "-").replace("&sim;", " ").replace("&sub;"," ");
  }
}
