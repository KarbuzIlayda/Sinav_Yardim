
package com.ilayda.sinavyardim;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Mesajlasma extends AppCompatActivity {
    String aliciid, aliciisim, alicippurl, gondericiid, gondericiisim, mes, gorulmetar, gorulmesa, mesajid;
    TextView aliciadi;
    ImageView alicipp;
    EditText mesaj;
    RecyclerView mesajlar;
    ImageButton btngonder;
    Mesaj mesajNesne;
    GelenKutusu gelenler;
    DatabaseReference vtreferansalici, vtreferansgonderen, vtgelenkutusu;
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesajlasma);

        Bundle alicibilgiler = getIntent().getBundleExtra("mesajagecbundle");
        Bundle bilgiler = getIntent().getBundleExtra("mesajagirbundle");
        if(alicibilgiler != null){
            aliciid = alicibilgiler.getString("aliciid");
            aliciisim = alicibilgiler.getString("aliciisim");
            alicippurl = alicibilgiler.getString("alicipp");
        } else if(bilgiler != null){
            aliciid = bilgiler.getString("aliciid");
            aliciisim = bilgiler.getString("aliciisim");
            alicippurl = bilgiler.getString("alicipp");
        } else {
            Toast.makeText(Mesajlasma.this, "Alıcı taraf bilgileri alınamadı.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Mesajlasma.this, KullaniciProfili.class));
        }

        mesajNesne = new Mesaj();

        mesajlar = findViewById(R.id.rv_mesajlar);
        mesajlar.setHasFixedSize(false);
        mesajlar.setLayoutManager(new LinearLayoutManager(Mesajlasma.this));

        alicipp = findViewById(R.id.alicipp);
        aliciadi = findViewById(R.id.aliciadi);
        mesaj = findViewById(R.id.mesaj);
        btngonder = findViewById(R.id.mesajigonder);

        if(!TextUtils.isEmpty(alicippurl) || !TextUtils.equals(alicippurl, "")){
            Picasso.get().load(alicippurl).into(alicipp);
        }
        aliciadi.setText(aliciisim);

        FirebaseUser mevcutkullanici = FirebaseAuth.getInstance().getCurrentUser();
        gondericiid = mevcutkullanici.getUid();

        DocumentReference mevcutkullanicibilgi = FirebaseFirestore.getInstance().collection("Kullanicilar").document(mevcutkullanici.getUid());
        mevcutkullanicibilgi.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    gondericiisim = task.getResult().getString("kullaniciAdi");
                }
            }
        });

        vtreferansgonderen = veritabani.getReference("Mesajlar").child(gondericiid).child(aliciid);
        vtreferansalici = veritabani.getReference("Mesajlar").child(aliciid).child(gondericiid);

        btngonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mes = mesaj.getText().toString();

                Calendar ctarih = Calendar.getInstance();
                SimpleDateFormat mevcuttarih = new SimpleDateFormat("dd-MMMM-yyyy");
                final String kayitlitarih = mevcuttarih.format(ctarih.getTime());

                Calendar csaat = Calendar.getInstance();
                SimpleDateFormat mevcutsaat = new SimpleDateFormat("HH:mm:ss");
                final String kayitlisaat = mevcutsaat.format(csaat.getTime());

                String gonderitarih = kayitlitarih + ":" + kayitlisaat;

                if(mes.isEmpty()){
                    Toast.makeText(Mesajlasma.this, "Boş mesaj atamazsınız!",Toast.LENGTH_LONG).show();
                } else {
                    mesajNesne.setMesaj(mes);
                    mesajNesne.setAliciid(aliciid);
                    mesajNesne.setAliciisim(aliciisim);
                    mesajNesne.setGonderenid(gondericiid);
                    mesajNesne.setGonderenisim(gondericiisim);
                    mesajNesne.setGonderimtarih(kayitlitarih);
                    mesajNesne.setGonderimsaat(kayitlisaat);
                    mesajNesne.setGorulmetarih(" ");
                    mesajNesne.setGorulmesaat(" ");

                    mesajid = vtreferansgonderen.push().getKey();
                    mesajNesne.setMesajid(mesajid);
                    vtreferansgonderen.child(mesajid).setValue(mesajNesne);
                    vtreferansalici.child(mesajid).setValue(mesajNesne);

                    gelenler = new GelenKutusu();
                    gelenler.setAliciid(gondericiid);
                    gelenler.setGonderenid(aliciid);
                    gelenler.setGonderenisim(aliciisim);
                    gelenler.setGonderilmesaat(kayitlisaat);
                    gelenler.setGonderilmetarih(kayitlitarih);
                    gelenler.setGorulmetarih("gönderildi");
                    gelenler.setMesaj(mes);

                    vtgelenkutusu = veritabani.getReference("Gelen Kutulari").child(gondericiid).child(aliciid);
                    vtgelenkutusu.setValue(gelenler);

                    gelenler.setAliciid(aliciid);
                    gelenler.setGonderenid(gondericiid);
                    gelenler.setGonderenisim(gondericiisim);
                    gelenler.setGorulmetarih(" ");

                    DatabaseReference vtgelenalici = veritabani.getReference("Gelen Kutulari").child(aliciid).child(gondericiid);
                    vtgelenalici.setValue(gelenler);

                    mesaj.setText("");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Mesaj> gonderen = new FirebaseRecyclerOptions.Builder<Mesaj>().setQuery(vtreferansgonderen, Mesaj.class).build();
        FirebaseRecyclerAdapter<Mesaj, MesajViewHolder> gonderenadapter = new FirebaseRecyclerAdapter<Mesaj, MesajViewHolder>(gonderen) {
            @Override
            protected void onBindViewHolder(@NonNull MesajViewHolder holder, int position, @NonNull Mesaj model) {

                holder.setMesaj(getApplication(), model.getGonderenid(), model.getAliciid(), model.getGonderenisim(), model.getAliciisim(),
                        model.getMesaj(), model.getGonderimsaat(), model.getGonderimtarih(), model.getGorulmesaat(), model.getGonderimtarih(), model.getMesajid());

                if(TextUtils.equals(model.getGorulmesaat()," ") && TextUtils.equals(model.getGorulmetarih()," ")){
                    holder.goruldusekli.setTextColor(Color.BLACK);
                } else {
                    holder.goruldusekli.setTextColor(Color.WHITE);
                }

                alicipp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profilegit = new Intent(Mesajlasma.this, KullaniciProfili.class);
                        profilegit.putExtra("kullaniciid", model.getGonderenid());
                        profilegit.putExtra("kullaniciad", model.getGonderenisim());
                        startActivity( profilegit);
                    }
                });
            }

            @NonNull
            @Override
            public MesajViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mesaj_layout, parent, false);

                return new MesajViewHolder(view);
            }
        };
        gonderenadapter.startListening();
        mesajlar.setAdapter(gonderenadapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(gonderenadapter.getItemCount() != 0)
                    mesajlar.smoothScrollToPosition(gonderenadapter.getItemCount() - 1);
            }
        }, 800);

        gorulduListener(aliciid);
    }
    private void gorulduListener(String mevcutkullaniciId){

        DatabaseReference dinleyici = veritabani.getReference("Mesajlar").child(aliciid).child(gondericiid);
        dinleyici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cocuk : snapshot.getChildren()){
                    if(cocuk.exists()){
                        Mesaj mesajlar = cocuk.getValue(Mesaj.class);
                        if(mesajlar != null) {
                            String mesajid = mesajlar.getMesajid();
                            if (TextUtils.equals(mesajlar.getAliciid(), gondericiid)) {
                                if (TextUtils.equals(mesajlar.getGorulmesaat(), " ") && TextUtils.equals(mesajlar.getGorulmetarih(), " ")) {
                                    Calendar ctarih = Calendar.getInstance();
                                    SimpleDateFormat mevcuttarih = new SimpleDateFormat("dd-MMMM-yyyy");
                                    final String kayitlitarih = mevcuttarih.format(ctarih.getTime());

                                    Calendar csaat = Calendar.getInstance();
                                    SimpleDateFormat mevcutsaat = new SimpleDateFormat("HH:mm:ss");
                                    final String kayitlisaat = mevcutsaat.format(csaat.getTime());

                                    mesajlar.setGorulmesaat(kayitlisaat);
                                    mesajlar.setGorulmetarih(kayitlitarih);
                                    dinleyici.child(mesajid).setValue(mesajlar);
                                    vtreferansgonderen.child(mesajid).setValue(mesajlar);

                                    vtgelenkutusu = veritabani.getReference("Gelen Kutulari").child(gondericiid).child(aliciid);
                                    vtgelenkutusu.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    GelenKutusu gelen = snapshot.getValue(GelenKutusu.class);
                                                    if(gelen != null) {
                                                        gelen.setGorulmetarih(kayitlitarih);
                                                        gelen.setGorulmesaat(kayitlisaat);
                                                        vtgelenkutusu.setValue(gelen);
                                                    }
                                                }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    DatabaseReference vtgelenalici = veritabani.getReference("Gelen Kutulari").child(aliciid).child(gondericiid);
                                    vtgelenalici.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                GelenKutusu gelen = snapshot.getValue(GelenKutusu.class);
                                                if(gelen != null) {
                                                    gelen.setGorulmetarih(kayitlitarih);
                                                    gelen.setGorulmesaat(kayitlisaat);
                                                    vtgelenalici.setValue(gelen);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                            dinleyici.removeEventListener(this);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Mesajlasma.this, "Mesajları alırken bir hata ile karşılaşıldı.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}