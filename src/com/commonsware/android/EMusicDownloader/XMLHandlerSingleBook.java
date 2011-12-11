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
 * SAXParser implementation for eMusic single book info API call.
 * Based roughly on the SAXParser tutorial at the anddev forum -
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * Assumed to be public domain.
 */

public class XMLHandlerSingleBook extends DefaultHandler{

    // Fields

    private boolean in_blurb = false;
    private boolean in_blurbs = false;
    
    public int statuscode = 200;
    public String blurb = "";
    public String blurbSource = "";
    public String rating = "";
    public String numberOfRatings = "";
    public String genre = "";
    public String narrator = "";
    public String author = "";
    public String authorId = "";
    public String publisher = "";
    public String releaseDate = "";
    public String edition = "";
    public String sampleURL = "";
    public String imageURL = "";

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
            genre=atts.getValue("name");
        } else if (localName.equals("publisher")) {
            publisher=atts.getValue("name");
        } else if (localName.equals("narrator")) {
            narrator=atts.getValue("name");
        } else if (localName.equals("author")) {
            author=atts.getValue("name");
            authorId=atts.getValue("id");
        } else if (localName.equals("blurbs")) {
            in_blurbs = true;
        } else if (localName.equals("status")) {
            statuscode=Integer.parseInt(atts.getValue("code"));
        } else if (localName.equals("blurb")) {
            in_blurb = true;
            if (in_blurbs) {
	        blurb = "";
                blurbSource=atts.getValue("source");
            }
        } else if (localName.equals("edition")) {
            edition=atts.getValue("name");
        } else if (localName.equals("communityRating")) {
            rating=atts.getValue("average");
            numberOfRatings=atts.getValue("votes");
        } else if (localName.equals("book")) {
            imageURL=atts.getValue("image");
            releaseDate=atts.getValue("published");
            sampleURL=atts.getValue("sample");
        }
    }

    //Gets be called on closing tags like: </tag>
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
     throws SAXException {
        if (localName.equals("blurb")) {
            in_blurb = false;
        } else if (localName.equals("blurbs")) {
            in_blurbs = false;
        }
    }

    //Gets be called on the following structure: <tag>characters</tag>
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_blurb && this.in_blurbs)  {
            blurb += new String(ch, start, length);
        }
    }

}
