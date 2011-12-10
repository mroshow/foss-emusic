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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class favoriteDB {

    private static final String CREATE_TABLE_FAVORITES = "create table if not exists favorites (favorite_id integer primary key autoincrement, "
     + "displaytext text not null, type text not null, url text not null);";
    private static final String FAVORITES_TABLE = "favorites";
    private static final String DATABASE_NAME = "EMDFavorites";

    private SQLiteDatabase db;

    public favoriteDB(Context ctx) {

        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            db.execSQL(CREATE_TABLE_FAVORITES);
        } catch ( Exception e) {
        }

    }

    public boolean isLocked() {
        Boolean islocked;
        islocked = db.isDbLockedByOtherThreads();
        return islocked;
    }

    public boolean insertFavorite(String displaytext, String type, String url) {
        ContentValues values = new ContentValues();
        values.put("displaytext", displaytext);
        values.put("type", type);
        values.put("url", url);
        return (db.insert(FAVORITES_TABLE, null, values) > 0);
    }

    public boolean deleteFavorite(Long favoriteId) {
        return (db.delete(FAVORITES_TABLE, "favorite_id=" + favoriteId.toString(), null) > 0);
    }

    public void close() {
        db.close();
    }

    public List<Favorite> getFavorites() {
        ArrayList<Favorite> favorites = new ArrayList<Favorite>();
        try {
            Cursor c = db.query(FAVORITES_TABLE, new String[] { "favorite_id","displaytext","type",
             "url"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Favorite favorite = new Favorite();
                favorite.favoriteId = c.getLong(0);
                favorite.displaytext = c.getString(1);
                favorite.type = c.getString(2);
                favorite.url = c.getString(3);
                favorites.add(favorite);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
	    //Log.e("EMD", e.toString());
        }
        return favorites;
    }

}
