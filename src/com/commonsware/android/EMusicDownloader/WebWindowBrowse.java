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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;



public class WebWindowBrowse extends WebWindow {

     public Boolean vBrowse = true;

     public static final int CLOSE_ID = Menu.FIRST+1;

     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
         if ((keyCode == KeyEvent.KEYCODE_BACK) && webby.canGoBack()) {
             webby.goBack();
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
 
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         populateMenu(menu);
         return(super.onCreateOptionsMenu(menu));
     }
 
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         return(applyMenuChoice(item) ||
         super.onOptionsItemSelected(item));
     }
 
     private void populateMenu(Menu menu) {
         menu.add(Menu.NONE, CLOSE_ID, Menu.NONE, "Close");
     }
 
     private boolean applyMenuChoice(MenuItem item) {
         switch (item.getItemId()) {
         case CLOSE_ID:
             finish();
             return true;
         }
         return(false);
     }


}
