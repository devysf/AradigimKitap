package com.example.yozacet.aradgimkitap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MesajPenceresi extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;

    ListView mesajPenceresiListView ;
    EditText mesajYazEditText;
    Button mesajGonderButton;
    public static String alici;

    private FirebaseDatabase m2FirebaseDatabase;
    private DatabaseReference m2MessagesDatabaseReference;
    private ChildEventListener m2ChildEventListener;


    MesajAdapter mesajAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj_penceresi);

        m2FirebaseDatabase  = FirebaseDatabase.getInstance();
        m2MessagesDatabaseReference = m2FirebaseDatabase.getInstance().getReference().child("mesajlar");

        mesajPenceresiListView = findViewById(R.id.mesajPenceresiListView);


        mesajYazEditText = findViewById(R.id.mesajYazEditText);
        mesajGonderButton = findViewById(R.id.mesajGonderButton);

        mesajPenceresiListView = findViewById(R.id.mesajPenceresiListView);
        List<MesajVeriTipi> mesajlar = new ArrayList<>();
        mesajAdapter = new MesajAdapter(this,R.layout.mesaj_layout,mesajlar);
        mesajPenceresiListView.setAdapter(mesajAdapter);



        Intent aliciIntent= getIntent();
        Bundle b = aliciIntent.getExtras();
        alici  = b.get("alici").toString();

        // Enable Send button when there's text to send
        mesajYazEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mesajGonderButton.setEnabled(true);
                } else {
                    mesajGonderButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Send button sends a message and clears the EditText
        mesajGonderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                MesajVeriTipi mesaj = new MesajVeriTipi(MainActivity.mUsername,alici , mesajYazEditText.getText().toString());

                m2MessagesDatabaseReference.push().setValue(mesaj);
                // Clear input box
                mesajYazEditText.setText("");
            }
        });


        MainActivity.mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    //sing in

                    onSignedInInitialize(user.getDisplayName());

                }

                else
                {
                    //sign out

                    onSignedOutCleanup();

                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                  new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                        RC_SIGN_IN);
                }
            }
        };

    }

     @Override
    protected void onPause() {
        super.onPause();
        if(MainActivity.mAuthStateListener != null)
            MainActivity.mFirebaseAuth.removeAuthStateListener(MainActivity.mAuthStateListener);

        detachDatabaseReadListener();
        mesajAdapter.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.mFirebaseAuth.addAuthStateListener(MainActivity.mAuthStateListener);
    }

    private void onSignedInInitialize(String userName)
    {
        MainActivity.mUsername = userName;

        attachDatabaseReadListener();


    }

    private void onSignedOutCleanup()
    {
        MainActivity.mUsername = "anonymous";
        mesajAdapter.clear();

        detachDatabaseReadListener();
    }

    private void  attachDatabaseReadListener()
    {
        if(m2ChildEventListener == null) {
            m2ChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MesajVeriTipi mesaj = dataSnapshot.getValue(MesajVeriTipi.class);
                    if(MainActivity.mUsername.equals(mesaj.getGonderen()) && alici.equals(mesaj.getAlici()))
                        mesajAdapter.add(mesaj);
                    if(MainActivity.mUsername.equals(mesaj.getAlici()) && alici.equals(mesaj.getGonderen()))
                        mesajAdapter.add(mesaj);

                    mesajPenceresiListView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Select the last row so it will scroll into view...
                            mesajPenceresiListView.setSelection(mesajAdapter.getCount() - 1);
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

            m2MessagesDatabaseReference.addChildEventListener(m2ChildEventListener);
        }
    }

    private void detachDatabaseReadListener()
    {
        if(m2ChildEventListener != null) {
            m2MessagesDatabaseReference.removeEventListener(m2ChildEventListener);
            m2ChildEventListener = null;
        }
    }

}
