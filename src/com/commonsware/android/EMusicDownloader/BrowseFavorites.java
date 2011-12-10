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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import java.util.List;
import android.util.Log;

public class BrowseFavorites extends Activity implements
        AdapterView.OnItemClickListener {

    //UI-Views
    private TextView headerTextView;
    public ListView list;

    private int nItems;
    private String name;
    private String[] itemNames;
    private String[] itemTypes;
    private String[] itemURLs;

    private List<Favorite> favorites;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        Intent myIntent = getIntent();

        list = (ListView) findViewById(R.id.categorylist);

        updateListView();

    }

    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        Intent myIntent = new Intent(this,SearchListWindow.class);
        String stringtype = itemTypes[position];
        myIntent.putExtra("keytype", stringtype);
        String stringtitle = itemNames[position];
        myIntent.putExtra("keytitle", stringtitle);
        String query = itemURLs[position];
        String urlad = "http://api.emusic.com/"+stringtype+"/charts?"+Secrets.apikey+"&"+query;
        myIntent.putExtra("keyurl", urlad);
        myIntent.putExtra("keyquery", query);
        startActivity(myIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.browse);

        list = (ListView) findViewById(R.id.categorylist);

        updateListView();
    }

    public void logoPressed(View buttoncover) {
        String browseurl = "http://www.emusic.com?fref=400062";
        Intent browseIntent = new Intent(this,WebWindowBrowse.class);
        browseIntent.putExtra("keyurl", browseurl);
        startActivity(browseIntent);
    }

    public void updateListView() {
        try {
            favoriteDB droidDB = new favoriteDB(this);
            favorites = droidDB.getFavorites();
            droidDB.close();
            nItems = favorites.size();
        } catch (Exception ef) {
            nItems = 0;
            Log.d("EMD - ","Failed to get favorite list "+ef);
        }

        itemNames = new String[nItems];
        itemURLs = new String[nItems];
        itemTypes = new String[nItems];

        int icount = 0;
        for (Favorite favorite : favorites) {
            itemNames[icount] = favorite.displaytext;
            itemTypes[icount] = favorite.type;
            itemURLs[icount] = favorite.url;
            icount++;
        }

        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemNames));

        list.setOnItemClickListener(this);
        registerForContextMenu(list);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }

        favoriteDB droidDB = new favoriteDB(this);
        //favorites = droidDB.getFavorites();

        int icount = 0;
        for (Favorite favorite : favorites) {
            if (icount == info.position) {
                droidDB.deleteFavorite(favorite.favoriteId);
            }
            icount++;
        }

        droidDB.close();

        updateListView();

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        try {
        } catch (ClassCastException e) {
            return;
        }
        menu.add(0, 1000, 0, "Remove from Favorites");
    }

}
