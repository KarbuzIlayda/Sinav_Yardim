package com.ilayda.sinavyardim;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContextCompat.getDrawable;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KullaniciGonderiAdapter extends RecyclerView.Adapter<KullaniciGonderiAdapter.KullaniciGonderiHolder> {
    private List<Gonderi> gonderiler;
    Context icerik;
    KullaniciUyeler kullanici;
    String mevcutkullaniciid;
    Integer begenisay;
    DatabaseReference vtrefkullanici, vtref;
    Boolean tiklandi = false;
    public KullaniciGonderiAdapter(List<Gonderi> gonderiler, Context icerik) {
        this.gonderiler = gonderiler;
        this.icerik = icerik;
    }
    static class KullaniciGonderiHolder extends RecyclerView.ViewHolder {
        ImageView kullanicifoto, gonderifoto;
        TextView kullaniciadi, aciklama, begeni, yorum;
        ImageButton btnyorum, btnsecenek, btnbegen;
        public KullaniciGonderiHolder(@NonNull View itemView) {
            super(itemView);

            kullanicifoto = itemView.findViewById(R.id.ivprofilfoto);
            gonderifoto = itemView.findViewById(R.id.ivfotopost);
            kullaniciadi = itemView.findViewById(R.id.tv_kullaniciad);
            aciklama = itemView.findViewById(R.id.tvyazipost);
            yorum = itemView.findViewById(R.id.tv_yorumsayi);
            btnyorum = itemView.findViewById(R.id.ib_yorum);
            btnsecenek = itemView.findViewById(R.id.post_secenekler);
            btnbegen = itemView.findViewById(R.id.ib_begen);
            begeni = itemView.findViewById(R.id.tv_begensayi);
        }
    }
    @NonNull
    @Override
    public KullaniciGonderiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_posts, parent, false);
        return new KullaniciGonderiHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KullaniciGonderiHolder holder, int position) {
        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();
        mevcutkullaniciid = kullanici.getUid();

        if(gonderiler.get(position) != null) {

            holder.kullaniciadi.setText(gonderiler.get(position).getIsim());
            holder.yorum.setText(String.valueOf(gonderiler.get(position).getYorumsayisi()));
            if(gonderiler.get(position).getUrl() != null && !gonderiler.get(position).getUrl().trim().isEmpty() && !TextUtils.equals(gonderiler.get(position).getUrl().trim(), " "))
                Picasso.get().load(gonderiler.get(position).getUrl()).into(holder.kullanicifoto);

            String acik = gonderiler.get(position).getAciklama();
            if (!TextUtils.isEmpty(acik) || !TextUtils.equals(acik, "")) {
                holder.aciklama.setText(gonderiler.get(position).getAciklama());
            } else {
                holder.aciklama.setVisibility(View.INVISIBLE);
            }

            String postp = gonderiler.get(position).getPostUri();
            if (!TextUtils.isEmpty(postp) || !TextUtils.equals(postp, "")) {
                Picasso.get().load(gonderiler.get(position).getPostUri()).into(holder.gonderifoto);
            } else {
                holder.aciklama.setVisibility(View.INVISIBLE);
            }

            holder.btnyorum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(icerik, YorumYapActivity.class);
                    intent.putExtra("yorumsayisi", gonderiler.get(position).getYorumsayisi());
                    intent.putExtra("key", gonderiler.get(position).getKey());
                    intent.putExtra("sinav",gonderiler.get(position).getSinav());
                    intent.putExtra("sahipid",gonderiler.get(position).getUid());
                    startActivity(icerik, intent, null);
                }
            });

            holder.kullanicifoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profiliac = new Intent(icerik, KullaniciProfili.class);
                    profiliac.putExtra("kullaniciid", gonderiler.get(position).getUid());
                    startActivity(icerik, profiliac, null);
                }
            });

            final String gonderiKey = gonderiler.get(position).getKey();

            DatabaseReference vtrefbuton = FirebaseDatabase.getInstance().getReference("Gonderi Begenileri");
            vtrefbuton.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(gonderiKey != null && snapshot.hasChild(gonderiKey)) {
                        begenisay = (int) snapshot.child(gonderiKey).getChildrenCount();
                        holder.begeni.setText(String.valueOf(begenisay));
                        if (snapshot.child(gonderiKey).hasChild(mevcutkullaniciid)) {
                            holder.btnbegen.setImageDrawable(getDrawable(icerik, R.drawable.kalp_icidolu));
                        } else {
                            holder.btnbegen.setImageDrawable(getDrawable(icerik, R.drawable.kalp_icibos));
                        }
                    } else {
                        holder.begeni.setText("0");
                        holder.btnbegen.setImageDrawable(getDrawable(icerik, R.drawable.kalp_icibos));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            holder.btnbegen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tiklandi = true;
                    vtref = FirebaseDatabase.getInstance().getReference("Gonderi Begenileri");
                    vtref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(tiklandi) {
                                if(gonderiKey != null) {
                                    if (!snapshot.child(gonderiKey).hasChild(mevcutkullaniciid)) {
                                        vtref.child(gonderiKey).child(mevcutkullaniciid).setValue(true);
                                        tiklandi = true;
                                    } else {
                                        vtref.child(gonderiKey).child(mevcutkullaniciid).removeValue();
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
    }
    @Override
    public int getItemCount() {
        return gonderiler.size();
    }
}
