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

package DataObjects;

import java.util.Objects;

/**
 *
 * @author Mihai Lupu <mihai at mihailupu.net>
 */
public class FlickrImage {
    private String date_taken;
    private String description;
    private String id;
    private String latitude;
    private String license;
    private String longitude;
    private String nbComments;
    private int rank;
    private String tags;
    private String title;
    private String url_b;
    private String username;
    private int views;
    private String userid;
    private String poi;

    public FlickrImage(String poi) {
        this.poi = poi;
    }

    
    
    @Override
    public String toString() {
        return "<doc>" + "<field name=\"date_taken\">" + date_taken.replace(' ', 'T') + "Z</field>"
                + "<field name=\"description\">" + description + "</field> "
                + "<field name=\"id\">" + id + "</field> "
                + "<field name=\"location\">" + latitude+","+longitude + "</field>"
                + "<field name=\"license\">" + license + "</field>"                
                + "<field name=\"nbComments\">" + nbComments + "</field>"
                + "<field name=\"rank\">" + rank + "</field>"
                + "<field name=\"tags\">" + tags + "</field>"
                + "<field name=\"title\">" + title + "</field>"
                + "<field name=\"url_b\">" + url_b + "</field>"
                + "<field name=\"username\">" + username + "</field>"
                + "<field name=\"views\">" + views + "</field>"
                + "<field name=\"userid\">" + userid + "</field>"
                + "<field name=\"poi\">" + poi + "</field></doc>";
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FlickrImage other = (FlickrImage) obj;
        return Objects.equals(this.id, other.id);
    }
    
    public String getPoi() {
        return poi;
    }    
    
    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNbComments() {
        return nbComments;
    }

    public void setNbComments(String nbComments) {
        this.nbComments = nbComments;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl_b() {
        return url_b;
    }

    public void setUrl_b(String url_b) {
        this.url_b = url_b;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    
}
