package com.example.yozacet.aradgimkitap;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaylasmaEkrani extends AppCompatActivity {

    public EditText kitapIsmiTextView ;
    public EditText kitapYazarIsmiTextView;
    public ImageButton imageEkleButton;
    public ImageView imageEkleView;
    public Button kaydetButton;


    public static final int RC_SIGN_IN = 1;
    public static final int RC_PHOTO_PICKER =  2;
    public static final int RC_SAVE_BUTTON = 3;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;


    public Uri selectedImageUri;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylasma_ekrani);

        selectedImageUri = null;

        mFirebaseDatabase  = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getInstance().getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getInstance().getReference().child("chat_photos");

        kitapIsmiTextView = findViewById(R.id.kitapIsmiTextView);
        kitapYazarIsmiTextView = findViewById(R.id.kitapYazarTextView);

        imageEkleView = findViewById(R.id.imageEkleView);
        imageEkleButton = findViewById(R.id.imageEkleButton);


        imageEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

            }
        });

        kaydetButton = findViewById(R.id.kaydetButton);


    }

    public void onClick(View view)
    {
            //Upload a file to Firebase Storage
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    KitapVeriTipi kitap = new KitapVeriTipi(kitapIsmiTextView.getText().toString(),
                            kitapYazarIsmiTextView.getText().toString(),MainActivity.mUsername ,downloadUrl.toString());
                    mMessagesDatabaseReference.push().setValue(kitap);
                    Log.i("info","foto");
                }
            });

       final ProgressBar progressBar;
        progressBar = findViewById(R.id.progresBar);

        progressBar.setVisibility(View.VISIBLE);

        new CountDownTimer(3000, 1000)
        {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Log.i("info","Done");

                kitapYazarIsmiTextView.setText("");
                kitapIsmiTextView.setText("");
                imageEkleView.setImageURI(null);

                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(PaylasmaEkrani.this,Profil.class);
                startActivity(intent);
            }

        }.start();


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            selectedImageUri = data.getData();

            imageEkleView.setImageURI(selectedImageUri);

        }


    }
}
