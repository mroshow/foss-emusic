/*
    Droidian eMusic - a free eMusic app for Android
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
 * SAXParser implementation for eMusic API search of tracks.
 * Based roughly on the SAXParser tutorial at the anddev forum -
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * Assumed to be public domain.
 */

public class XMLHandlerTracks extends DefaultHandler{

     // Fields

    private int iCounter = 0;
    private int iTypeFlag = 0;

    public int nItems = 0;
    public int nTotalItems = 0;
    public int statuscode = 200;
    public String[] albums;
    public String[] tracks;
    public String[] artists;
    public String[] urls;
    public String[] images;
    public String[] artistURLs;
    public String[] albumIds;
    
    String[] artistType = {"artist","author"};

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
        if (localName.equals("track")) {
            tracks[iCounter]=atts.getValue("name");
        } else if (localName.equals("album")) {
            albumIds[iCounter]=atts.getValue("id");
	    albums[iCounter]=atts.getValue("name");
            urls[iCounter]=atts.getValue("url");
            images[iCounter]=atts.getValue("image");
        } else if (localName.equals("status")) {
            statuscode=Integer.parseInt(atts.getValue("code"));
        } else if (localName.equals("tracks")) {
            String nItemsString=atts.getValue("size");
            nItems = Integer.parseInt(nItemsString);
            albumIds = new String[nItems];
            albums = new String[nItems];
            artists = new String[nItems];
            urls = new String[nItems];
            tracks = new String[nItems];
            images = new String[nItems];
            artistURLs = new String[nItems];
        } else if (localName.equals("view")) {
            nTotalItems=Integer.parseInt(atts.getValue("total"));
        } else if (localName.equals(artistType[iTypeFlag])) {
            artists[iCounter]=atts.getValue("name");
            artistURLs[iCounter]=atts.getValue("url");
        }
    }

    //Gets be called on closing tags like: </tag>
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
     throws SAXException {

        if (localName.equals("albums")) {
        } else if (localName.equals("album")) {
        } else if (localName.equals("track")) {
            iCounter++;
        }
    }

    //Gets be called on the following structure: <tag>characters</tag>
    @Override
    public void characters(char ch[], int start, int length) {
        //if (this.in_description)  {
        // 	descriptions[icount] += new String(ch, start, length);
        //}
    }

}
