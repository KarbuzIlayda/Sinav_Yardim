package com.ilayda.sinavyardim;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PaylasFragment extends Fragment {
    ImageView postfoto;
    EditText icerikEt;
    Button postgonder;
    FirebaseFirestore ffvt = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    FirebaseUser mevcutkullanici;
    private Uri url;
    UploadTask yukleme;
    StorageReference deporeferans;
    FirebaseDatabase rvt = FirebaseDatabase.getInstance();
    DatabaseReference vtreferanspost, vtreferansfoto, vtkullanicipost, vtkullantumpostlar;
    DocumentReference dokumanreferans;
    String sinav, isim, ppurl;
    Gonderi gonderi;
    private static final int FOTO_SEC = 1;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mevcutkullanici = mAuth.getInstance().getCurrentUser();
        dokumanreferans = ffvt.collection("Kullanicilar").document(mevcutkullanici.getUid());
        dokumanreferans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    sinav = task.getResult().getString("sinavSecimi");
                    isim = task.getResult().getString("isim");
                    ppurl = task.getResult().getString("urlFoto");
                    vtreferanspost = rvt.getReference(sinav+" postlar");
                    vtreferansfoto = rvt.getReference(sinav+" fotograflar");
                    vtkullantumpostlar = rvt.getReference("Tum Postlar");
                    vtkullanicipost = rvt.getReference("Kullanici Postlar").child(mevcutkullanici.getUid());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_paylas, container, false);

        icerikEt = view.findViewById(R.id.tv_icerik);
        postfoto = view.findViewById(R.id.iv_postfoto);
        postgonder = view.findViewById(R.id.btn_gonder);
        gonderi = new Gonderi();
        deporeferans = FirebaseStorage.getInstance().getReference("Gonderi Fotolari");

        postfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fotosec = new Intent(Intent.ACTION_CHOOSER);
                fotosec.setType("image/*");
                fotosec.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(fotosec, FOTO_SEC);
            }
        });

        postgonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aciklama = icerikEt.getText().toString();
                String kullaniciID = mevcutkullanici.getUid();

                Calendar cDate = Calendar.getInstance();
                SimpleDateFormat mevcuttarih = new SimpleDateFormat("dd-MMMM-yyyy");
                final String kayittarihi = mevcuttarih.format(cDate.getTime());

                Calendar cTime = Calendar.getInstance();
                SimpleDateFormat mevcutzaman = new SimpleDateFormat("HH:mm:ss");
                final String kayitlizaman = mevcutzaman.format(cTime.getTime());
                String zaman = kayittarihi + ":" + kayitlizaman;
                final StorageReference deporef;

                if(url != null) {
                    deporef = deporeferans.child(System.currentTimeMillis() + "." + dosyaUzantisiAl(url));
                    yukleme = deporef.putFile(url);
                    Task<Uri> urlTask = yukleme.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful())
                                throw task.getException();
                            return deporef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                Uri pp = task.getResult();
                                gonderi.setUrl(ppurl);
                                gonderi.setAciklama(aciklama);
                                gonderi.setIsim(isim);
                                gonderi.setUid(kullaniciID);
                                gonderi.setZaman(zaman);
                                gonderi.setSinav(sinav);
                                gonderi.setPostUri(pp.toString());
                                gonderi.setBegenisayisi(0);
                                gonderi.setYorumsayisi(0);

                                String id = vtreferansfoto.push().getKey();
                                gonderi.setKey(id);
                                vtreferansfoto.child(id).child("Gonderi Bilgileri").setValue(gonderi);

                                vtreferanspost.child(id).child("Gonderi Bilgileri").setValue(gonderi);

                                vtkullanicipost.child(id).child("Gonderi Bilgileri").setValue(gonderi);

                                vtkullantumpostlar.child(id).child("Gonderi Bilgileri").setValue(gonderi);

                                icerikEt.setText(" ");
                                icerikEt.setVisibility(View.VISIBLE);
                                Picasso.get().load((String) null).into(postfoto);

                                Toast.makeText(getContext(), "Gönderi başarıyla oluşturuldu.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Herhangi bir medya seçilmedi!",Toast.LENGTH_LONG).show();
                        }
                    });
                } else if(!TextUtils.isEmpty(aciklama)){
                    gonderi.setUrl(ppurl);
                    gonderi.setAciklama(aciklama);
                    gonderi.setIsim(isim);
                    gonderi.setUid(kullaniciID);
                    gonderi.setZaman(zaman);
                    gonderi.setSinav(sinav);
                    gonderi.setYorumsayisi(0);
                    gonderi.setBegenisayisi(0);

                    String id3 = vtreferanspost.push().getKey();
                    gonderi.setKey(id3);
                    vtreferanspost.child(id3).child("Gonderi Bilgileri").setValue(gonderi);

                    vtkullanicipost.child(id3).child("Gonderi Bilgileri").setValue(gonderi);

                    vtkullantumpostlar.child(id3).child("Gonderi Bilgileri").setValue(gonderi);

                    icerikEt.setText(" ");

                    Toast.makeText(getContext(), "Gönderi başarıyla oluşturuldu.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Açıklama ve medya eklenmedi. Lütfen en az bir alanı doldurunuz!",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
    private String dosyaUzantisiAl(Uri uri) {
        ContentResolver icerik = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((icerik.getType(uri)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FOTO_SEC || resultCode == RESULT_OK || data != null || data.getData() != null) {
            url = data.getData();
            Picasso.get().load(url).into(postfoto);
            postfoto.setVisibility(View.VISIBLE);
        } else if(data == null){
            postfoto.setVisibility(View.INVISIBLE);
        }
    }

}