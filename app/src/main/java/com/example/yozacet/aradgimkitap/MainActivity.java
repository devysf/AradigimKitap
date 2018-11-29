package com.example.yozacet.aradgimkitap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.yozacet.aradgimkitap.PaylasmaEkrani.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static String mUsername;

    public static  FirebaseDatabase mFirebaseDatabase;
    public static  DatabaseReference mMessagesDatabaseReference;

    public static  ChildEventListener mChildEventListener;

    public List<KitapVeriTipi> paylasimlar = new ArrayList<>();
    public ListView paylasimListView;
    public PaylasmaAdapter paylasimAdapter;

    public static FirebaseAuth mFirebaseAuth;
    public  static FirebaseAuth.AuthStateListener mAuthStateListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        paylasimListView = findViewById(R.id.listViewPaylasım);

        paylasimAdapter = new PaylasmaAdapter(MainActivity.this, R.layout.paylasim, paylasimlar);
        paylasimListView.setAdapter(paylasimAdapter);

        paylasimListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                final int index = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(paylasimlar.get(index).getkAdi() + " adli kullaniciya");
                builder.setMessage("Mesaj Atmak İster Misiniz?!");
                builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this,MesajPenceresi.class);
                        intent.putExtra("alici",paylasimlar.get(index).getkAdi());
                        startActivity(intent);
                    }
                });
                builder.show();

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this,PaylasmaEkrani.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == RC_SIGN_IN )
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(this,"Signed In", Toast.LENGTH_LONG ).show();
                 TextView kAdiText;
                 TextView emailText;
                kAdiText = findViewById(R.id.kullanıcıAdiTextView);
                emailText = findViewById(R.id.emailTextView);
                kAdiText.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
                emailText.setText(mFirebaseAuth.getCurrentUser().getEmail());

            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this,"Signed In canceled", Toast.LENGTH_LONG ).show();
                finish();
            }
        }

    }
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        startActivity(new Intent(this,MainActivity.class));
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("infoOncreate","asdasdasd");



        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profil)
        {
            Intent intent = new Intent(MainActivity.this,Profil.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_kesfet)
        {
            Intent intent = new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.mesajlarim)
        {
            Intent intent = new Intent(MainActivity.this,Mesajlarim.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_ayarlar) {

        }

        else if (id == R.id.nav_kapat) {
            AuthUI.getInstance().signOut(this);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null)
           mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

        detachDatabaseReadListener();
        paylasimAdapter.clear();

    }


    @Override
    protected void onResume() {
        super.onResume();


        attachDatabaseReadListener();
         mFirebaseAuth.addAuthStateListener(mAuthStateListener);


    }


    private void onSignedInInitialize(String userName)
    {
        mUsername = userName;

        attachDatabaseReadListener();


    }

    private void onSignedOutCleanup()
    {
        mUsername = ANONYMOUS;
        paylasimAdapter.clear();

        detachDatabaseReadListener();
    }



    private void  attachDatabaseReadListener()
    {

        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    KitapVeriTipi kitap = dataSnapshot.getValue(KitapVeriTipi.class);
                    if(!kitap.getkAdi().equals(mUsername))
                        paylasimAdapter.add(kitap);

                    paylasimListView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Select the last row so it will scroll into view...
                            paylasimListView.setSelection(paylasimAdapter.getCount() - 1);
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

            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener()
    {
        if(mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
