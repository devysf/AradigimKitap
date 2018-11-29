package com.example.yozacet.aradgimkitap;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.yozacet.aradgimkitap.PaylasmaEkrani.RC_SIGN_IN;

public class Profil extends AppCompatActivity {

    public PaylasmaAdapter profilAdapter ;
    public List<KitapVeriTipi> profilPaylasim = new ArrayList<>();
    public ListView profilListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        profilListView = findViewById(R.id.profilListView);

        profilAdapter= new PaylasmaAdapter(this,R.layout.paylasim,profilPaylasim);
        profilListView.setAdapter(profilAdapter);


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
        profilAdapter.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();

        attachDatabaseReadListener();
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
        profilAdapter.clear();

        detachDatabaseReadListener();
    }



    private void  attachDatabaseReadListener()
    {
        if(MainActivity.mChildEventListener == null) {
            MainActivity.mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    KitapVeriTipi kitap = dataSnapshot.getValue(KitapVeriTipi.class);
                    if(kitap.getkAdi().equals(MainActivity.mUsername))
                        profilAdapter.add(kitap);

                    profilListView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Select the last row so it will scroll into view...
                            profilListView.setSelection(profilAdapter.getCount() - 1);
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

            MainActivity.mMessagesDatabaseReference.addChildEventListener(MainActivity.mChildEventListener);
        }
    }

    private void detachDatabaseReadListener()
    {
        if(MainActivity.mChildEventListener != null) {
            MainActivity.mMessagesDatabaseReference.removeEventListener(MainActivity.mChildEventListener);
            MainActivity.mChildEventListener = null;
        }
    }
}
