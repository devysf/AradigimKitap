package com.example.yozacet.aradgimkitap;

/**
 * Created by yozacet on 29.01.2018.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MesajAdapter extends ArrayAdapter<MesajVeriTipi>
{
    public MesajAdapter(Context context, int resource, List<MesajVeriTipi> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.mesaj_layout, parent, false);
        }

        TextView kullaniciAdi = convertView.findViewById(R.id.mesajlasmaKullaniciAdiTextView);
        TextView mesajIcerik = convertView.findViewById(R.id.mesajlasmaMesajIcerigi);
        TextView aliciKadi = convertView.findViewById(R.id.aliciMesajlasmaKAdi);
        TextView aliciMesajIcerik = convertView.findViewById(R.id.aliciMesajlasmaMesajIcerigi);

        LinearLayout gonderenLineerLayout = convertView.findViewById(R.id.gonderenLineerLayout);
        LinearLayout aliciLineerLayout = convertView.findViewById(R.id.aliciLineerLayout);


        MesajVeriTipi message = getItem(position);

        if(MainActivity.mUsername.equals(message.getGonderen().toString()))
        {
            gonderenLineerLayout.setVisibility(View.VISIBLE);
            aliciLineerLayout.setVisibility(View.GONE);
            kullaniciAdi.setText("Ben");
            mesajIcerik.setText(message.getIcerik().toString());
        }
        else
        {
            aliciLineerLayout.setVisibility(View.VISIBLE);
            gonderenLineerLayout.setVisibility(View.GONE);

            aliciKadi.setText(message.getAlici().toString());
            aliciMesajIcerik.setText( message.getIcerik().toString());
        }


        return convertView;
    }
}