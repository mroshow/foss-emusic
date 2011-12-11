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
import android.util.Log;

/*
 * SAXParser implementation for eMusic Album/Book reviews API call.
 * Based roughly on the SAXParser tutorial at the anddev forum -
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * Assumed to be public domain.
 */

public class XMLHandlerReviews extends DefaultHandler{

    // Fields + Variables

    private int icount = 0;
    private boolean in_content = false;
    
    public int nItems = 0;
    public int nTotalItems = 0;
    public int statuscode = 200;
    public String[] reviews;
    public String[] authors;
    public String[] ratings;
    public String[] titles;

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
        if (localName.equals("review")) {
            titles[icount]=atts.getValue("title");
            reviews[icount]="";
        } else if (localName.equals("content")) {
            this.in_content = true;
        } else if (localName.equals("userRating")) {
            ratings[icount]=atts.getValue("average");
        } else if (localName.equals("user")) {
            authors[icount]=atts.getValue("userName");
        } else if (localName.equals("view")) {
            nTotalItems=Integer.parseInt(atts.getValue("total"));
        } else if (localName.equals("status")) {
            statuscode=Integer.parseInt(atts.getValue("code"));
        } else if (localName.equals("reviews")) {
            String nItemsString=atts.getValue("size");
            nItems = Integer.parseInt(nItemsString);
            titles = new String[nItems];
            authors = new String[nItems];
            reviews = new String[nItems];
            ratings = new String[nItems];
        }
    }

    //Gets be called on closing tags like: </tag>
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
     throws SAXException {
        if (localName.equals("content")) {
            this.in_content = false;
        } else if (localName.equals("review")) {
            icount++;
	}
    }

    //Gets be called on the following structure: <tag>characters</tag>
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_content)  {
            reviews[icount] += new String(ch, start, length);
        }
    }

}
