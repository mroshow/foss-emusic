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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class Downloader extends Service {

    private Downloader thisService;
    private static DownloadManager EMD = null;
    
    public WakeLock w1;
    public WifiManager.WifiLock wl;

    // Variables for Taskbar notification

    public Notification notification;
    public Notification notificationcomplete;
    public NotificationManager mNotificationManager;
    public int NOTI_ID;
    public int NOTI_ID2;

    private Intent notificationIntent2;

    public static String XMLAddress;
    public static String[] fileNames;
    public static String[] trackNames;
    public static String[] trackURLs;
    public static String[] trackArtists;
    public static String[] trackImageURLs;
    public static Long[] trackIds;
    public static String[] trackAlbums;
    public static String[] trackNums;
    public static int[] trackBookFlags;
    public static Boolean vCancel = false;
    public static Boolean vLoop = false;
    public static int nFiles;
    public static int[] trackStatus;

    public final String mimetype = "audio/mpeg";

    public Context context;
    public PendingIntent contentIntent;
    public PendingIntent contentIntent2;

    private String statString;
    private Boolean vKilled = false;

    private static final Class[] mStartForegroundSignature = new Class[] {
     int.class, Notification.class};
    private static final Class[] mStopForegroundSignature = new Class[] {
     boolean.class};
    private static final Class<?>[] mSetForegroundSignature = new Class[] {
     boolean.class};

    private Method mStartForeground;
    private Method mStopForeground;
    private Method mSetForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    private Object[] mSetForegroundArgs = new Object[1];
    private static Thread downloadThread;
    private CharSequence originalAlbumText;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("EMD - ","Download Manager Starting");

        NOTI_ID = 234050;
        NOTI_ID2 = 234051;
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);

        thisService = this;

    	try {
            mStartForeground = getClass().getMethod("startForeground",
             mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",
             mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
    	}

        try {
            mSetForeground = getClass().getMethod("setForeground",
                mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            //throw new IllegalStateException(
            //    "OS doesn't have Service.startForeground OR Service.setForeground!");
            mSetForeground = null;
        }

        int icon = R.drawable.iconnotify;
        CharSequence tickerText = getString(R.string.app_name);
        long when = System.currentTimeMillis();
        notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationcomplete = new Notification(icon, tickerText, when);
        notificationcomplete.flags |= Notification.FLAG_AUTO_CANCEL;

        context = getApplicationContext();

        Intent notificationIntent = new Intent(this, DownloadManager.class);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notificationIntent2 = new Intent(this, DownloadManager.class);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //WakeLock w1 = pm.newWakeLock(pm.FULL_WAKE_LOCK, "EMD");
        w1 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EMD");

        wl = null;
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wl = manager.createWifiLock("wifilock");

        try {

            int nFilesNew = getFileStatus();

            notification.setLatestEventInfo(context, getString(R.string.app_name), "Beginning Downloads", contentIntent);

            if (nFilesNew > 0) {
               startForegroundCompat(NOTI_ID,notification);
               performDownloads();
            } else {
               vLoop = false;

               if (EMD != null) {
                   EMD.vLoop = false;
                   EMD.statusTextView.post(new Runnable() {
                       public void run() {
                           EMD.statusTextView.setText("Downloads complete. Restart with button above.");
                       }
                   });
                   EMD.pauseButton.post(new Runnable() {
                       public void run() {
                           EMD.pauseButton.setImageResource(R.drawable.restart);
                       }
                   });
               }

               stopSelf();
            }
        } catch (Exception e) {
            stopForegroundCompat(NOTI_ID);
            Toast.makeText(context,R.string.downloads_killed,
             Toast.LENGTH_SHORT).show();
            notificationcomplete.setLatestEventInfo(context, getString(R.string.app_name), getString(R.string.downloads_killed_noti), contentIntent);
            mNotificationManager.notify(NOTI_ID2, notificationcomplete);

            if (EMD != null) {
                try {
                    EMD.statusTextView.post(new Runnable() {
                        public void run() {
                            EMD.statusTextView.setText("Downloads complete. Restart with button above.");
                        }
                    });
                    EMD.pauseButton.post(new Runnable() {
                        public void run() {
                            EMD.pauseButton.setImageResource(R.drawable.restart);
                        }
                    });
                } catch (Exception e2) {
                }
            }

            stopSelf();
        }
    }

    public static void setMainActivity(DownloadManager activity) {
        EMD = activity;
    }

    public static void setvLoop(Boolean value) {
        vLoop = value;
    }

    public static void setvCancel(Boolean value) {
        vCancel = value;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vLoop=false;
        vKilled=true;
        if (  w1.isHeld() ) {
            try {
                w1.release();
            } catch (Exception elock) {
            }
        }
        if (  wl.isHeld() ) {
            try {
                wl.release();
            } catch (Exception elock) {
            }
        }
        stopForegroundCompat(NOTI_ID);
    }


    private void performDownloads() {

        downloadThread = new Thread() {
            public void run() {

               w1.acquire();
               wl.acquire();

               int nFilesNew = getFileStatus();

               while (vLoop && !vCancel && nFilesNew > 0) {

                       Log.d("EMD - ","Start DL Loop "+nFiles);
                       for (int ifile = 0; ifile < nFiles; ifile++) {
                           final int it = ifile;
                           //testExpired(ifile);

                           Log.d("EMD - ","In DL Loop "+ifile+" "+trackStatus[ifile]);
                           if (trackStatus[ifile] > 9 && vLoop == true && vCancel == false && !trackCanceled(ifile)) {
                           testExpired(ifile);
                           if (trackStatus[ifile] > 9 &&  trackStatus[ifile] < 16 && vLoop == true && vCancel == false) {

                               updateStatString(ifile,nFiles);

                               if (vLoop) {
                                   notification.setLatestEventInfo(context, getString(R.string.app_name), statString, contentIntent);
                                   mNotificationManager.notify(NOTI_ID, notification);
                               }

                               String artist = trackArtists[ifile];
                               String album = trackAlbums[ifile];

                               final int k = ifile + 1;
                               final String fileName=Utils.getTrackFileName(trackNames[ifile],trackArtists[ifile],trackAlbums[ifile],trackNums[ifile],thisService);
                               long i = 0;

                               String emusicPath = null;

                               if (trackBookFlags[ifile] == 1) {
                                   emusicPath=Utils.getAudioDirectory(thisService,"Book");
                               } else {
                                   emusicPath=Utils.getAudioDirectory(thisService,"Music");
                               }

                               try {
                                   final String filePath=emusicPath+artist+"/"+album+"/";
                                   File fare = new File(emusicPath);
               	                   fare.mkdirs();
                                   File fart = new File(emusicPath+artist);
                                   fart.mkdir();
                                   File falb = new File(emusicPath+artist+"/"+album);
                                   falb.mkdir();

                                   long itmp = 0;
                                   Boolean vPartial = false;
                                   File futureFile = new File(filePath,fileName);
                                   if (futureFile.exists()) {
                                       itmp = futureFile.length();
                                       if (itmp > 1024*200) {
                                           vPartial = true;
                                       } else {
                                           itmp=0;
                                       }
                                   }

                                   Log.d("EMD","Download Process: vPartial "+vPartial);

                                   if (trackStatus[ifile] > 9 && trackStatus[ifile]%2 == 0) {
                                       trackStatus[ifile]++;
                                       updateDB(ifile);
                                   }

                                   URL currentTrackURL = new URL(trackURLs[ifile]);
                                   HttpURLConnection currentTrackConnection = (HttpURLConnection) currentTrackURL.openConnection();

                                   if (vPartial) {
                                      Log.d("EMD","Partial Download "+itmp);
                                       currentTrackConnection.addRequestProperty("Range", "bytes=" + itmp + "-");
                                   }

                                   Log.d("EMD","Set Up HttpURLConnection z"+trackURLs[ifile]+"z");

                                   currentTrackConnection.setRequestMethod("GET");
                                   //currentTrackConnection.setDoOutput(true);
                                   currentTrackConnection.setFollowRedirects(true);
                                   currentTrackConnection.setInstanceFollowRedirects(true);
                                   currentTrackConnection.connect();

                                   Log.d("EMD","Set Up TrackConnection");

                                   long itfs = 0;
                                   itfs = currentTrackConnection.getContentLength();

                                   Log.d("EMD - ","Content Length "+itfs);

                                   if (itfs == -1) {
                                       currentTrackConnection.disconnect();
                                       currentTrackConnection = (HttpURLConnection) currentTrackURL.openConnection();
                                       if (vPartial) {
                                           currentTrackConnection.addRequestProperty("Range", "bytes=" + itmp + "-");
                                       }
                                       currentTrackConnection.setRequestMethod("GET");
                                       //currentTrackConnection.setDoOutput(true);
                                       currentTrackConnection.connect();
                                       itfs = currentTrackConnection.getContentLength();
                                   }

                                   FileOutputStream f;
                                   if (vPartial) {
                                       f = new FileOutputStream(futureFile,true);
                                   } else {
                                       f = new FileOutputStream(futureFile,false);
                                   }
                                   final long ifs = itfs + itmp;

                                   Log.d("EMD - ","Setup Output Stream ");

                                   if (EMD != null) {
                                       EMD.statusTextView.post(new Runnable() {
                                           public void run() {
                                               EMD.statusTextView.setText(getString(R.string.beginning)+" "+fileName+" "+k+" "+getString(R.string.of)+" "+nFiles);
                                           }
                                       });
                                   }

                                   InputStream in = currentTrackConnection.getInputStream();

                                   Log.d("EMD - ","Setup Input Stream ");

                                   final long jfs = ifs*100/1024/1024;
                                   final double rfs = (double) jfs/100.0;

                                   Boolean vmarkable=in.markSupported();

                                   Log.d("EMD - ","Is it markable: "+vmarkable);

                                   byte[] buffer = new byte[1024];
                                   int len1 = 0;
                                   if (itmp > 0) {
                                       i = itmp;
                                   } else {
                                       i = 0;
                                   }
                                   long jtprev=-1;

                                   while ( (len1 = in.read(buffer)) > 0 ) {
                                       if (vLoop == false || vCancel == true || trackCanceled(ifile)) {
                                           if (vCancel == true) {
                                               vLoop = false;
                                           }
                                           if ( vKilled ) {
                                               return;
                                           }
                                           break;
                                       }
                                       f.write(buffer,0,len1);
                                       i+=len1;
                                       long jt = 100*i/ifs;

                                       if (jt != jtprev) {
                                           jtprev=jt;
                                           final int j = (int) jt;
                                           if (EMD != null) {
                                               EMD.statusTextView.post(new Runnable() {
            	                                   public void run() {
                                                       EMD.statusTextView.setText(getString(R.string.downloading)+" "+k+" "+getString(R.string.of)+" "+EMD.nFiles+" - "+j+"% "+getString(R.string.of)+" "+rfs+"MB");
                                                   }
                                               });
                                           }
                                           updateListViewStatus(" "+j+"%",it);
                                       }
                                   }
                                   f.close();

                                   //Check to make sure we didn't cancel on last iteration of loop
                                   if (vCancel == true) {
                                       vLoop=false;
                                       if ( vKilled ) {
                                           return;
                                       }
                                   }

                                   if (vLoop == false) {
                                       trackStatus[ifile]--;

                                       updateDB(ifile);
                                       updateListViewStatus("Inc.",it);

                                       final int j = 0;

                                       if (EMD != null) {
                                           EMD.statusTextView.post(new Runnable() {
                                               public void run() {
                                                   EMD.statusTextView.setText(getString(R.string.canceled_during)+" "+k+" "+getString(R.string.of)+" "+nFiles+" - " + rfs + "MB");
                                               }
                                           });
                                       }
                                   } else {
                                       long jt = 100*i/ifs;
                                       if (jt > 98) {
                                           trackStatus[ifile]=1;

                                           updateDB(ifile);
                                           updateListViewStatus("\u2714",it);

                                           //Media Scanner
                                           if ( !fileName.contains("AlbumArt") ) {
                                               scanMedia(filePath+fileName);
                                           }
                                       } else if (!trackCanceled(ifile)) {
                                           trackStatus[ifile]++;

                                           updateDB(ifile);
                                           updateListViewStatus("Inc.",it);
                                       } else {
                                           trackStatus[ifile]=16;
                                       }
                                       if (EMD != null) {
                                           EMD.statusTextView.post(new Runnable() {
                                               public void run() {
                                                   EMD.statusTextView.setText(getString(R.string.finished)+" "+k+" "+getString(R.string.of)+" "+nFiles+" - " + rfs + "MB");
                                               }
                                           });
                                       }
                                   }

                               } catch (Exception e) {
                                   Log.d("EMD - ","Download Failed "+e);
                                   String exceptionString = ""+e;
                                   if (exceptionString.contains("No space left")) {
                                     Message handlermsg = new Message();
                                     String ttext = "No space left on storage device.";
                                     handlermsg.obj = ttext;
                                     handlerToast.sendMessage(handlermsg);
                                   }

                                   //final Exception ef = e;
                                   if (EMD != null) {
                                       EMD.statusTextView.post(new Runnable() {
                                           public void run() {
                                               //EMD.txt.setText("Failed to download "+filename+"\n");
                                               EMD.statusTextView.setText(getString(R.string.failed_to_download)+" "+fileName);
                                           }
                                       });
                                   }

                                   if (true) {
                                       trackStatus[ifile]++;

                                       updateDB(ifile);
                                       if (trackStatus[it] < 16) {
                                           updateListViewStatus("Inc.",it);
                                       } else {
                                           updateListViewStatus("\u2716",it);
                                       }
                                       if (EMD != null) {
                                           EMD.statusTextView.post(new Runnable() {
                                               public void run() {
                                                   //EMD.txt.setText("Failed to download "+filename+"\n");
                                                   if (trackStatus[it] < 16) {
                                                       EMD.statusTextView.setText(getString(R.string.failed_to_download)+" "+fileName+" "+getString(R.string._will_automatically_try_again_after_all_downloads_finish_));
                                                   } else {
                                                       EMD.statusTextView.setText(getString(R.string.failed_to_download)+" "+fileName+" "+getString(R.string._to_try_again_manually_press_restart_after_all_downloads_finish_));
                                                   }
                                               }
                                           });
                                       }
                                   }
                               }

                               //Notification Update Here.
                               updateStatString(ifile,nFiles);
                               if (vLoop) {
                                   notification.setLatestEventInfo(context, getString(R.string.app_name), statString, contentIntent);
                                   mNotificationManager.notify(NOTI_ID, notification);
                               }
                               System.gc();

                           }
                           }

                       }

                       nFilesNew = getFileStatus();

                       if (vLoop && nFilesNew > 0 ){
                           Log.d("EMD - ",getString(R.string.not_finished_yet));
                           if (EMD != null) {
                               EMD.statusTextView.post(new Runnable() {
                                   public void run() {
                                       //EMD.txt.setText("Failed to download "+filename+"\n");
                                       EMD.statusTextView.setText(R.string.waiting_10_seconds);
                                   }
                               });
                           }
                           try {
                               sleep(10000);
                           } catch (Exception ep) {
                           }
                       }
               }

               updateStatString(nFiles,nFiles);

               if (vLoop) {
                   notificationIntent2.setData(Uri.parse("DLFinished"));
                   contentIntent2 = PendingIntent.getActivity(thisService, 0, notificationIntent2, 0);
                   notificationcomplete.setLatestEventInfo(context, "Droidian eMusic", "Finished Downloading\n"+statString, contentIntent2);
                   //notificationcomplete.setLatestEventInfo(context, "EMusic Droid", "HELLO", contentIntent2);
                   mNotificationManager.notify(NOTI_ID2, notificationcomplete);
                   if (EMD != null) {
                       EMD.statusTextView.post(new Runnable() {
                           public void run() {
                               EMD.statusTextView.setText("Downloads complete. Restart with button above.");
                           }
                       });
                   }
               }

               vLoop = false;

               if (EMD != null) {
                   EMD.vLoop = false;
                   EMD.pauseButton.post(new Runnable() {
                       public void run() {
                           EMD.pauseButton.setImageResource(R.drawable.restart);
                       }
                   });
               }

               if (  w1.isHeld() ) {
                   try {
                       w1.release();
                   } catch (Exception elock) {
                   }
               }
               if (  wl.isHeld() ) {
                   try {
                       wl.release();
                   } catch (Exception elock) {
                   }
               }

               stopForegroundCompat(NOTI_ID);
               stopSelf();
           }
        };
        downloadThread.start();
    }

    public void scanMedia(String filetoscan) {
        new MediaScannerNotifier(context,filetoscan,mimetype);
    }

    void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            try {
                mStartForeground.invoke(this, mStartForegroundArgs);
            } catch (Exception e) {
                Log.d("EMD - ", "Unable to invoke startForeground", e);
            }
            return;
        }
        // Fall back on the old API.
        //setForeground(true);
        mSetForegroundArgs[0] = Boolean.TRUE;
        try {
            mSetForeground.invoke(this, mSetForegroundArgs);
        } catch (Exception ef) {
        }
        mNotificationManager.notify(NOTI_ID, notification);
    }

    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            try {
                mStopForeground.invoke(this, mStopForegroundArgs);
            } catch (Exception e) {
                Log.d("EMD - ", "Unable to invoke stopForeground", e);
            }
            return;
        }

        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        //setForeground(false);
        mSetForegroundArgs[0] = Boolean.FALSE;
        try {
            mSetForeground.invoke(this, mSetForegroundArgs);
        } catch (Exception ef) {
        }
        mNotificationManager.cancel(NOTI_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return(null);
    }

    private Handler handlerToast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Object texttotoast = msg.obj;
            Toast.makeText(context, ""+texttotoast,
             Toast.LENGTH_LONG).show();
        }
    };

    private void updateDB(int ifile) {
        //downloadDB droidDB = new downloadDB(thisService);
        try {
            while (Utils.droidDB.isLocked()) {
                Thread.currentThread().sleep(1000);
            }
            if (Utils.droidDB.isLocked()) {
                Toast.makeText(thisService, R.string.database_locked,
                 Toast.LENGTH_SHORT).show();
            } else {
                Utils.droidDB.updateDownload(trackIds[ifile],trackArtists[ifile],trackAlbums[ifile],trackNames[ifile],
                 trackImageURLs[ifile],trackURLs[ifile],trackNums[ifile],trackBookFlags[ifile],trackStatus[ifile]);
            }
        } catch (Exception ef) {
        }
        //droidDB.close();
    }

    private void updateListViewStatus(String updateText, int iFile) {

        if (EMD != null) {

            int firstPosition = EMD.downloadListView.getFirstVisiblePosition();
            Integer iFilePosition = EMD.layoutHash.get(trackIds[iFile]);

            if (iFilePosition != null) {

                final int wantedChild = iFilePosition - firstPosition;

                EMD.adapter.updateStatus(iFilePosition,updateText);

                if (wantedChild < 0 || wantedChild >= EMD.downloadListView.getChildCount()) {
                } else {
                    final String ut = updateText;
                    EMD.downloadListView.post(new Runnable() {
                        public void run() {
                            try {
                                TextView statusView = (TextView) EMD.downloadListView.getChildAt(wantedChild).findViewById(R.id.statustext);
                                statusView.setText(ut);
                            } catch (Exception ef) {
                                Log.d("EMD - ","layout hash failed?");
                            }
                        }
                    });
                }
            }
        }
    }

    private void testExpired(int ifile) {
        try {
            URL currentTrackURL = new URL(trackURLs[ifile]);
            HttpURLConnection c = (HttpURLConnection) currentTrackURL.openConnection();
            long ifs = c.getContentLength();
            if (ifs == -1) {
                c.disconnect();
                c =  (HttpURLConnection) currentTrackURL.openConnection();
                ifs = c.getContentLength();
            }
            String fileType = c.getContentType();
            c.disconnect();

            if (trackURLs[ifile].contains("mp3") && fileType.lastIndexOf("audio") == -1 && fileType.lastIndexOf("octet") == -1) {
                trackStatus[ifile] = 3;
                updateDB(ifile);
                updateListViewStatus("Exp.",ifile);
            }

        } catch (Exception ef) {
            trackStatus[ifile] = 3;
            updateDB(ifile);
            updateListViewStatus("Exp.",ifile);
        }
    }

    private Boolean trackCanceled(int iFile) {

       if (EMD == null) {
           return false;
       } else {
           if (EMD.layoutHash.get(trackIds[iFile]) == null || EMD.trackStatus[EMD.layoutHash.get(trackIds[iFile])] == 16) {
               return true;
           } else {
               return false;
           }
       }

    }

    private int getFileStatus() {

            List<Download> downloads = null;
            try {
                //downloadDB droidDB = new downloadDB(thisService);
                downloads = Utils.droidDB.getDownloads();
                //droidDB.close();
            } catch (Exception ef) {
                Log.d("EMD - ","Failed to get downloads"+ef);
            }

            nFiles = 0;
            try {
                nFiles = downloads.size();
                trackNames = new String[nFiles];
                trackArtists = new String[nFiles];
                trackAlbums = new String[nFiles];
                trackNums = new String[nFiles];
                trackURLs = new String[nFiles];
                trackImageURLs = new String[nFiles];
                trackBookFlags = new int[nFiles];
                trackStatus = new int[nFiles];
                trackIds = new Long[nFiles];
            } catch (Exception ef) {
                Log.d("EMD - ","Failed to get fileStatus in Service");
            }

            if (nFiles > 0) {
                int icount = 0;
                for (Download download : downloads) {
                    trackNames[icount]= download.track;
                    trackArtists[icount]= download.artist;
                    trackAlbums[icount]= download.album;
                    trackURLs[icount]= download.trackurl;
                    trackNums[icount]= download.number;
                    trackBookFlags[icount]= download.bookflag;
                    trackStatus[icount]= download.status;
                    trackIds[icount]= download.downloadId;
                    trackImageURLs[icount]= download.imageurl;
                    icount++;
                }
            }

            int nFilesNew = getFilesNew();
            return nFilesNew;

    }

    public void updateStatString(int ifile, int nFiles) {
        if (EMD != null) {
            EMD.getStatus();
            statString = EMD.statString;
        } else {
            statString = "Downloading "+ifile+" of "+nFiles;
        }
    }

    // Update statString to reflect the current download/completion status
    public int getFilesNew() {

        int nfilesNew = 0;

        int nTracksFailed = 0;
        int nTracksCompleted = 0;
        int nTracksWaiting = 0;
        int nTracksDownloading= 0;
        int nPreviousDownloads= 0;
        int nTracksExpired= 0;
        int nTracksIncomplete= 0;
        int nTracksIncompleteWaiting= 0;

        for (int ifile = 0; ifile < nFiles; ifile++) {
            if (trackStatus[ifile] == 10) {
                nTracksWaiting++;
            } else if (trackStatus[ifile] == 1) {
                nTracksCompleted++;
            } else if (trackStatus[ifile] == 2) {
                nPreviousDownloads++;
            } else if (trackStatus[ifile] == 3) {
                nTracksExpired++;
            } else if (trackStatus[ifile] == 16) {
                nTracksFailed++;
            } else if (trackStatus[ifile] > 10 && trackStatus[ifile]%2 == 1) {
                nTracksDownloading++;
            } else if (trackStatus[ifile] > 10 && trackStatus[ifile]%2 == 0) {
                nTracksIncomplete++;
            }
        }

        //nfilesNew = nFiles - nPreviousDownloads - nTracksExpired - nTracks;
        nfilesNew = nTracksIncomplete + nTracksWaiting;
        return nfilesNew;

    }

}
