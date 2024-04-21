package com.ilayda.sinavyardim;

import static androidx.core.content.ContextCompat.getDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class YorumYapActivity extends AppCompatActivity {
    String kullanicipp, postkey, yorumfotouri, kullanicisim, sinav, postsahibikey;
    Integer sayi, begensayi;
    Boolean tiklandi = false;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DocumentReference dokumanreferans;
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans, yorumsayisi, vtsinav, yorumsayisi1, vtkullanici, yorumsayisi2;
    FirebaseUser kullanici;
    StorageReference deporeferans;
    EditText yorum;
    ImageView pp;
    Button yorumgonder;
    ImageView yorumfoto;
    RecyclerView yapilmisyorumlar;
    List<YorumBilgileri> yorumbilgi;
    Uri fotouri;
    UploadTask yukleme;
    static final int GALERIDEN_SEC = 0;
    static final int FOTO_CEK = 1;
    YorumBilgileri yorumNesne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorumyap);

        yorumNesne = new YorumBilgileri();
        yorum = findViewById(R.id.et_yorumyaz);
        pp = findViewById(R.id.yorumpp);
        yorumgonder = findViewById(R.id.btn_yorumgonder);
        yorumfoto = findViewById(R.id.yorumfoto);

        yapilmisyorumlar = findViewById(R.id.rv_yorumlar);
        yapilmisyorumlar.setHasFixedSize(false);
        yapilmisyorumlar.setLayoutManager(new LinearLayoutManager(this));

        Bundle gonderilmisbilgiler = getIntent().getExtras();
        if(gonderilmisbilgiler != null) {
            postkey = gonderilmisbilgiler.getString("key");
            sayi = gonderilmisbilgiler.getInt("yorumsayisi");
            sinav = gonderilmisbilgiler.getString("sinav");
            postsahibikey = gonderilmisbilgiler.getString("sahipid");
            yorumNesne.setPostkey(postkey);
        } else {
            Toast.makeText(this, "Gönderi bilgilerini alırken bir sorunla karşılaşıldı!", Toast.LENGTH_LONG).show();
        }

        kullanici = FirebaseAuth.getInstance().getCurrentUser();
        yorumNesne.setYorumcuid(kullanici.getUid());
        dokumanreferans = firestore.collection("Kullanicilar").document(kullanici.getUid());

        vtreferans = veritabani.getReference("Tum Postlar").child(postkey).child("Yorumlar");
        vtsinav = veritabani.getReference(sinav+" postlar").child(postkey).child("Yorumlar");
        vtkullanici = veritabani.getReference("Kullanici Postlar").child(postsahibikey).child(postkey).child("Yorumlar");

        yorumgonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yaziliyorum = yorum.getText().toString();
                deporeferans = FirebaseStorage.getInstance().getReference("Yorum Fotolari");
                final StorageReference referans;

                if(fotouri != null){
                    referans = deporeferans.child(System.currentTimeMillis() + "." + dosyaUzantisiAl(fotouri));
                    yukleme = referans.putFile(fotouri);
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
                            if(task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                Log.d("YorumYapActivity", "Setting Yorumfoto: " + downloadUri.toString());
                                yorumNesne.setYorumfoto(downloadUri.toString());
                                yorumNesne.setYorumyazi(yaziliyorum);

                                String yorumkey = vtreferans.push().getKey();
                                yorumNesne.setYorumkey(yorumkey);
                                yorumNesne.setBegenisayisi(0);
                                yorumNesne.setYorumsayisi(0);
                                vtreferans.child(yorumkey).setValue(yorumNesne);

                                vtsinav.child(yorumkey).setValue(yorumNesne);

                                vtkullanici.child(yorumkey).setValue(yorumNesne);

                                yorum.setText(" ");
                                Picasso.get().load((String)null).into(yorumfoto);
                            }
                        }
                    });
                } else {
                    yorumNesne.setYorumyazi(yaziliyorum);

                    String yorumkey = vtreferans.push().getKey();
                    yorumNesne.setYorumkey(yorumkey);
                    yorumNesne.setBegenisayisi(0);
                    yorumNesne.setYorumsayisi(0);
                    vtreferans.child(yorumkey).setValue(yorumNesne);

                    vtsinav.child(yorumkey).setValue(yorumNesne);

                    vtkullanici.child(yorumkey).setValue(yorumNesne);

                    yorum.setText(" ");
                }

                yorumsayisi = veritabani.getReference("Tum Postlar").child(postkey).child("Gonderi Bilgileri");
                yorumsayisi.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Gonderi gonderi = snapshot.getValue(Gonderi.class);
                        if(gonderi != null)
                            gonderi.setYorumsayisi(sayi+1);
                        yorumsayisi.setValue(gonderi);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(YorumYapActivity.this, "Veritabanı hatası", Toast.LENGTH_SHORT).show();
                    }
                });
                yorumsayisi1 = veritabani.getReference(sinav+" postlar").child(postkey).child("Gonderi Bilgileri");
                yorumsayisi1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Gonderi gonderi = snapshot.getValue(Gonderi.class);
                        if(gonderi != null)
                            gonderi.setYorumsayisi(sayi+1);
                        yorumsayisi1.setValue(gonderi);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(YorumYapActivity.this, "Veritabanı hatası", Toast.LENGTH_SHORT).show();
                    }
                });
                yorumsayisi2 = veritabani.getReference("Kullanici Postlar").child(postsahibikey).child(postkey).child("Gonderi Bilgileri");
                yorumsayisi2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Gonderi gonderi = snapshot.getValue(Gonderi.class);
                        if(gonderi != null)
                            gonderi.setYorumsayisi(sayi+1);
                        yorumsayisi2.setValue(gonderi);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(YorumYapActivity.this, "Veritabanı hatası", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(YorumYapActivity.this, "Yorum başarıyla paylaşıldı.",Toast.LENGTH_SHORT).show();
            }
        });

        yorumfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotosec();
            }
        });

        yorum.setText(" ");

    }
    private String dosyaUzantisiAl(Uri uri) {
        ContentResolver icerik = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((icerik.getType(uri)));
    }

    private void fotosec() {
        AlertDialog.Builder fotosecici = new AlertDialog.Builder(this);
        fotosecici.setTitle("Fotoğrafı eklemek istediğiniz seçeneği seçiniz...")
                .setItems(new CharSequence[]{"Galeriden Seç", "Fotoğraf Çek"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                Intent fotosec = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(fotosec, GALERIDEN_SEC);
                                break;
                            case 1:
                                Intent fotocek = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(fotocek.resolveActivity(getPackageManager()) != null) {
                                    File fotoDosya = null;
                                    try{
                                        fotoDosya = fotoDosyaOlustur();
                                    } catch(IOException ex){
                                        Toast.makeText(YorumYapActivity.this, "Dosya oluşturulamadı",Toast.LENGTH_SHORT).show();
                                    }
                                    if(fotoDosya != null) {
                                        fotouri = FileProvider.getUriForFile(YorumYapActivity.this, "com.ilayda.sinavyardim.fileprovider", fotoDosya);
                                        fotocek.putExtra(MediaStore.EXTRA_OUTPUT, fotouri);
                                        startActivityForResult(fotocek, FOTO_CEK);
                                    }
                                }
                                break;
                        }
                    }
                });
        fotosecici.show();
    }

    private File fotoDosyaOlustur() throws IOException{
        try {
            String zaman = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fotodosyaisim = "JPEG_" + zaman + "_";
            File dosyaDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File foto = File.createTempFile(fotodosyaisim, ".jpg", dosyaDir);
            yorumfotouri = foto.getAbsolutePath();
            return foto;
        } catch(IOException ex){
            ex.printStackTrace();
            Log.e("FotoFile", "Error creating file: " + ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch(requestCode){
                case GALERIDEN_SEC:
                    if(data != null) {
                        fotouri = data.getData();
                        yorumfoto.setImageURI(fotouri);
                    } else {
                        Toast.makeText(this, "Fotoğraf galeriden alınırken bir sorunla karşılaşıldı...", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FOTO_CEK:
                    if(fotouri != null) {
                        yorumfoto.setImageURI(fotouri);
                    } else {
                        Toast.makeText(this, "Fotoğraf alınırken bir sorunla karşılaşıldı...", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        dokumanreferans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    kullanicipp = task.getResult().getString("urlFoto");
                    kullanicisim = task.getResult().getString("isim");
                    yorumNesne.setYorumcuisim(kullanicisim);
                    yorumNesne.setYorumcupp(kullanicipp);
                    if(!TextUtils.equals(kullanicipp, "") || !TextUtils.isEmpty(kullanicipp)){
                        Picasso.get().load(kullanicipp).into(pp);
                    }
                }
            }
        });

        FirebaseRecyclerOptions<YorumBilgileri> options = new FirebaseRecyclerOptions.Builder<YorumBilgileri>()
                .setQuery(vtsinav, YorumBilgileri.class).build();

        FirebaseRecyclerAdapter<YorumBilgileri, YorumViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<YorumBilgileri, YorumViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull YorumViewHolder holder, int position, @NonNull YorumBilgileri model) {
                        holder.setYorum(getApplication(), model.getPostkey(), model.getYorumkey(), model.getYorumcuid(),
                                model.getYorumcuisim(), model.getYorumcupp(), model.getYorumyazi(), model.getYorumfoto(),
                                model.getYorumsayisi());

                        DatabaseReference vtrefbuton = FirebaseDatabase.getInstance().getReference("Yorum Begenileri");
                        vtrefbuton.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(model.getYorumkey() != null && snapshot.hasChild(model.getYorumkey())) {
                                    begensayi = (int) snapshot.child(model.getYorumkey()).getChildrenCount();
                                    holder.yorumbegenisayi.setText(String.valueOf(begensayi));
                                    if (snapshot.child(model.getYorumkey()).hasChild(kullanici.getUid())) {
                                        holder.yorumbegen.setImageDrawable(getDrawable(R.drawable.kalp_icidolu));
                                    } else {
                                        holder.yorumbegen.setImageDrawable(getDrawable(R.drawable.kalp_icibos));
                                    }
                                } else {
                                    holder.yorumbegenisayi.setText("0");
                                    holder.yorumbegen.setImageDrawable(getDrawable(R.drawable.kalp_icibos));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        holder.yorumbegen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tiklandi = true;
                                DatabaseReference vtref = FirebaseDatabase.getInstance().getReference("Yorum Begenileri");
                                vtref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(tiklandi) {
                                            if(model.getYorumkey() != null) {
                                                if (!snapshot.child(model.getYorumkey()).hasChild(kullanici.getUid())) {
                                                    vtref.child(model.getYorumkey()).child(kullanici.getUid()).setValue(true);
                                                    tiklandi = true;
                                                } else {
                                                    vtref.child(model.getYorumkey()).child(kullanici.getUid()).removeValue();
                                                    tiklandi = false;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public YorumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yorum_kartlari, parent, false);
                        return new YorumViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        yapilmisyorumlar.setAdapter(firebaseRecyclerAdapter);
    }
}