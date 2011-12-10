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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {

    private HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();

    private File cacheDir;

    public ImageLoader(Context context) {
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        } else {
            cacheDir=context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public ImageLoader(Context context,HashMap<String, Bitmap> cachein) {
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

	cache=cachein;

        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        } else {
            cacheDir=context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    final int stub_id=R.drawable.stub;
    public void DisplayImage(String url, ImageView imageView) {
        if(cache.containsKey(url)) {
            imageView.setImageBitmap(cache.get(url));
	    //Log.d("EMD - ","In cache");
        } else {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
	    //Log.d("EMD - ","NOT in cache");
        }
    }

    public void DisplayImage(String url) {
        if(cache.containsKey(url)) {
        } else {
            queuePhoto(url, null);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them.
	if (imageView != null) {
	    photosQueue.Clean(imageView);
	}
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }

        //start thread if it's not started yet
        try {
            if (photoLoaderThread.getState()==Thread.State.NEW) {
                photoLoaderThread.start();
            }
        } catch (Exception ef) {
        }
    }

    private Bitmap getBitmap(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        try {
            String filename=String.valueOf(url.hashCode());
            File f=new File(cacheDir, filename);

            //from SD cache
            Bitmap b = decodeFile(f);
            if (b!=null) {
               return b;
            }

            //from web
            Bitmap bitmap=null;
            InputStream is=new URL(url).openStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            is.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true) {
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE) {
                    break;
                }
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {}
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u;
            imageView=i;
        }
    }

    PhotosQueue photosQueue=new PhotosQueue();

    public void stopThread() {
        photoLoaderThread.interrupt();
    }

    //stores list of photos to download
    class PhotosQueue {
        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();

        //removes all instances of this ImageView
        public void Clean(ImageView image) {
            for (int j=0 ;j<photosToLoad.size();) {
		ImageView tempimage = null;
		try {
			tempimage = photosToLoad.get(j).imageView;
		} catch (Exception ef) {
		}
                if(tempimage==image) {
			try {
        	            photosToLoad.remove(j);
			} catch (Exception ef) {
			}
                } else {
                    ++j;
                }
            }
        }
    }

    class PhotosLoader extends Thread {
        public void run() {
            try {
                while(true) {
                    //thread waits until there are any images to load in the queue
                    if (photosQueue.photosToLoad.size()==0) {
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    }
                    if (photosQueue.photosToLoad.size()!=0) {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad){
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }
                        Bitmap bmp=getBitmap(photoToLoad.url);
                        cache.put(photoToLoad.url, bmp);
			if (photoToLoad.imageView != null) {
                            if (((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)) {
                                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
                                Activity a=(Activity)photoToLoad.imageView.getContext();
                                a.runOnUiThread(bd);
                            }
			}
                    }
                    if(Thread.interrupted()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                //allow thread to exit
            }
        }
    }

    PhotosLoader photoLoaderThread=new PhotosLoader();

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
        public void run() {
            if(bitmap!=null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(stub_id);
            }
        }
    }

    public void clearCache() {
        //clear memory cache
        cache.clear();

        //clear SD cache
        File[] files=cacheDir.listFiles();
        for (File f:files) {
            f.delete();
        }
    }

}
