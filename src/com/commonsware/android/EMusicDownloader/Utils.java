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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

public class Utils {

    public static downloadDB droidDB = null;

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try {
            byte[] bytes=new byte[buffer_size];
            for(;;) {
               int count=is.read(bytes, 0, buffer_size);
               if(count==-1)
                   break;
               os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String getStorageDirectory(Context ctx) {

        // Discover if there is an sdcard or emmc internal storage or neither
        Boolean vStorage = false;
        String storageRoot=null;

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(ctx);

        int iMountDirPref=Integer.parseInt(prefs.getString("mountdirlist", "1"));
        Log.d("EMD","Mount preference "+iMountDirPref);

        if ( iMountDirPref != 1) {
            String customMountDirectoryPref=prefs.getString("custommountname", "/sdcard");
            Log.d("EMD","Mount custom "+customMountDirectoryPref);
            String msdir = ""+customMountDirectoryPref+"/emxfiles/";
            File mdir = new File (msdir);
            mdir.mkdir();
            if (mdir.exists()) {
                storageRoot=customMountDirectoryPref+"/emxfiles";
                File emxdir = new File(storageRoot);
                emxdir.mkdir();
                vStorage=true;
                Log.d("EMD","Successfully created "+msdir);
            } else {
                Log.d("EMD","Unable to create "+msdir);
            }
        }

        if (!vStorage) {
 
            String msdir = ""+Environment.getExternalStorageDirectory()+"/emxfiles/";
            File mdir = new File (msdir);
            mdir.mkdir();
            if (mdir.exists()) {
                storageRoot=Environment.getExternalStorageDirectory()+"/emxfiles";
                File emxdir = new File(storageRoot);
                emxdir.mkdir();
                vStorage=true;
            } else {
                File sddir = new File("/sdcard/emxfiles");
                sddir.mkdir();
                if (sddir.exists()) {
                    storageRoot="/sdcard/emxfiles";
                    File emxdir = new File(storageRoot);
                    emxdir.mkdir();
                    vStorage=true;
                } else {
                    File emdir = new File("/emmc/emxifiles");
                    emdir.mkdir();
                    if (emdir.exists()) {
                        storageRoot="/emmc/emxfiles";
                        File emxdir = new File(storageRoot);
                        emxdir.mkdir();
                        vStorage=true;
                    } else {
                        emdir = new File("/media/emxfiles");
                        emdir.mkdir();
                        if (emdir.exists()) {
                            storageRoot="/media/emxfiles";
                            File emxdir = new File(storageRoot);
                            emxdir.mkdir();
                            vStorage=true;
                        }
                    }
                }
            }
        }

        if (vStorage) {
            return storageRoot;
        } else {
            return null;
        }
    }

    public static String getTrackFileName(String inString, String newArtist, String newAlbum, String trackNum, Context thisContext) {
        String outString = inString;

//CHANGECHANGE
        if (outString == null) outString="No Name";

        if(!outString.contains("AlbumArt")) {
            outString = stringCleanUp(outString);
    
            SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(thisContext);
            int iFileNamePref=Integer.parseInt(prefs.getString("filenamelist", "1"));
            String fileNameSeparatorPref=prefs.getString("filenameseparator", " - ");

            outString = outString+".mp3";

            String tracknumstring = "";

            if (Integer.parseInt(trackNum) < 10) {
                tracknumstring = "0"+trackNum;
            } else {
                tracknumstring = ""+trackNum;
            }

            if (iFileNamePref == 1) {
                outString = tracknumstring+fileNameSeparatorPref+outString;
            } else if (iFileNamePref == 3) {
                outString = newAlbum+fileNameSeparatorPref+tracknumstring+fileNameSeparatorPref+outString;
            } else if (iFileNamePref == 2) {
                outString = newArtist+fileNameSeparatorPref+tracknumstring+fileNameSeparatorPref+outString;
            } else if (iFileNamePref == 4) {
                outString = newArtist+fileNameSeparatorPref+newAlbum+fileNameSeparatorPref+tracknumstring+fileNameSeparatorPref+outString;
            }
        }

        return outString;

    }

    public static String stringCleanUp(String inString) {
        String outString;
        outString = inString.replace(":","");
        outString = outString.replace("\"","");
        outString = outString.replace("?","");
        outString = outString.replace("/","");
        outString = outString.replace("*","");
        outString = outString.replace(">","");
        outString = outString.replace("<","");
        outString = outString.replace("|","");
        outString = outString.replace("`","");
        return outString;
    }

    public static String getAudioDirectory(Context ctx, String type) {
        String storageDir=null;

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(ctx);

        Integer iFileDirPref = null;

        if (type.equals("Book")) {
            iFileDirPref=Integer.parseInt(prefs.getString("bookdirlist", "1"));
        } else {
            iFileDirPref=Integer.parseInt(prefs.getString("filedirlist", "1"));
        }

        String customFileDirectoryPref=prefs.getString("customdirname", "eMusic");

        String tempdirstring="";

        if (iFileDirPref == 1) {
            tempdirstring = "eMusic";
        } else if (iFileDirPref == 2) {
            tempdirstring = "Music";
        } else if (iFileDirPref == 3) {
            tempdirstring = "AudioBooks";
        } else {
            //tempdirstring = customFileDirectoryPref.replace("/","");
            tempdirstring = customFileDirectoryPref;
        }

        String storageRoot = Utils.getStorageDirectory(ctx);
        Log.d("EMD - ","storageRoot "+storageRoot);

        if (storageRoot != null){
            storageDir=storageRoot.replace("emxfiles",tempdirstring+"/");
            try {
                File mdir = new File (storageDir);
                mdir.mkdir();
            } catch (Exception ef) {
            }
        }

        return storageDir;
    }

}
