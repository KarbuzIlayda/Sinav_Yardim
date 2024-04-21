package com.ilayda.sinavyardim;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.view.WindowCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProfilOlustur extends AppCompatActivity {

    EditText isimEt, kuladEt, mailEt, bioEt;
    Button btnprokay;
    ProgressBar probar;
    Uri imageUri;
    UploadTask yukleme;
    ImageView profilfotoIV;
    private static final int FOTO_SEC = 1;
    StorageReference deporeferans;
    FirebaseDatabase veritaban = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans;
    FirebaseFirestore vt = FirebaseFirestore.getInstance();
    DocumentReference dokumanreferans;
    KullaniciUyeler uye;
    String kullaniciID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_olustur);

        uye = new KullaniciUyeler();
        isimEt = findViewById(R.id.edittextisim);
        kuladEt = findViewById(R.id.edittextkullanad);
        mailEt = findViewById(R.id.edittextmail);
        bioEt = findViewById(R.id.edittextbio);
        btnprokay = findViewById(R.id.btnprofilkaydet);
        probar = findViewById(R.id.progbarprofilkayit);
        profilfotoIV = findViewById(R.id.imviewprofilfoto);

        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();
        if(kullanici != null)
            kullaniciID = kullanici.getUid();
        dokumanreferans = vt.collection("Kullanicilar/").document(kullaniciID);
        deporeferans = FirebaseStorage.getInstance().getReference("Profil Fotograflari");
        vtreferans = veritaban.getReference("Tum Kullanicilar");

        btnprokay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                veriyukle();
            }
        });

        profilfotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foto = new Intent();
                foto.setType("image/*");
                foto.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(foto, FOTO_SEC);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        dokumanreferans.collection("Kullanicilar/").document(kullaniciID);
        dokumanreferans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String cekilenisim = task.getResult().getString("isim");
                    String cekilenkullaniciad= task.getResult().getString("kullaniciAdi");
                    String cekilenmail = task.getResult().getString("mail");
                    String cekilenbio = task.getResult().getString("bio");
                    String cekilenppurl = task.getResult().getString("urlFoto");

                    if(cekilenppurl != null && !cekilenppurl.trim().isEmpty() && !TextUtils.equals(cekilenppurl.trim(), " "))
                        Picasso.get().load(cekilenppurl).into(profilfotoIV);
                    isimEt.setText(cekilenisim);
                    kuladEt.setText(cekilenkullaniciad);
                    mailEt.setText(cekilenmail);
                    bioEt.setText(cekilenbio);
                } else {
                    Toast.makeText(ProfilOlustur.this, "Profil bulunamadı!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == FOTO_SEC || resultCode == RESULT_OK || data != null || data.getData() != null ){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(profilfotoIV);
            }
        } catch (Exception e){
            Toast.makeText(ProfilOlustur.this, "HATA!",Toast.LENGTH_SHORT).show();
        }
    }
    private String dosyaUzantisiAl(Uri uri) {
        ContentResolver icerik = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((icerik.getType(uri)));
    }
    private void veriyukle() {
        String isim = isimEt.getText().toString();
        String bio = bioEt.getText().toString();
        String mail = mailEt.getText().toString();
        String kullaniciadi = kuladEt.getText().toString();
        final StorageReference referans;

        if(!TextUtils.isEmpty(isim)  || !TextUtils.isEmpty(mail) || !TextUtils.isEmpty(kullaniciadi)
            || !TextUtils.isEmpty(bio)) {
            probar.setVisibility(View.VISIBLE);

            if (imageUri != null) {
                referans = deporeferans.child(System.currentTimeMillis() + "." + dosyaUzantisiAl(imageUri));
                yukleme = referans.putFile(imageUri);
                    Task<Uri> urlTask = yukleme.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return referans.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                uye.setUrl(downloadUri.toString());
                                uye.setIsim(isim);
                                uye.setKuladi(kullaniciadi);
                                uye.setBio(bio);
                                uye.setUid(kullaniciID);

                                vtreferans.child(kullaniciID).setValue(uye);
                                vt.runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                        transaction.update(dokumanreferans, "isim", isim);
                                        transaction.update(dokumanreferans, "kullaniciAdi", kullaniciadi);
                                        transaction.update(dokumanreferans, "mail", mail);
                                        transaction.update(dokumanreferans, "bio", bio);
                                        transaction.update(dokumanreferans, "urlFoto", downloadUri.toString());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        probar.setVisibility(View.VISIBLE);
                                        Toast.makeText(ProfilOlustur.this,"Profil başarıyla değiştirildi.",Toast.LENGTH_LONG).show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //şuraya bir daha bir bak daha iyi bir çözüm illa ki bulursun!!!
                                                startActivity(new Intent(ProfilOlustur.this, MainActivity.class));
                                            }
                                        },2000);
                                    }
                                });
                            }
                        }
                    });
            } else {
                uye.setIsim(isim);
                uye.setKuladi(kullaniciadi);
                uye.setBio(bio);
                uye.setUid(kullaniciID);

                vtreferans.child(kullaniciID).setValue(uye);
                vt.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        transaction.update(dokumanreferans, "isim", isim);
                        transaction.update(dokumanreferans, "kullaniciAdi", kullaniciadi);
                        transaction.update(dokumanreferans, "mail", mail);
                        transaction.update(dokumanreferans, "bio", bio);

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        probar.setVisibility(View.VISIBLE);
                        Toast.makeText(ProfilOlustur.this,"Profil başarıyla değiştirildi.",Toast.LENGTH_LONG).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //şuraya bir daha bir bak daha iyi bir çözüm illa ki bulursun!!!
                                startActivity(new Intent(ProfilOlustur.this, MainActivity.class));
                            }
                        },2000);
                    }
                });
            }
        } else {
            Toast.makeText(ProfilOlustur.this, "Değişiklik yapmak üzere alan seçilmedi!",Toast.LENGTH_LONG).show();
        }
    }
}