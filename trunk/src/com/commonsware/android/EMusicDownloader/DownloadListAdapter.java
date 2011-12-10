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

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadListAdapter extends BaseAdapter {

    private Activity activity;
    private String[] data;
    private String[] listText;
    private String[] status;
    private HashMap<String, Bitmap> cache;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public DownloadListAdapter(Activity a) {
        activity = a;
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public DownloadListAdapter(Activity a, String[] d, String[] t, String[] st, HashMap<String, Bitmap> cachein) {
        activity = a;
        data=d;
        listText=t;
        status=st;
        cache=cachein;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext(),cache);

        for (int i = 0; i < data.length; i++) {
            //ViewHolder holder;
            //holder.text.setText(listtext[i]);
            //holder.image.setTag(data[i]);

            imageLoader.DisplayImage(data[i]);
        }
    }

    public DownloadListAdapter(Activity a, String[] d, String[] t) {
        activity = a;
        data=d;
        listText=t;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext(),cache);
    }

    public void nullActivity() {
        activity = null;
        inflater = null;
        imageLoader = null;
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public void updateStatus(int pos, String st) {
        status[pos]=st;
    }

    public static class ViewHolder{
        public TextView infotext;
        public TextView statustext;
        public ImageView image;
        public ImageView statusimage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if (convertView==null) {
            vi = inflater.inflate(R.layout.downloadlist_item, null);
            holder=new ViewHolder();
            holder.infotext=(TextView)vi.findViewById(R.id.infotext);;
            holder.statustext=(TextView)vi.findViewById(R.id.statustext);;
            holder.image=(ImageView)vi.findViewById(R.id.image);
            holder.statusimage=(ImageView)vi.findViewById(R.id.statusimage);
            vi.setTag(holder);
        } else {
            holder=(ViewHolder)vi.getTag();
        }

        holder.infotext.setText(listText[position]);
        holder.statustext.setText(status[position]);
        //if (status[position].equals("Complete")) {
        //    holder.statusimage.setImageResource(R.drawable.icon2);
        //    holder.statusimage.setVisibility(0);
        //    holder.statustext.setVisibility(8);
        //} else if (status[position].equals("Queued")) {
        //    holder.statusimage.setImageResource(R.drawable.icon0);
        //    holder.statusimage.setVisibility(0);
        //    holder.statustext.setVisibility(8);
        //}
        holder.image.setTag(data[position]);
        imageLoader.DisplayImage(data[position], holder.image);
        return vi;
    }

}
