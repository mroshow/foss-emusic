/*
    FOSS eMusic - a free eMusic app for Android
    This application is not associated with eMusic.com in any way.

    Copyright (C) 2010 Jack Deslippe

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package com.commonsware.android.EMusicDownloader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * SAXParser implementation for eMusic single album info API call.
 * Based roughly on the SAXParser tutorial at the anddev forum -
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * Assumed to be public domain.
 */

public class XMLHandlerSingleAlbum extends DefaultHandler{

    // Fields

    private boolean in_tracks = false;
    private boolean notin_styles = true;

    private int iCounter = 0;
    
    public int nItems = 0;
    public int nTotalItems = 0;
    public int numberOfDiscs = 1;
    public int statuscode = 200;
    public String artistId = "";
    public String artist = "";
    public String genre = "";
    public String genreId = "";
    public String label = "";
    public String labelId = "";
    public String rating = "";
    public String numberOfRatings = "";
    public String releaseDate = "";
    public String imageURL = "";
    public String[] tracks;
    //public String[] sampleAddress;
    //public Boolean[] sampleExists;
    //public Boolean samplesExist=false;

    // Methods

    @Override
    public void startDocument() throws SAXException {
        //Nothing to do
    }

    @Override
    public void endDocument() throws SAXException {
        //Nothing to do
    }

    //Gets be called on opening tags like: <tag>
    @Override
    public void startElement(String namespaceURI, String localName,
     String qName, Attributes atts) throws SAXException {
        if (localName.equals("genre")) {
            if (notin_styles) {
	        genre=atts.getValue("name");
	        genreId=atts.getValue("id");
            }
        } else if (localName.equals("discs")) {
            numberOfDiscs=Integer.parseInt(atts.getValue("size"));
            if (numberOfDiscs > 1) {
                nItems=nTotalItems;
                tracks = new String[nItems];
                //sampleAddress = new String[nItems];
                //sampleExists = new Boolean[nItems];
            }
        } else if (localName.equals("label")) {
            label=atts.getValue("name");
            labelId=atts.getValue("id");
        } else if (localName.equals("artist")) {
            if (!in_tracks) {
	        artistId=atts.getValue("id");
                artist=atts.getValue("name");
            }
        } else if (localName.equals("styles")) {
            notin_styles=false;
        } else if (localName.equals("status")) {
            statuscode=Integer.parseInt(atts.getValue("code"));
        } else if (localName.equals("tracks")) {
            if (numberOfDiscs == 1) {
                nItems=Integer.parseInt(atts.getValue("size"));
                tracks = new String[nItems];
                //sampleAddress = new String[nItems];
                //sampleExists = new Boolean[nItems];
            }
            in_tracks = true;
        } else if (localName.equals("track")) {
            tracks[iCounter]=atts.getValue("name");
            //sampleAddress[iCounter]=atts.getValue("sample");
            //sampleExists[iCounter]=false;
            //sampleExists[iCounter]=Boolean.parseBoolean(atts.getValue("sampleAvailable"));
	    //if (sampleAddress[iCounter].contains("m3u")) {
            //    sampleExists[iCounter] = true;
	    //}
	    //if (sampleExists[iCounter]) {
            //    samplesExist=true;
            //}
            iCounter++;
        } else if (localName.equals("communityRating")) {
            rating=atts.getValue("average");
            numberOfRatings=atts.getValue("total");
        } else if (localName.equals("album")) {
            imageURL=atts.getValue("image");
            releaseDate=atts.getValue("released");
            nTotalItems=Integer.parseInt(atts.getValue("tracksTotal"));
        }
    }

    //Gets be called on closing tags like: </tag>
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
     throws SAXException {
        if (localName.equals("styles")) {
            notin_styles = true;
        } else if (localName.equals("tracks")) {
            in_tracks = false;
        }
    }

    //Gets be called on the following structure: <tag>characters</tag>
    @Override
    public void characters(char ch[], int start, int length) {
        //Do Nothing
    }
}
