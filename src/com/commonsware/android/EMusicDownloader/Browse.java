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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.res.Configuration;
import android.widget.Toast;

public class Browse extends Activity implements
        AdapterView.OnItemClickListener {

    //UI-Views
    private TextView headerTextView;
    public ListView list;

    private String name;
    private String[] items;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        //Intent myIntent = getIntent();
        //name = myIntent.getStringExtra("keyname");
        //urls = myIntent.getStringArrayExtra("keyurls");
        //items = myIntent.getStringArrayExtra("keyitems");
        //shortItems = myIntent.getStringArrayExtra("keyshortitems");

        list = (ListView) findViewById(R.id.categorylist);

        items = Genres.genres;

        list.setAdapter(new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, items));

        list.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> a, View v, int position, long id) {

        if (position != 2 && position !=0 ) {
            Intent myIntent = new Intent(this, BrowseSubcategory.class);
            myIntent.putExtra("keyposition", position);
            startActivity(myIntent);
        } else if (position == 2) {
            Intent myIntent = new Intent(this,SearchListWindow.class);
            String stringtype = "book";
            myIntent.putExtra("keytype", stringtype);
            String stringtitle = "Genre: "+items[position];
            myIntent.putExtra("keytitle", stringtitle);
            String query="";
            String urlad = "http://api.emusic.com/book/charts?"+Secrets.apikey+"&"+query;
            myIntent.putExtra("keyurl", urlad);
            myIntent.putExtra("keyquery", query);
            startActivity(myIntent);
        } else {
            //Toast.makeText(this, "Favorite labels, artists, searches coming soon!",
            // Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(this, BrowseFavorites.class);
            startActivity(myIntent);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.browse);

        list = (ListView) findViewById(R.id.categorylist);

        list.setAdapter(new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, items));

        list.setOnItemClickListener(this);
    }

    public void logoPressed(View buttoncover) {
        String browseurl = "http://www.emusic.com?fref=400062";
        Intent browseIntent = new Intent(this,WebWindowBrowse.class);
        browseIntent.putExtra("keyurl", browseurl);
        startActivity(browseIntent);
    }

}
