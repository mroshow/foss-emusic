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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringEscapeUtils.*;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleAlbum extends Activity implements AdapterView.OnItemClickListener {

    private Activity thisActivity;
    
    //UI-Views
    private ImageView reviewsButton;
    private ImageView sampleButton;
    private ImageView albumArt;
    private TextView nameTextView;
    private TextView artistTextView;
    private TextView genreTextView;
    private TextView labelTextView;
    private LinearLayout genreLayout;
    private LinearLayout labelLayout;
    private LinearLayout artistLayout;
    private ListView trackList;
    private RatingBar ratingBar;
    
    private int numberOfTracks;
    private int samplePlayPosition;
    private int statuscode = 200;
    private int version;
    private String albumId;
    private String genreId="";
    private String labelId="";
    private String artist;
    private String album;
    private String genre="";
    private String mp3Address;
    private String currentPlayingTrack;
    private String label;
    private String artistId;
    private String date;
    private String rating;
    private String urlAddress;
    private String emusicURL;
    private String imageURL;
    private String[] trackNames;
    private String[] sampleAddresses;
    private Boolean[] sampleExists;
    private Boolean vSamplesExist=false;
    private Boolean vArtExists=false;
    private Boolean vKilled=false;
    private Boolean vLoaded=false;
    private Boolean vPlayAllSamples=false;
    private Bitmap albumArtBitmap;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.singlealbum);

        version = android.os.Build.VERSION.SDK_INT;

        Intent myIntent = getIntent();
        albumId = myIntent.getStringExtra("keyalbumid");
        emusicURL = myIntent.getStringExtra("keyexturl");
        album = myIntent.getStringExtra("keyalbum");
        artist = myIntent.getStringExtra("keyartist");

        thisActivity = this;

        genreLayout=(LinearLayout)findViewById(R.id.llgenre);
        artistLayout=(LinearLayout)findViewById(R.id.llartist);
        labelLayout=(LinearLayout)findViewById(R.id.lllabel);
        nameTextView=(TextView)findViewById(R.id.tname);
        artistTextView=(TextView)findViewById(R.id.tartist);
        genreTextView=(TextView)findViewById(R.id.tgenre);
        labelTextView=(TextView)findViewById(R.id.tlabel);

        Resources res = getResources();

        if (version < 11) {
            genreLayout.setBackgroundDrawable(
             res.getDrawable(android.R.drawable.list_selector_background));
            labelLayout.setBackgroundDrawable(
             res.getDrawable(android.R.drawable.list_selector_background));
            artistLayout.setBackgroundDrawable(
             res.getDrawable(android.R.drawable.list_selector_background));
        } else {
            genreLayout.setBackgroundResource(R.drawable.list_selector_holo_dark);
            labelLayout.setBackgroundResource(R.drawable.list_selector_holo_dark);
            artistLayout.setBackgroundResource(R.drawable.list_selector_holo_dark);
        }
        genreLayout.setFocusable(true);
        labelLayout.setFocusable(true);
        artistLayout.setFocusable(true);

        albumArt=(ImageView)findViewById(R.id.albumart);
        reviewsButton=(ImageView)findViewById(R.id.reviewsbutton);
        sampleButton=(ImageView)findViewById(R.id.samplebutton);
        trackList=(ListView)findViewById(R.id.trklist);
        ratingBar=(RatingBar)findViewById(R.id.rbar);
        trackList.setOnItemClickListener(this);

        urlAddress="http://api.emusic.com/album/info?"+Secrets.apikey+"&albumId="+albumId+"&include=albumRating,label&imageSize=small";
        //Log.d("EMD - ",urlAddress);

        getInfoFromXML();
    }

    private void getInfoFromXML() {

        //Show a progress dialog while reading XML
        final ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.loading), true, true);
        setProgressBarIndeterminateVisibility(true);

        Thread t3 = new Thread() {
            public void run() {

            waiting(200);

                try {

                    //Log.d("EMD - ","About to parse");

                    URL url = new URL(urlAddress);
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    SAXParser sp = spf.newSAXParser();
                    XMLReader xr = sp.getXMLReader();
                    XMLHandlerSingleAlbum myXMLHandler = new XMLHandlerSingleAlbum();
                    xr.setContentHandler(myXMLHandler);
                    xr.parse(new InputSource(url.openStream()));

                    //Log.d("EMD - ","Done Parsing");

                    statuscode = myXMLHandler.statuscode;
                    if (statuscode != 200 && statuscode != 206) {
                        throw new Exception();
                    }

                    genre = myXMLHandler.genre;
                    genreId = myXMLHandler.genreId;
                    labelId = myXMLHandler.labelId;
                    label = myXMLHandler.label;
                    date = myXMLHandler.releaseDate;
                    rating = myXMLHandler.rating;
                    imageURL = myXMLHandler.imageURL;
                    artist = myXMLHandler.artist;
                    artistId = myXMLHandler.artistId;

                    //Log.d("EMD - ","Set genre etc..");

                    numberOfTracks = myXMLHandler.nItems;
                    trackNames = myXMLHandler.tracks;
                    sampleAddresses = myXMLHandler.sampleAddress;
                    sampleExists = myXMLHandler.sampleExists;
                    vSamplesExist = myXMLHandler.samplesExist;

                    handlerSetContent.sendEmptyMessage(0);
                    dialog.dismiss();
                    updateImage();

                } catch (Exception e) {
                    final Exception ef = e;
                    nameTextView.post(new Runnable() {
                        public void run() {
                            nameTextView.setText(R.string.couldnt_get_album_info);
                        }
                    });

                }

                //Remove Progress Dialog
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                handlerDoneLoading.sendEmptyMessage(0);
            }
        };
        t3.start();
    }

    private Handler handlerToast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Object texttotoast = msg.obj;
            Toast.makeText(thisActivity, ""+texttotoast,
             Toast.LENGTH_LONG).show();
        }
    };

    private Handler handlerSetContent = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            vLoaded = true;
            reviewsButton.setVisibility(0);
            String namestring=album;
            String artiststring=artist;
            String genrestring=genre;
            String labelstring=label;

            try {
                namestring= StringEscapeUtils.unescapeHtml(namestring);
                artiststring= StringEscapeUtils.unescapeHtml(artiststring);
                genrestring= StringEscapeUtils.unescapeHtml(genrestring);
                labelstring= StringEscapeUtils.unescapeHtml(labelstring);
            } catch (Exception em) {
            }

            nameTextView.setText(namestring);
            artistTextView.setText(artiststring);

            if ( genre != null && genre != "") {
                genreTextView.setText(genrestring);
                genreLayout.setVisibility(0);
            }
            if ( label != null && label != "") {
                labelTextView.setText(labelstring);
                labelLayout.setVisibility(0);
            }

            try {
                ratingBar.setRating(Float.parseFloat(rating));
            } catch (Exception ef2) {
            }

            if (vSamplesExist) {
                trackList.setAdapter(new ArrayAdapter<String>(thisActivity,
                 R.layout.tracklistplay_item, R.id.text,trackNames));
                sampleButton.setVisibility(0);
            } else {
                trackList.setAdapter(new ArrayAdapter<String>(thisActivity,
                 R.layout.item, R.id.label,trackNames));
            }
        }
    };

    private Handler handlerDoneLoading = new Handler() {
        @Override
        public void handleMessage(Message msg) {
             setProgressBarIndeterminateVisibility(false);
        }
    };

    private static void waiting (int n){
        long t0, t1;
        t0 =  System.currentTimeMillis();
        do{
            t1 = System.currentTimeMillis();
        }
        while (t1 - t0 < n);
    }

    public void sampleButtonPressed(View button) {
        //Toast.makeText(thisActivity, R.string.album_sample,
        // Toast.LENGTH_LONG).show();
        vPlayAllSamples = true;
        samplePlayPosition = 0;
        startSamplePlayback();
    }

    public void buyButtonPressed(View button) {
        Intent myIntent = new Intent(this,WebWindow.class);
        myIntent.putExtra("keyurl", emusicURL);
        Log.d("EMD - ","New WebWindow "+emusicURL);
        startActivity(myIntent);
    }

    public void artistButtonPressed(View button) {
        Intent myIntent = new Intent(this,SingleArtist.class);
        myIntent.putExtra("keyartistid", artistId);
        startActivity(myIntent);
    }

    public void reviewsButtonPressed(View button) {
        Intent myIntent = new Intent(this,ReviewList.class);
        String stringtitle = "User reviews of "+album;
        myIntent.putExtra("keytitle", stringtitle);
        String urlad = "http://api.emusic.com/album/reviews?"+Secrets.apikey+"&albumId="+albumId;
        myIntent.putExtra("keyurl", urlad);
        startActivity(myIntent);
    }

    public void genreButtonPressed(View button) {
        Intent myIntent = new Intent(this,SearchListWindow.class);
        String stringtype = "album";
        myIntent.putExtra("keytype", stringtype);
        String stringtitle = "Genre: "+genre;
        myIntent.putExtra("keytitle", stringtitle);
        String urlad = "http://api.emusic.com/album/charts?"+Secrets.apikey+"&genreId="+genreId;
        myIntent.putExtra("keyurl", urlad);
        String totalsearch = "genreId="+genreId;
        myIntent.putExtra("keyquery", totalsearch);
        startActivity(myIntent);
    }

    public void labelButtonPressed(View button) {
        Intent myIntent = new Intent(this,SearchListWindow.class);
        String stringtype = "album";
        myIntent.putExtra("keytype", stringtype);
        String stringtitle = "Label: "+label;
        myIntent.putExtra("keytitle", stringtitle);
        String urlad = "http://api.emusic.com/album/charts?"+Secrets.apikey+"&labelId="+labelId;
        myIntent.putExtra("keyurl", urlad);
        String totalsearch = "labelId="+labelId;
        myIntent.putExtra("keyquery", totalsearch);
        startActivity(myIntent);
    }

    private void updateImage() {
        albumArtBitmap = getImageBitmap(imageURL);
        if (vArtExists) {
	    albumArt.post(new Runnable() {
                public void run() {
                    albumArt.setImageBitmap(albumArtBitmap);
                }
            });
        } else {
            //Try a second time
            albumArtBitmap = getImageBitmap(imageURL);
            if (vArtExists) {
                albumArt.post(new Runnable() {
                    public void run() {
                        albumArt.setImageBitmap(albumArtBitmap);
                    }
                });
            } else {
                albumArt.post(new Runnable() {
                    public void run() {
                        albumArt.setImageResource(R.drawable.noalbum);;
                    }
                });
            }
        }
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) aURL.openConnection();
            long ifs = 0;
            ifs = conn.getContentLength();
            if (ifs == -1) {
                conn.disconnect();
                conn = (HttpURLConnection) aURL.openConnection();
                ifs = conn.getContentLength();
            }
            vArtExists = false;
            if (ifs > 0) {
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                vArtExists = true;
                //Log.d("EMD - ","art exists - Hurray!");
            } else {
                Log.e("EMD - ","art fail ifs 0 "+ifs+" "+url);
            }
        } catch (IOException e) {
            vArtExists = false;
            Log.e("EMD - ","art fail");
        }
        return bm;
    }

    //Attempt to get sample mp3 address from m3u file when item is clicked
    public void onItemClick(AdapterView<?> a, View v, int position,long id) {
        samplePlayPosition = position;
        startSamplePlayback();
    }

    private void startSamplePlayback() {

        int position = samplePlayPosition;

        if (sampleExists[position]) {

            final int fposition = position;
            final ProgressDialog dialog = ProgressDialog.show(this, "", ""+(samplePlayPosition+1)+". "+getString(R.string.getting_sample_loation), true, true);

            Thread t5 = new Thread() {
                public void run() {

                    String addresstemp="";
                    String debugText = "";

                    try {
                        URL u = new URL(sampleAddresses[fposition]);
                        HttpURLConnection c = (HttpURLConnection) u.openConnection();
    	                c.setRequestMethod("GET");
       	                c.setDoOutput(true);
                        c.connect();
                        InputStream in = c.getInputStream();

                        Boolean vSample=false;

                        InputStreamReader inputReader = new InputStreamReader(in);
                        BufferedReader buffReader = new BufferedReader(inputReader);

                        String line;

                        //if (true) {
                        //    Message handlermsg = new Message();
                        //    debugText = "Attempting to get mp3 address from m3u - "+sampleAddresses[fposition];
                        //    handlermsg.obj = debugText;
                        //    handlerToast.sendMessage(handlermsg);
                        //}

                        // read every line of the file into the line-variable, on line at the time
                        //while (( line = buffreader.readLine()).length() > 0) {
                        while (( line = buffReader.readLine()) != null) {

                            debugText = debugText + "\n"+line;

                            if (line.contains(".mp3") || line.contains("samples.emusic") || line.contains("samples.nl.emusic")) {
                            //if (false) {
                                vSample=true;
                                addresstemp=line;
                                mp3Address = addresstemp;
                                currentPlayingTrack = trackNames[fposition];
                                if (!vKilled && dialog.isShowing()) {
                                    handlerPlay.sendEmptyMessage(0);
                                }
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }

                        in.close();
                        c.disconnect();

                        if (dialog.isShowing()) {
                            if (true && !vSample) {
                                Message handlermsg = new Message();
                                String ttext = getString(R.string.could_not_get_an_address_for_sample);
                                handlermsg.obj = ttext;
                                handlerToast.sendMessage(handlermsg);

                                //For Debug Version - Send eMail with info
                                //Intent i = new Intent(android.content.Intent.ACTION_SEND);
                                //i.setType("text/plain");
                                //i.putExtra(Intent.EXTRA_SUBJECT, "eMusic Debugging Info");
                                //i.putExtra(Intent.EXTRA_TEXT, "Failed to find mp3 address\n"+debugText);
                                //startActivity(Intent.createChooser(i, "Please Send By eMail to jdeslip@gmail.com"));
                                vPlayAllSamples = false;
                            }
                            dialog.dismiss();

                        }

                    } catch (Exception ef) {
                        Log.e("EMD - ","Getting sample mp3 address failed ");
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            vPlayAllSamples = false;
                        }

                        //For Debug Version
                        //Intent i = new Intent(android.content.Intent.ACTION_SEND);
                        //i.setType("text/plain");
                        //i.putExtra(Intent.EXTRA_SUBJECT, "eMusic Debugging Info");
                        //i.putExtra(Intent.EXTRA_TEXT, "Finding sample caught exception\n"+debugText);
                        //startActivity(Intent.createChooser(i, "Please Send By eMail to jdeslip@gmail.com"));
                    }
                }
            };
            t5.start();

        } else {
            Toast.makeText(thisActivity, R.string.no_sample_available,
             Toast.LENGTH_SHORT).show();
            vPlayAllSamples = false;
        }

    }

    private Handler handlerNext = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (samplePlayPosition < (numberOfTracks-1) && vPlayAllSamples) {
                samplePlayPosition++;
                startSamplePlayback();
            }
            //Toast.makeText(thisActivity, "Next",
            // Toast.LENGTH_SHORT).show();
        }
    };


    //Creates a dialog and an embedded player for the sample
    private Handler handlerPlay = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (!vKilled) {
                Dialog dialog = new Dialog(thisActivity) {

                    Dialog thisDialog = this;
                    
                    //Dialog Views
                    ScrollView dialogScrollView = new ScrollView(thisActivity);
                    LinearLayout dialogLinLay = new LinearLayout(thisActivity);
                    TextView sampleInfoTextView = new TextView(thisActivity);
                    
                    //Dialog Variables
                    private MediaPlayer sampleMediaPlayer = new MediaPlayer();
                    boolean vDownloaded;
                    Thread t4;
                    String storagePath;

                    public boolean onKeyDown(int keyCode, KeyEvent event){
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            Thread t7 = new Thread() {
                                public void run() {
                                    try {
                                        t4.interrupt();
                                    } catch ( Exception ef) {
                                        Log.e("EMD - ","Cant stop thread");
                                    }
                                    try {
                                        sampleMediaPlayer.stop();
                                        sampleMediaPlayer.release();
                                    } catch ( Exception ef) {
                                        Log.e("EMD - ","Cant stop playback");
                                    }
                                }
                            };
                            t7.start();

                            vPlayAllSamples=false;
                            this.dismiss();
                            return true;
                        }
                        return false;
                    }

                    public void startTimer() {
                        Thread t4 = new Thread() {
                            public void run() {

                                try {
                                    sleep(30000);
                                } catch (Exception ef) {
                                }
                                try {
                                    sampleMediaPlayer.stop();
                                } catch ( Exception ef) {
                                }
                                try {
                                    if (thisDialog.isShowing()) {
                                        thisDialog.dismiss();
                                        handlerNext.sendEmptyMessage(0);
                                    }
                                } catch ( Exception ef) {
                                }

                            }
                        };
                        t4.start();
                    }

                    public void downloadSample() {

                        try {

                            vDownloaded = false;
                            waiting(100);

                            sampleInfoTextView.post(new Runnable() {
                                public void run() {
                                    sampleInfoTextView.setText(""+(samplePlayPosition+1)+". "+getString(R.string.buffering));
                                }
                            });

                            storagePath=Environment.getExternalStorageDirectory()+"/emxsamples";
                            final String filePath=storagePath;
                            final String fileName="lastsample.mp3";

                            File futureDirectory = new File(filePath);
                            futureDirectory.mkdir();

                            if (futureDirectory.exists()) {
                                //Log.d("EMD - ","Storage exists shouldn't toast");
                            } else {
                                throw new Exception();
                            }

                            File noMediaFile = new File(filePath+"/.nomedia");
                            noMediaFile.createNewFile();

                            try {
                              File oldSampleFile = new File(Environment.getExternalStorageDirectory()+"/eMusic/lastsample.mp3");
                              if (oldSampleFile.exists()) {
                                oldSampleFile.delete();
                              }
                            } catch (Exception ef) {
                            }

                            File futureFile = new File(filePath,fileName);

                            long itmp = 0;
                            itmp = futureFile.length();

                            URL u = new URL(mp3Address);
                            HttpURLConnection mp3HttpConnection = (HttpURLConnection) u.openConnection();
                            mp3HttpConnection.setRequestMethod("GET");
                            mp3HttpConnection.setDoOutput(true);
                            mp3HttpConnection.connect();
                            long itfs = 0;
                            itfs = mp3HttpConnection.getContentLength();
                            Log.d("EMD - ","CONTENT LENGTH "+itfs);

                            if (itfs == -1) {
                                mp3HttpConnection.disconnect();
                                mp3HttpConnection = (HttpURLConnection) u.openConnection();
                                mp3HttpConnection.setRequestMethod("GET");
                                mp3HttpConnection.setDoOutput(true);
                                mp3HttpConnection.connect();
                                itfs = mp3HttpConnection.getContentLength();
                            }

                            FileOutputStream bufferFile = new FileOutputStream(futureFile,false);
                            final long ifs = itfs;
                            final long jfs = ifs*100/1024/1024;

                            Log.d("EMD - ","ifs "+ifs);

                            InputStream in = mp3HttpConnection.getInputStream();

                            long i = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;
                            long jtprev=-1;

                            while ( (len1 = in.read(buffer)) > 0 ) {
                                if (vKilled || !thisDialog.isShowing()) {
                                    break;
                                }
                                bufferFile.write(buffer,0,len1);
                                i+=len1;
                                long jt = 100*i/1024/1024;
                                final double rfs = (double) jt/100.0;

                                if (jt != jtprev) {
                                    jtprev=jt;
                                    final int j = (int) jt;
                                    sampleInfoTextView.post(new Runnable() {
                                        public void run() {
                                            sampleInfoTextView.setText(""+(samplePlayPosition+1)+". "+getString(R.string.buffering)+" "+rfs+" "+getString(R.string.mb_3g_wifi_suggested_));
                                        }
                                    });
                                }
                            }
                            bufferFile.close();
                            vDownloaded=true;

                        } catch (Exception eff) {
                            Log.e("EMD - ",getString(R.string.cant_get_sample_)+eff);
                            sampleInfoTextView.post(new Runnable() {
                                public void run() {
                                    sampleInfoTextView.setText(R.string.no_storage_to_buffer);
                                }
                            });

                        }
                    }

                    public void show() {
                        dialogLinLay.setOrientation(1);
                        dialogLinLay.setPadding(16,0,16,16);
                        dialogLinLay.addView(sampleInfoTextView);
                        dialogScrollView.addView(dialogLinLay);

                        this.setContentView(dialogScrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        super.show();

                        MediaPlayer.OnPreparedListener listener = new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mpin) {
                                if (!vKilled && thisDialog.isShowing()) {
                                    Thread t8 = new Thread() {
                                        public void run() {
                                            try {
                                                sampleMediaPlayer.start();
                                                startTimer();
                                            } catch (Exception ef) {
                                            }
                                        }
                                    };
                                    t8.start();
	                            sampleInfoTextView.setText(getString(R.string.now_playing)+" "+(samplePlayPosition+1)+". "+currentPlayingTrack+" "+getString(R.string._3g_wifi_suggested));
                                }
                            }
                        };

                        MediaPlayer.OnCompletionListener complistener = new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mpin) {
                                Log.d("EMD - ","Completed");
                                try {
                                    sampleMediaPlayer.release();
                                } catch (Exception ef) {
                                }
                                if (thisDialog.isShowing()) {
                                    thisDialog.dismiss();
                                    handlerNext.sendEmptyMessage(0);
                                }
                            }
                        };

                        MediaPlayer.OnErrorListener errorlistener = new MediaPlayer.OnErrorListener() {
                            public boolean onError(MediaPlayer mpin,int what, int extra) {
                                Log.e("EMD - ","Error");
                                return true;
                            }
                        };

                        MediaPlayer.OnInfoListener infolistener = new MediaPlayer.OnInfoListener() {
                            public boolean onInfo(MediaPlayer mpin,int what, int extra) {
                                Log.d("EMD - ","Info");
                                return true;
                            }
                        };

                        MediaPlayer.OnBufferingUpdateListener bufferlistener = new MediaPlayer.OnBufferingUpdateListener() {
                            public void onBufferingUpdate(MediaPlayer mpin,int percent) {
                                Log.d("EMD - ","Buffer "+percent);
                            }
                        };

                        sampleMediaPlayer.setLooping(false);
                        sampleMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        sampleMediaPlayer.setOnPreparedListener(listener);
                        sampleMediaPlayer.setOnCompletionListener(complistener);
                        sampleMediaPlayer.setOnErrorListener(errorlistener);
                        sampleMediaPlayer.setOnInfoListener(infolistener);
                        sampleMediaPlayer.setOnBufferingUpdateListener(bufferlistener);

                        if (!vKilled && thisDialog.isShowing()) {
                            Thread t12 = new Thread() {
                                public void run() {
                                    try {
                                        downloadSample();
                                        mp3Address="file:/"+storagePath+"/lastsample.mp3";
                                        if (!vKilled && thisDialog.isShowing() && vDownloaded) {
                                            File file = new File(storagePath+"/lastsample.mp3");
                                            FileInputStream fis = new FileInputStream(file);
                                            sampleMediaPlayer.setDataSource(fis.getFD());
	                                    sampleMediaPlayer.prepareAsync();
                                        }
                                    } catch (Exception ef) {
                                        Log.e("EMD - ","MediaPlayer Failed");
                                    }
                                }
                            };
                            t12.start();
                        } else {
                            try {
                                sampleMediaPlayer.release();
                            } catch (Exception ef) {
                            }
                        }

                        Log.d("EMD - ","Starting Dialog");

                    }
                };

                dialog.setTitle(R.string.audio_sample);
                dialog.show();
            }
        }
    };

    // Destroy our taskbar notification icon when the program is closed
    @Override
    public void onDestroy() {
        super.onDestroy();
        vKilled=true;
        //Log.d("EMD Album - ", "Destroyed");
    }

    public void logoPressed(View buttoncover) {
        Log.d("EMD - ","logo pressed");
        String browseurl = "http://www.emusic.com?fref=400062";
        Intent browseIntent = new Intent(this,WebWindowBrowse.class);
        browseIntent.putExtra("keyurl", browseurl);
        startActivity(browseIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.singlealbum);

        nameTextView=(TextView)findViewById(R.id.tname);
        artistTextView=(TextView)findViewById(R.id.tartist);
        genreTextView=(TextView)findViewById(R.id.tgenre);
        labelTextView=(TextView)findViewById(R.id.tlabel);
        genreLayout=(LinearLayout)findViewById(R.id.llgenre);
        artistLayout=(LinearLayout)findViewById(R.id.llartist);
        labelLayout=(LinearLayout)findViewById(R.id.lllabel);

        Resources res = getResources();

        if (version < 11) {
            genreLayout.setBackgroundDrawable(
             res.getDrawable(android.R.drawable.list_selector_background));
            labelLayout.setBackgroundDrawable(
             res.getDrawable(android.R.drawable.list_selector_background));
            artistLayout.setBackgroundDrawable(
             res.getDrawable(android.R.drawable.list_selector_background));
        } else {
            genreLayout.setBackgroundResource(R.drawable.list_selector_holo_dark);
            labelLayout.setBackgroundResource(R.drawable.list_selector_holo_dark);
            artistLayout.setBackgroundResource(R.drawable.list_selector_holo_dark);
        }
        genreLayout.setFocusable(true);
        labelLayout.setFocusable(true);
        artistLayout.setFocusable(true);

        albumArt=(ImageView)findViewById(R.id.albumart);
        trackList=(ListView)findViewById(R.id.trklist);
        ratingBar=(RatingBar)findViewById(R.id.rbar);
        trackList.setOnItemClickListener(this);
        reviewsButton=(ImageView)findViewById(R.id.reviewsbutton);
        sampleButton=(ImageView)findViewById(R.id.samplebutton);

        if (vLoaded) {
            handlerSetContent.sendEmptyMessage(0);
            if (vArtExists) {
                albumArt.setImageBitmap(albumArtBitmap);
            } else {
                albumArt.setImageResource(R.drawable.noalbum);;
            }
        }
    }

}
