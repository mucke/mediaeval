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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author Mihai Lupu <mihai at mihailupu.net>
 */
public class PointOfInterest {

    private String name;
    private ArrayList<FlickrImage> flickrImages;

    @Override
    public String toString() {
        return "PointOfInterest{" + "name=" + name + ", flickrImages=" + flickrImages + '}';
    }

    
    
    public PointOfInterest() {
        this.name = "empty";
        flickrImages = new ArrayList<>();
    }

    public boolean isEmpty() {
        return flickrImages.isEmpty();
    }

    public boolean contains(FlickrImage o) {
        return flickrImages.contains(o);
    }

    public FlickrImage get(int index) {
        return flickrImages.get(index);
    }

    public boolean add(FlickrImage e) {
        return flickrImages.add(e);
    }

    public boolean remove(FlickrImage o) {
        return flickrImages.remove(o);
    }

    public Iterator<FlickrImage> iterator() {
        return flickrImages.iterator();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.name);
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
        final PointOfInterest other = (PointOfInterest) obj;
        return Objects.equals(this.name, other.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
