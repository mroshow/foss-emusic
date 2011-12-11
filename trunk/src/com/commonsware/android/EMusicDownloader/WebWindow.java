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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.List;
import java.io.File;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Display;

public class WebWindow extends Activity {

    private Activity thisActivity;

    private WebView webby;
    
    private String myURL;
    private Boolean vLoaded = false;
    //private static final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(
    // ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.BOTTOM);
    private emuDB droidDB;
    private downloadDB droidHisDB;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS,Window.PROGRESS_VISIBILITY_ON);

        setContentView(R.layout.webwindow);
        setProgressBarIndeterminateVisibility(true);

        Intent myIntent = getIntent();
        myURL = myIntent.getStringExtra("keyurl");

        thisActivity = this;

        webby=(WebView)findViewById(R.id.webby);

        WebSettings websettings = webby.getSettings();
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setUserAgentString("droidian");
 
        websettings.setLoadWithOverviewMode(true);
        websettings.setUseWideViewPort(true);

        //Display display = getWindowManager().getDefaultDisplay(); 
        //int width = display.getWidth();

        //if (width < 800) {
        //    webby.setInitialScale(45);
        //}

        webby.setWebChromeClient(new WebChromeClient() {
   	        public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
     	        // The progress meter will automatically disappear when we reach 100%
     	        thisActivity.setProgress(progress * 100);
   	        }
        });

        websettings.setJavaScriptEnabled(true);
        //websettings.setJavaScriptCanOpenWindowsAutomatically(false);

        webby.setWebViewClient(new InsideWebViewClient());
        
        //The below used to be necessary to get zoom controls
        //webby.invokeZoomPicker();

        try {
            Method m = webby.getClass().getMethod("setIsCacheDrawBitmap", boolean.class);
            if (m != null) {
                m.invoke(webby, false);
                webby.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        //For Debug Version - Send eMail with info
        //Toast.makeText(thisActivity, "Loading URL "+myURL,
        // Toast.LENGTH_LONG).show();
        //Intent i = new Intent(android.content.Intent.ACTION_SEND);
        //i.setType("text/plain");
        //i.putExtra(Intent.EXTRA_SUBJECT, "eMusic Debugging Info");
        //i.putExtra(Intent.EXTRA_TEXT, "Original URL\n"+myURL);
        //startActivity(Intent.createChooser(i, "Please Send By eMail to jdeslip@gmail.com"));

        webby.loadUrl(myURL);

    }

    private class InsideWebViewClient extends WebViewClient {

        //@Override
        //public boolean shouldInterceptRequest(WebView view, String url) {

        //    Log.d("EMD RES URL -",url);

        //    return false;
        //}

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d("EMD URL -",url);

            //For Debug Version - Send eMail with info
            //Toast.makeText(thisActivity, "Loading URL "+url,
            // Toast.LENGTH_LONG).show();
            //Intent i = new Intent(android.content.Intent.ACTION_SEND);
            //i.setType("text/plain");
            //i.putExtra(Intent.EXTRA_SUBJECT, "eMusic Debugging Info");
            //i.putExtra(Intent.EXTRA_TEXT, "Loading URL\n"+myURL);
            //startActivity(Intent.createChooser(i, "Please Send By eMail to jdeslip@gmail.com"));

            if (url.contains(".emx")) {

                //WebSettings websettings = webby.getSettings();
                //websettings.setJavaScriptEnabled(false);

                long currenttime = System.currentTimeMillis();
                Log.d("EMD - ","Current time "+currenttime);

                droidDB = new emuDB(thisActivity);
                long cookietime = droidDB.getCookietime();

                Log.d("EMD - ","Cookie time "+cookietime);

                try {
                    droidDB.updateCookietime(currenttime);
                } catch (Exception eff) {
                    Log.e("EMD - ","Failed to update cookietime");
                }
                droidDB.close();

                // Check if our cookie is too old - if so, need to relogin
                if ((currenttime-cookietime) > 7200000) {
                //if ((currenttime-cookietime) > 1) {
                    Log.d("EMD","Cookie time to large");
                    setProgressBarIndeterminateVisibility(true);
 	            view.loadUrl("https://www.emusic.com/security/signon.html");
                } else {
                    startDownload(url);
                }
                return true;

            } else if (url.contains("audiobooks/book/-")) {
                Log.d("EMD","We have a dubious book URL: "+url);
                url = url.replace("audiobooks/book","book/-");
                Log.d("EMD","Replacing with: "+url);
                view.loadUrl(url);
                return true;
            } else if ((url.contains(".mp3")) || url.contains("?rn=")) {
                //WebSettings websettings = webby.getSettings();
                //websettings.setJavaScriptEnabled(false);
                setProgressBarIndeterminateVisibility(true);
                Log.d("EMD","URL contains mp3");
                view.loadUrl("https://www.emusic.com/security/signon.html");
                return true;
            } else if (url.contains("security/success") || url.equals("https://www.emusic.com/")) {
                long currenttime = System.currentTimeMillis();

                droidDB = new emuDB(thisActivity);
                try {
                    droidDB.updateCookietime(currenttime);
                } catch (Exception eff) {
                    Log.e("EMD - ","Failed to update cookietime");
                }
                droidDB.close();

                //WebSettings websettings = webby.getSettings();
                //websettings.setJavaScriptEnabled(true);
                //setProgressBarIndeterminateVisibility(true);
                //view.loadUrl(myURL);
                return false;
            } else if ((url.contains("registration"))) {
                //WebSettings websettings = webby.getSettings();
                //websettings.setJavaScriptEnabled(true);
                setProgressBarIndeterminateVisibility(true);
                return false;
            } else {
                //WebSettings websettings = webby.getSettings();
                //websettings.setJavaScriptEnabled(true);
                setProgressBarIndeterminateVisibility(true);
                return false;
            }
        }

        public void onPageFinished(WebView view, String url) {
            Log.d("EMD","Page Finished: "+url);
            setProgressBarIndeterminateVisibility(false);
            //WebSettings websettings = webby.getSettings();
            //websettings.setJavaScriptEnabled(false);
        }

        public void startDownload(String url) {
                Log.d("EMD","We have a download! "+url);

                CookieManager cookieManager = CookieManager.getInstance(); 
                String sessionCookie =  cookieManager.getCookie("http://www.emusic.com");
                //Toast.makeText(thisActivity, "EMX ALERT "+sessionCookie,
                // Toast.LENGTH_LONG).show();
                url = url.replaceAll("&h=.*","");
                long foundId = -1;

                try {
                    droidHisDB = new downloadDB(thisActivity);
                     List<History> historys = droidHisDB.getHistory();

                    int icount = 0;
                    for (History history : historys) {
                        icount++;
                        if (url.contains(history.url)) {
                             File cachedfile = new File(history.urllocal.replace("file://",""));
                             if (cachedfile.exists()) {
                                 foundId = history.historyId;
                             }
                        }
                    }
                    droidHisDB.close();
                } catch (Exception e2) {
                }

                if (foundId == -1) {
                    Intent myIntent = new Intent(thisActivity,DownloadManager.class);
                    myIntent.setData(Uri.parse(url));
                    myIntent.putExtra("keycookie",sessionCookie);
      	            startActivity(myIntent);
                } else {
                    final long foundIdFin = foundId;
                    final String urlFin = url;
                    final String sessionCookieFin = sessionCookie;

                    new AlertDialog.Builder(thisActivity)
                     .setTitle("Repurchase?")
                     .setMessage("This appears to be a previous download. Would you like to use the cached-copy instead?\n\nIf you continue, you may be recharged. If you didn\'t complete all your tracks, you must contact eMusic customer service at http://www.emusic.com/contact/index.html to ask for your credits to be restored.")
                     .setPositiveButton("Use Cache", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent myIntent = new Intent(thisActivity,DownloadManager.class);
                            myIntent.setData(Uri.parse(urlFin));
                            myIntent.putExtra("keycookie",sessionCookieFin);
      	                    startActivity(myIntent);
                        }
                    }).setNegativeButton("Use New", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            droidHisDB = new downloadDB(thisActivity);
                            droidHisDB.deleteHistory(foundIdFin);
                            droidHisDB.close();
                            Intent myIntent = new Intent(thisActivity,DownloadManager.class);
                            myIntent.setData(Uri.parse(urlFin));
                            myIntent.putExtra("keycookie",sessionCookieFin);
      	                    startActivity(myIntent);
                        }
                    }).show();
                }
        }

        public void onLoadResource(WebView view, String url) {
            //Log.d("EMD","Loading resource: "+url);
            if (url.contains(".emx") && url.contains("/download")) {
                startDownload(url);
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("EMD","Page Started: "+url);
            setProgressBarIndeterminateVisibility(true);
            if (url.contains("audiobooks/book/-")) {
                Log.d("EMD","We have a dubious book URL");
                url = url.replace("audiobooks/book","book/-");
                Log.d("EMD","Replacing with: "+url);
                view.loadUrl(url);
            }
            //WebSettings websettings = webby.getSettings();
            //websettings.setJavaScriptEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("EMD - ","WW Resume "+vLoaded);
        if (vLoaded) {
            webby.reload();
            Toast.makeText(thisActivity, R.string.please_wait_for_page_to_refresh,
             Toast.LENGTH_SHORT).show();
        }
        vLoaded=true;
    }

}
