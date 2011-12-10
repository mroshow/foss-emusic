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

public class downloadDB {

    private static final String CREATE_TABLE_HISTORY = "create table if not exists history (history_id integer primary key autoincrement, "
     + "displaytext text not null, url text not null, urllocal text not null);";
    private static final String CREATE_TABLE_DOWNLOADS = "create table if not exists downloads (download_id integer primary key autoincrement, "
     + "artist text not null, album text not null, track text not null, imageurl text not null, trackurl text not null, number text not null, bookflag integer not null, status integer not null);";
    private static final String HISTORY_TABLE = "history";
    private static final String DOWNLOADS_TABLE = "downloads";
    private static final String DATABASE_NAME = "EMDDownloads";

    private SQLiteDatabase db;

    public downloadDB(Context ctx) {

        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            db.execSQL(CREATE_TABLE_DOWNLOADS);
            db.execSQL(CREATE_TABLE_HISTORY);
        } catch ( Exception e) {
        }

    }

    public boolean isLocked() {
        Boolean islocked;
        islocked = db.isDbLockedByOtherThreads();
        return islocked;
    }

    public boolean insertHistory(String displaytext, String url, String urllocal) {
        ContentValues values = new ContentValues();
        values.put("displaytext", displaytext);
        values.put("url", url);
        values.put("urllocal", urllocal);
        return (db.insert(HISTORY_TABLE, null, values) > 0);
    }

    public boolean insertDownload(String artist, String album, String track, String imageurl, String trackurl, String number, int bookflag, int status) {
        ContentValues values = new ContentValues();
        values.put("artist", artist);
        values.put("album", album);
        values.put("track", track);
        values.put("imageurl", imageurl);
        values.put("trackurl", trackurl);
        values.put("number", number);
        values.put("bookflag", bookflag);
        values.put("status", status);
        return (db.insert(DOWNLOADS_TABLE, null, values) > 0);
    }

    public boolean deleteHistory(Long historyId) {
        return (db.delete(HISTORY_TABLE, "history_id=" + historyId.toString(), null) > 0);
    }

    public boolean deleteDownload(Long downloadsId) {
        return (db.delete(DOWNLOADS_TABLE, "download_id=" + downloadsId.toString(), null) > 0);
    }

    public void close() {
        db.close();
    }

    public List<History> getHistory() {
        ArrayList<History> historys = new ArrayList<History>();
        try {
            Cursor c = db.query(HISTORY_TABLE, new String[] { "history_id", "displaytext",
             "url", "urllocal" }, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                History history = new History();
                history.historyId = c.getLong(0);
                history.displaytext = c.getString(1);
                history.url = c.getString(2);
                history.urllocal = c.getString(3);
                historys.add(history);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
	    //Log.e("EMD", e.toString());
        }
        return historys;
    }

    public List<Download> getDownloads() {
        ArrayList<Download> downloads = new ArrayList<Download>();
        try {
            Cursor c = db.query(DOWNLOADS_TABLE, new String[] { "download_id", "artist",
             "album", "track", "imageurl", "trackurl", "number", "bookflag", "status" }, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Download download = new Download();
                download.downloadId = c.getLong(0);
                download.artist = c.getString(1);
                download.album = c.getString(2);
                download.track = c.getString(3);
                download.imageurl = c.getString(4);
                download.trackurl = c.getString(5);
                download.number = c.getString(6);
                download.bookflag = c.getInt(7);
                download.status = c.getInt(8);
                downloads.add(download);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
	    //Log.e("EMD", e.toString());
        }
        return downloads;
    }

    public boolean updateDownload(Long downloadId, String artist, String album, String track, String imageurl, String trackurl, String number, int bookflag, int status) {
        ContentValues values = new ContentValues();
        values.put("artist", artist);
        values.put("album", album);
        values.put("track", track);
        values.put("imageurl", imageurl);
        values.put("trackurl", trackurl);
        values.put("number", number);
        values.put("bookflag", bookflag);
        values.put("status", status);
        return (db.update(DOWNLOADS_TABLE, values, "download_id=" + downloadId.toString(), null) > 0);
    }

}
