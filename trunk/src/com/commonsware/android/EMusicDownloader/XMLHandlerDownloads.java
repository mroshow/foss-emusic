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
import android.util.Log;

/*
 * SAXParser implementation for eMusic .emx xml file for downloading tracks.
 * Based roughly on the SAXParser tutorial at the anddev forum -
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * Assumed to be public domain.
 */

public class XMLHandlerDownloads extends DefaultHandler{

    // Flags for XML headers

    private boolean in_GENRE = false;
    private boolean in_TRACKURL = false;
    private boolean in_TRACKNUM = false;
    private boolean in_TITLE = false;
    private boolean in_TRACKCOUNT = false;
    private boolean in_ALBUM = false;
    private boolean in_ARTIST = false;
    private boolean in_ALBUMARTLARGE = false;
    private boolean in_ALBUMARTSMALL = false;
    
    public int nTracks = 0;
    public String artist="";
    public String album="";
    public String genre="";
    public String albumArt="";
    public String albumArtSml="";
    public String[] downloadURLs = new String[100];
    public String[] trackNumbers = new String[100];
    public String[] downloadNames = new String[100];

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
        if (localName.equals("TRACK")) {
            nTracks++;
            downloadURLs[nTracks-1]="";
            trackNumbers[nTracks-1]="";
            downloadNames[nTracks-1]="";
        } else if (localName.equals("TRACKCOUNT")) {
            this.in_TRACKCOUNT = true;
        } else if (localName.equals("TRACKURL")) {
            this.in_TRACKURL = true;
        } else if (localName.equals("TRACKNUM")) {
            this.in_TRACKNUM = true;
        } else if (localName.equals("ALBUM")) {
            this.in_ALBUM = true;
        } else if (localName.equals("GENRE")) {
            this.in_GENRE = true;
        } else if (localName.equals("ARTIST")) {
            this.in_ARTIST = true;
        } else if (localName.equals("TITLE")) {
            this.in_TITLE = true;
        } else if (localName.equals("ALBUMART")) {
            this.in_ALBUMARTSMALL = true;
        } else if (localName.equals("ALBUMARTLARGE")) {
            this.in_ALBUMARTLARGE = true;
        }
    }

    //Gets be called on closing tags like: </tag>
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
     throws SAXException {
        if (localName.equals("TRACK")) {
        } else if (localName.equals("TRACKCOUNT")) {
            this.in_TRACKCOUNT = false;
        } else if (localName.equals("TRACKURL")) {
            this.in_TRACKURL = false;
        } else if (localName.equals("TRACKNUM")) {
            this.in_TRACKNUM = false;
        } else if (localName.equals("ALBUM")) {
            this.in_ALBUM = false;
        } else if (localName.equals("GENRE")) {
            this.in_GENRE = false;
        } else if (localName.equals("ARTIST")) {
            this.in_ARTIST = false;
        } else if (localName.equals("TITLE")) {
            this.in_TITLE = false;
        } else if (localName.equals("ALBUMART")) {
            this.in_ALBUMARTSMALL = false;
        } else if (localName.equals("ALBUMARTLARGE")) {
            this.in_ALBUMARTLARGE = false;
        }
    }

    //Gets be called on the following structure: <tag>characters</tag>
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_TRACKCOUNT){
            if (nTracks == 1) {
                //Nothing to do here
            }
        } else if (this.in_ALBUM)  {
            if (nTracks == 1) {
                album += new String(ch, start, length);
            }
        } else if (this.in_GENRE)  {
            if (nTracks == 1) {
                genre += new String(ch, start, length);
            }
        } else if (this.in_ARTIST)  {
            if (nTracks == 1) {
                artist += new String(ch, start, length);
            }
        } else if (this.in_ALBUMARTLARGE)  {
            if (nTracks == 1) {
                albumArt += new String(ch, start, length);
            }
        } else if (this.in_ALBUMARTSMALL)  {
            if (nTracks == 1) {
                albumArtSml += new String(ch, start, length);
            }
        } else if (this.in_TRACKURL)  {
            if (nTracks <= 100) {
                downloadURLs[nTracks-1] += new String(ch, start, length);
            }
        } else if (this.in_TRACKNUM)  {
	    if (nTracks <= 100) {
                trackNumbers[nTracks-1] += new String(ch, start, length);
            }
        } else if (this.in_TITLE)  {
            if (nTracks <= 100) {
                downloadNames[nTracks-1] += new String(ch, start, length);
            }
        }
    }

}
