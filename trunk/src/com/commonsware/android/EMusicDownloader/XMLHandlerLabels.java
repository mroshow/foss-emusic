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
 * SAXParser implementation for eMusic artists search API call.
 * Based roughly on the SAXParser tutorial at the anddev forum -
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * Assumed to be public domain.
 */

public class XMLHandlerLabels extends DefaultHandler{

    // Fields + variables

    private int iCounter = 0;
    private boolean vStarted = false;
    
    public int nItems = 0;
    public int nTotalItems = 0;
    public int statuscode = 200;
    public String[] labelsId;
    public String[] labels;
    public String[] urls;

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
        if (localName.equals("label") && vStarted) {
            labels[iCounter]=atts.getValue("name");
            urls[iCounter]=atts.getValue("url");
            labelsId[iCounter]=atts.getValue("id");
            iCounter++;
        } else if (localName.equals("status")) {
            statuscode=Integer.parseInt(atts.getValue("code"));
        } else if (localName.equals("labels")) {
            String nItemsString=atts.getValue("size");
            vStarted=true;
            nItems = Integer.parseInt(nItemsString);
            labelsId = new String[nItems];
            labels = new String[nItems];
            urls = new String[nItems];
        } else if (localName.equals("view")) {
            nTotalItems=Integer.parseInt(atts.getValue("total"));
        }
    }

    //Gets be called on closing tags like: </tag>
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
     throws SAXException {
        // Nothing to do
    }

    //Gets be called on the following structure: <tag>characters</tag>
    @Override
    public void characters(char ch[], int start, int length) {
        // Nothing to do
    }

}
