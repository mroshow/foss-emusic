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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SearchWindow extends Activity implements AdapterView.OnItemSelectedListener, TextWatcher
{
    //UI-Views
    private EditText field;
    private Spinner categorySpinner;
    private Spinner searchSpinner;
    
    private String textEntryValue="";
    private String[] items={"Albums","Artists","Audiobooks","Labels","Tracks"};
    private String[] items_albums={"Title"};
    private String[] items_artists={"Name"};
    private String[] items_books={"Title"};
    private String[] items_tracks={"Title"};
    private String[] items_labels={"Name"};
    private int iSelected=0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        categorySpinner=(Spinner)findViewById(R.id.spinner1);
        categorySpinner.setOnItemSelectedListener(this);
        searchSpinner=(Spinner)findViewById(R.id.spinner2);
        searchSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(aa);

        updateLowerSpinners();

        field=(EditText)findViewById(R.id.field);
        field.addTextChangedListener(this);
    }

    public void updateLowerSpinners() {
        if (iSelected == 0) {
            ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items_albums);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(aa);
        } else if ( iSelected  == 1) {
            ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items_artists);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(aa);
        } else if ( iSelected == 2) {
            ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items_books);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(aa);
        } else if ( iSelected == 3) {
            ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items_labels);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(aa);
        } else {
            ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items_tracks);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            searchSpinner.setAdapter(aa);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        long idn = parent.getId();
        if (idn  == R.id.spinner1) {
            iSelected=position;
            updateLowerSpinners();
        } else if (idn == R.id.spinner2) {
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // needed for interface, but not used
    }

    public void onTextChanged(CharSequence s, int start, int before,int count) {
        //selection.setText(edit.getText());
        String tempt = ""; 
        tempt = field.getText().toString();
        if (textEntryValue != tempt) {
	    textEntryValue = tempt;
        }
    }

    public void beforeTextChanged(CharSequence s, int start,int count, int after) {
        // needed for interface, but not used
    }

    public void afterTextChanged(Editable s) {
        // needed for interface, but not used
    }

    public void pressedSearchButton(View button) {
        String query = "";
        query="term="+textEntryValue.replace(" ","+");

        String totalsearch="";
        totalsearch=query;

        Intent myIntent = new Intent(this,SearchListWindow.class);
        String tittext = "Search Results";
        myIntent.putExtra("keyname", tittext);
        String stringtype = "";

        if (iSelected == 0) {
            stringtype="album";
            totalsearch=totalsearch+"&imageSize=thumbnail";
        } else if (iSelected == 1) {
            stringtype="artist";
        } else if (iSelected == 2) {
            stringtype="book";
            totalsearch=totalsearch+"&imageSize=thumbnail";
        } else if (iSelected == 3) {
            stringtype="label";
        } else if (iSelected == 4) {
            stringtype="track";
            totalsearch=totalsearch+"&imageSize=thumbnail";
        }

        myIntent.putExtra("keytype", stringtype);
        String urlAddress = "http://api.emusic.com/"+stringtype+"/search?"+Secrets.apikey+"&"+totalsearch;
        myIntent.putExtra("keyurl", urlAddress);
        myIntent.putExtra("keyquery", totalsearch);
        startActivity(myIntent);
    }

    public void logoPressed(View buttoncover) {
        String browseurl = "http://www.emusic.com?fref=400062";
        Intent browseIntent = new Intent(this,WebWindowBrowse.class);
        browseIntent.putExtra("keyurl", browseurl);
        startActivity(browseIntent);
    }

}
