package com.ilayda.sinavyardim;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class KullaniciProfili extends AppCompatActivity {
    TextView kullaniciad, isim, bio;
    ImageView pp;
    Button mesajbtn;
    RecyclerView gonderiler;
    private List<Gonderi> gonderileri;
    KullaniciGonderiAdapter adapter;
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans;
    DocumentReference dokumanreferans;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String kullaniciid, kullaniciadi, kullanicippurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullaniciprofili);

        kullaniciad = findViewById(R.id.kullaniciad);
        isim = findViewById(R.id.kullaniciisim);
        bio = findViewById(R.id.bio);
        pp = findViewById(R.id.profilfoto);
        mesajbtn = findViewById(R.id.mesajgonder);

        gonderiler = findViewById(R.id.kullanicigonderiler);
        gonderiler.setHasFixedSize(true);
        gonderiler.setLayoutManager(new LinearLayoutManager(this));

        Bundle hazirbilgiler = getIntent().getExtras();
        if(hazirbilgiler != null){
            kullaniciid = hazirbilgiler.getString("kullaniciid");
            kullaniciadi = hazirbilgiler.getString("kullaniciad");
        } else {
            Toast.makeText(this, "Kullanici bilgisi alınamadı.", Toast.LENGTH_LONG).show();
        }

        dokumanreferans = firestore.collection("Kullanicilar").document(kullaniciid);
        dokumanreferans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String cekilenisim = task.getResult().getString("isim");
                    kullaniciadi = task.getResult().getString("kullaniciAdi");
                    String cekilenbio = task.getResult().getString("bio");
                    kullanicippurl = task.getResult().getString("urlFoto");

                    Picasso.get().load(kullanicippurl).into(pp);
                    kullaniciad.setText(String.format("@%s", kullaniciadi));
                    isim.setText(cekilenisim);
                    bio.setText(cekilenbio);

                    if(adapter == null) {
                        gonderileri = new ArrayList<>();
                        vtreferans = veritabani.getReference("Kullanici Postlar").child(kullaniciid);

                        vtreferans.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                gonderileri.clear();
                                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                                    Gonderi gonderi = datasnapshot.child("Gonderi Bilgileri").getValue(Gonderi.class);
                                    gonderileri.add(gonderi);
                                }
                                adapter = new KullaniciGonderiAdapter(gonderileri, KullaniciProfili.this);
                                gonderiler.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(KullaniciProfili.this, "Veriler getirilemedi...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(KullaniciProfili.this, "Bilgiler alınırken bir sorunla karşılaşıldı!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(KullaniciProfili.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KullaniciProfili.this, "Veritabanı bağlantı hatası!",Toast.LENGTH_LONG).show();
            }
        });

        mesajbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mesajagec = new Intent(KullaniciProfili.this, Mesajlasma.class);
                Bundle mesaja = new Bundle();
                mesaja.putString("aliciid", kullaniciid);
                mesaja.putString("aliciisim", kullaniciadi);
                mesaja.putString("alicipp", kullanicippurl);
                mesajagec.putExtra("mesajagecbundle", mesaja);
                startActivity(mesajagec);
            }
        });
    }

}