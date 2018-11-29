package com.example.yozacet.aradgimkitap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Mesajlarim extends AppCompatActivity {

    ListView mesajlarimListView;
    List<String> kisiListesi = new ArrayList<>();
    ArrayAdapter<String> mesajKisilerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesajlarim);

        mesajlarimListView = findViewById(R.id.mesajlarimListView);
        // Get a reference to our posts
        FirebaseDatabase mesajDB = FirebaseDatabase.getInstance();
        DatabaseReference ref = mesajDB.getReference().child("mesajlar");
        ChildEventListener childEventListener ;
        childEventListener =null;

        mesajDB  = FirebaseDatabase.getInstance();
        ref = mesajDB.getInstance().getReference().child("mesajlar");

        mesajKisilerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,kisiListesi);
        mesajlarimListView.setAdapter(mesajKisilerAdapter);

        mesajlarimListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Mesajlarim.this,MesajPenceresi.class);
                intent.putExtra("alici",kisiListesi.get(i).toString());
                startActivity(intent);
            }
        });

        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MesajVeriTipi mesaj = dataSnapshot.getValue(MesajVeriTipi.class);
                    if(MainActivity.mUsername.equals(mesaj.getGonderen()) )
                    {
                        int flag=0;
                        for(int i=0;i<kisiListesi.size() ;i++ )
                            if(kisiListesi.get(i).equals(mesaj.getAlici()) )
                                flag=1;

                        if(flag!=1)
                        {
                            kisiListesi.add(mesaj.getAlici());
                            mesajKisilerAdapter.notifyDataSetChanged();
                        }
                    }
                    if(MainActivity.mUsername.equals(mesaj.getAlici()))
                    {
                        int flag=0;
                        for(int i=0;i<kisiListesi.size() ;i++ )
                            if(kisiListesi.get(i).equals(mesaj.getGonderen()) )
                                flag=1;

                        if(flag!=1)
                        {
                            kisiListesi.add(mesaj.getGonderen());
                            mesajKisilerAdapter.notifyDataSetChanged();
                        }
                    }

                    mesajlarimListView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Select the last row so it will scroll into view...
                            mesajlarimListView.setSelection(mesajKisilerAdapter.getCount() - 1);
                        }
                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            };

            ref.addChildEventListener(childEventListener);
        }
    }
}
