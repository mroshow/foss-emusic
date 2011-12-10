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

public class emuDB {

    private static final String CREATE_TABLE_COOKIETIME = "create table if not exists cookietime (cookietime_id integer primary key autoincrement, "
     + "time integer not null);";
    private static final String CREATE_TABLE_CACHETIME = "create table if not exists cachetime (cachetime_id integer primary key autoincrement, "
     + "time integer not null);";
    private static final String CREATE_TABLE_ARTTIME = "create table if not exists arttime (arttime_id integer primary key autoincrement, "
     + "time integer not null);";
    private static final String CREATE_TABLE_ALBUMCACHE = "create table if not exists albumcache (albumcache_id integer primary key autoincrement, "
     + "albumname text not null, albumurl text not null, albumartist text not null, albumdisplay text not null, "
     + "emusicid text not null);";
    private static final String CREATE_TABLE_BOOKCACHE = "create table if not exists bookcache (bookcache_id integer primary key autoincrement, "
     + "bookname text not null, bookurl text not null, bookauthor text not null, bookdisplay text not null, "
     + "emusicid text not null);";
    private static final String CREATE_TABLE_ARTISTCACHE = "create table if not exists artistcache (artistcache_id integer primary key autoincrement, "
     + "artistname text not null, artisturl text not null, "
     + "emusicid text not null);";
    private static final String COOKIETIME_TABLE = "cookietime";
    private static final String CACHETIME_TABLE = "cachetime";
    private static final String ARTTIME_TABLE = "arttime";
    private static final String ALBUMCACHE_TABLE = "albumcache";
    private static final String BOOKCACHE_TABLE = "bookcache";
    private static final String ARTISTCACHE_TABLE = "artistcache";
    private static final String DATABASE_NAME = "EMD";
    //private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public emuDB(Context ctx) {

        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            db.execSQL(CREATE_TABLE_ARTTIME);
            db.execSQL(CREATE_TABLE_CACHETIME);
            db.execSQL(CREATE_TABLE_ARTISTCACHE);
            db.execSQL(CREATE_TABLE_BOOKCACHE);
            db.execSQL(CREATE_TABLE_ALBUMCACHE);
            db.execSQL(CREATE_TABLE_COOKIETIME);
        } catch ( Exception e) {
        }

    }

    public boolean isLocked() {
        Boolean islocked;
        islocked = db.isDbLockedByOtherThreads();
        return islocked;
    }

    public boolean updateCookietime(long newtime) {
        db.delete(COOKIETIME_TABLE, "cookietime_id=" + getCookietimeId(), null);
        ContentValues values = new ContentValues();
        values.put("time",newtime);
        return (db.insert(COOKIETIME_TABLE, null, values) > 0);
    }

    public boolean updateCachetime(long newtime) {
        db.delete(CACHETIME_TABLE, "cachetime_id=" + getCachetimeId(), null);
        ContentValues values = new ContentValues();
        values.put("time",newtime);
        return (db.insert(CACHETIME_TABLE, null, values) > 0);
    }

    public boolean updateArttime(long newtime) {
        try {
            db.delete(ARTTIME_TABLE, "arttime_id=" + getArttimeId(), null);
        } catch (Exception e) {
        }
        ContentValues values = new ContentValues();
        values.put("time",newtime);
        return (db.insert(ARTTIME_TABLE, null, values) > 0);
    }

    public boolean insertAlbumCache(String albumname, String albumurl, String albumartist, String albumdisplay, String emusicid) {
        ContentValues values = new ContentValues();
        values.put("albumname", albumname);
        values.put("albumurl", albumurl);
        values.put("albumartist", albumartist);
        values.put("albumdisplay", albumdisplay);
        values.put("emusicid", emusicid);
        return (db.insert(ALBUMCACHE_TABLE, null, values) > 0);
    }

    public boolean insertBookCache(String bookname, String bookurl, String bookauthor, String bookdisplay, String emusicid) {
        ContentValues values = new ContentValues();
        values.put("bookname", bookname);
        values.put("bookurl", bookurl);
        values.put("bookauthor", bookauthor);
        values.put("bookdisplay", bookdisplay);
        values.put("emusicid", emusicid);
        return (db.insert(BOOKCACHE_TABLE, null, values) > 0);
    }

    public boolean insertArtistCache(String artistname, String artisturl, String emusicid) {
        ContentValues values = new ContentValues();
        values.put("artistname", artistname);
        values.put("artisturl", artisturl);
        values.put("emusicid", emusicid);
        return (db.insert(ARTISTCACHE_TABLE, null, values) > 0);
    }

    public boolean deleteAlbumCache(Long albumcacheId) {
        return (db.delete(ALBUMCACHE_TABLE, "albumcache_id=" + albumcacheId.toString(), null) > 0);
    }

    public boolean deleteBookCache(Long bookcacheId) {
        return (db.delete(BOOKCACHE_TABLE, "bookcache_id=" + bookcacheId.toString(), null) > 0);
    }

    public boolean deleteArtistCache(Long artistcacheId) {
        return (db.delete(ARTISTCACHE_TABLE, "artistcache_id=" + artistcacheId.toString(), null) > 0);
    }

    public void close() {
        db.close();
    }

    public long getCookietime() {
        long time=0;
        //long id;
        try {
            Cursor c = db.query(COOKIETIME_TABLE, new String[] { "cookietime_id", "time"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                //id = c.getLong(0);
                time = c.getLong(1);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
        }
        return time;
    }

    public long getCookietimeId() {
        //long time=0;
        long id=0;
        try {
            Cursor c = db.query(COOKIETIME_TABLE, new String[] { "cookietime_id", "time"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                id = c.getLong(0);
                //time = c.getLong(1);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
        }
        return id;
    }

    public long getCachetime() {
        long time=0;
        //long id;
        try {
            Cursor c = db.query(CACHETIME_TABLE, new String[] { "cachetime_id", "time"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                //id = c.getLong(0);
                time = c.getLong(1);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
        }
        return time;
    }

    public long getArttime() {
        long time=0;
        //long id;
        try {
            Cursor c = db.query(ARTTIME_TABLE, new String[] { "arttime_id", "time"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                //id = c.getLong(0);
                time = c.getLong(1);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
        }
        return time;
    }

    public long getCachetimeId() {
        //long time=0;
        long id=0;
        try {
            Cursor c = db.query(CACHETIME_TABLE, new String[] { "cachetime_id", "time"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                id = c.getLong(0);
                //time = c.getLong(1);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
        }
        return id;
    }

    public long getArttimeId() {
        //long time=0;
        long id=0;
        try {
            Cursor c = db.query(ARTTIME_TABLE, new String[] { "arttime_id", "time"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                id = c.getLong(0);
                //time = c.getLong(1);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
        }
        return id;
    }

    public List<AlbumCache> getAlbumCache() {
        ArrayList<AlbumCache> albumcaches = new ArrayList<AlbumCache>();
        try {
            Cursor c = db.query(ALBUMCACHE_TABLE, new String[] { "albumcache_id", "albumname",
             "albumurl","albumartist","albumdisplay","emusicid"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                AlbumCache albumcache = new AlbumCache();
                albumcache.albumcacheId = c.getLong(0);
                albumcache.albumname = c.getString(1);
                albumcache.albumurl = c.getString(2);
                albumcache.albumartist = c.getString(3);
                albumcache.albumdisplay = c.getString(4);
                albumcache.emusicid = c.getString(5);
                albumcaches.add(albumcache);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
            //Log.e("EMD", e.toString());
        }
        return albumcaches;
    }

    public int getAlbumCacheSize() {
        int size;
        try {
            Cursor c = db.query(ALBUMCACHE_TABLE, new String[] { "albumcache_id", "albumname",
             "albumurl","albumartist","albumdisplay","emusicid"}, null, null, null, null, null);
            size = c.getCount();
            c.close();
        } catch (SQLException e) {
            size = 0;
        }
        return size;
    }

    public List<BookCache> getBookCache() {
        ArrayList<BookCache> bookcaches = new ArrayList<BookCache>();
        try {
            Cursor c = db.query(BOOKCACHE_TABLE, new String[] { "bookcache_id", "bookname",
             "bookurl","bookauthor","bookdisplay","emusicid"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                BookCache bookcache = new BookCache();
                bookcache.bookcacheId = c.getLong(0);
                bookcache.bookname = c.getString(1);
                bookcache.bookurl = c.getString(2);
                bookcache.bookauthor = c.getString(3);
                bookcache.bookdisplay = c.getString(4);
                bookcache.emusicid = c.getString(5);
                bookcaches.add(bookcache);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
            //Log.e("EMD", e.toString());
        }
        return bookcaches;
    }

    public int getBookCacheSize() {
        int size;
        try {
            Cursor c = db.query(BOOKCACHE_TABLE, new String[] { "bookcache_id", "bookname",
             "bookurl","bookauthor","bookdisplay","emusicid"}, null, null, null, null, null);
            size = c.getCount();
            c.close();
        } catch (SQLException e) {
            size = 0;
        }
        return size;
    }

    public List<ArtistCache> getArtistCache() {
        ArrayList<ArtistCache> artistcaches = new ArrayList<ArtistCache>();
        try {
            Cursor c = db.query(ARTISTCACHE_TABLE, new String[] { "artistcache_id", "artistname",
             "artisturl","emusicid"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                ArtistCache artistcache = new ArtistCache();
                artistcache.artistcacheId = c.getLong(0);
                artistcache.artistname = c.getString(1);
                artistcache.artisturl = c.getString(2);
                artistcache.emusicid = c.getString(3);
                artistcaches.add(artistcache);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
            //Log.e("EMD", e.toString());
        }
        return artistcaches;
    }

    public int getArtistCacheSize() {
        int size;
        try {
            Cursor c = db.query(ARTISTCACHE_TABLE, new String[] { "artistcache_id", "artistname",
             "artisturl","emusicid"}, null, null, null, null, null);
            size = c.getCount();
            c.close();
        } catch (SQLException e) {
            size = 0;
        }
        return size;
    }

}
