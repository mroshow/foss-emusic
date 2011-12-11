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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
import java.util.List;
import java.io.File;

public class EditPreferences extends PreferenceActivity {
    private EditPreferences thisActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        thisActivity = this;

        Preference myPref = findPreference("clearcache");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                LazyAdapter adapter;
                adapter=new LazyAdapter(thisActivity);
                adapter.imageLoader.clearCache();

                long currenttime=0;
                emuDB droidDB = new emuDB(thisActivity);
                if (droidDB.isLocked()){
                    Toast.makeText(thisActivity, R.string.database_locked,
                     Toast.LENGTH_SHORT).show();
                } else {
                    droidDB.updateCachetime(currenttime);
                    droidDB.close();
                    Toast.makeText(thisActivity, R.string.cleared_cache,
                     Toast.LENGTH_SHORT).show();
                }
                adapter.nullActivity();
                adapter=null;
                //thisActivity=null;
                return false;
            }
        });

        myPref = findPreference("clearhistory");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                String storageRoot = Utils.getStorageDirectory(thisActivity);

                if (storageRoot != null){
                    try {
                        File dir = new File(storageRoot);
                        String[] children = dir.list();
                        if (children != null) {
                            for (int i=0; i<children.length; i++) {
                                String filename = children[i];
                                File f = new File(storageRoot+"/"+filename);
                                if (f.exists()) {
                                    f.delete();
                                }
                            }
                        }
                    } catch (Exception eff) {
                    }
                }

                downloadDB droidDB = new downloadDB(thisActivity);
                if (droidDB.isLocked()){
                    Toast.makeText(thisActivity, R.string.database_locked,
                     Toast.LENGTH_SHORT).show();
                } else {
                    List<History> historys = droidDB.getHistory();
                    try {
                        for (History history : historys) {
                            droidDB.deleteHistory(history.historyId);
                        }
                        Toast.makeText(thisActivity, R.string.deleted_download_emx_history,
                         Toast.LENGTH_SHORT).show();
                    } catch (Exception eff) {
                        Toast.makeText(thisActivity, R.string.database_locked,
                         Toast.LENGTH_SHORT).show();
                    }
                }
                droidDB.close();
                return false;
            }
        });

    }
}
