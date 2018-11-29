package com.example.yozacet.aradgimkitap;

/**
 * Created by yozacet on 27.01.2018.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PaylasmaAdapter extends ArrayAdapter<KitapVeriTipi> {

    public PaylasmaAdapter(Context context, int resource, List<KitapVeriTipi> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.paylasim, parent, false);
        }

        ImageView kitapResim =  convertView.findViewById(R.id.kitapResmiPaylasım);
        TextView kitapIsim =  convertView.findViewById(R.id.kitapIsmiPaylasım);
        TextView kitapYazar =  convertView.findViewById(R.id.yazarIsmiPaylasım);
        TextView kAdi = convertView.findViewById(R.id.kAdiPaylasim);


        KitapVeriTipi message = getItem(position);

        if(message.getPhotoUrl() !=null)
        {
            Glide.with(kitapResim.getContext())
                    .load(message.getPhotoUrl())
                    .into(kitapResim);
        }

        kitapIsim.setText(message.getKitapIsmi().toString());
        kitapYazar.setText(message.getYazarIsmi().toString());
        kAdi.setText(message.getkAdi());

        return convertView;
    }
}