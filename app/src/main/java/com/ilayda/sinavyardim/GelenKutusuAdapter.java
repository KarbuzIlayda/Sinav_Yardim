package com.ilayda.sinavyardim;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GelenKutusuAdapter extends RecyclerView.Adapter<GelenKutusuAdapter.GelenKutuViewHolder> {
    private List<GelenKutusu> gelenkutusu;
    Context icerik;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String ppurl;
    boolean mesajlaryuklendi = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    public GelenKutusuAdapter(List<GelenKutusu> gelen, Context icerik){
        this.gelenkutusu = gelen;
        this.icerik = icerik;
    }
    public class GelenKutuViewHolder extends RecyclerView.ViewHolder{
        CardView okunmusmesaj, okunmamismesaj;
        TextView memiskullaniciad, sonmesaj, memistarih, muskullaniciad, mustarih, memismesaj;
        ImageView kullanicipp, memispp;
        public GelenKutuViewHolder(@NonNull View itemView) {
            super(itemView);

            okunmusmesaj = itemView.findViewById(R.id.cv_okunmusmesaj); //gorulen
            kullanicipp = itemView.findViewById(R.id.mesajalicipp); //gorulen
            muskullaniciad = itemView.findViewById(R.id.mesajaliciad); //gorulen
            sonmesaj = itemView.findViewById(R.id.gelensonmesaj); //gorulen
            mustarih = itemView.findViewById(R.id.goruldugonderilmetarihi); //gorulen

            memistarih = itemView.findViewById(R.id.gorulmedigonderilmetarihi);
            okunmamismesaj = itemView.findViewById(R.id.cv_okunmamismesaj);
            memiskullaniciad = itemView.findViewById(R.id.mesajgormediad);
            memispp = itemView.findViewById(R.id.mesajgormedipp);
            memismesaj = itemView.findViewById(R.id.gorulmemissonmesaj);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GelenKutuViewHolder holder, int position) {
        if(gelenkutusu.get(position) != null){

            DocumentReference ppicin = firestore.collection("Kullanicilar").document(gelenkutusu.get(position).getGonderenid());
            ppicin.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()){
                        ppurl = task.getResult().getString("urlFoto");

                        if(!mesajlaryuklendi) {
                            if (TextUtils.equals(gelenkutusu.get(position).getGorulmesaat(), " ") || TextUtils.equals(gelenkutusu.get(position).getGorulmetarih(), " ")) {

                                holder.okunmusmesaj.setVisibility(View.GONE);
                                holder.okunmamismesaj.setVisibility(View.VISIBLE);
                                holder.memiskullaniciad.setText(gelenkutusu.get(position).getGonderenisim());
                                holder.memismesaj.setText(gelenkutusu.get(position).getMesaj());
                                holder.memistarih.setText(gelenkutusu.get(position).getGonderilmetarih() + " " + gelenkutusu.get(position).getGonderilmesaat());
                                if (ppurl != null && !ppurl.trim().isEmpty() && !TextUtils.equals(ppurl.trim(), " "))
                                    Picasso.get().load(ppurl).into(holder.memispp);

                                holder.okunmamismesaj.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent mesajagir = new Intent(icerik, Mesajlasma.class);
                                        Bundle mesaja = new Bundle();
                                        mesaja.putString("aliciid", gelenkutusu.get(position).getGonderenid());
                                        mesaja.putString("aliciisim", gelenkutusu.get(position).getGonderenisim());
                                        mesaja.putString("alicipp", ppurl);
                                        mesajagir.putExtra("mesajagirbundle", mesaja);
                                        startActivity(icerik, mesajagir, null);
                                    }
                                });

                                holder.memispp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent profilegit = new Intent(icerik, KullaniciProfili.class);
                                        profilegit.putExtra("kullaniciid", gelenkutusu.get(position).getGonderenid());
                                        profilegit.putExtra("kullaniciad", gelenkutusu.get(position).getGonderenisim());
                                        startActivity(icerik, profilegit, null);
                                    }
                                });
                            } else {
                                holder.okunmamismesaj.setVisibility(View.GONE);
                                holder.okunmusmesaj.setVisibility(View.VISIBLE);
                                holder.muskullaniciad.setText(gelenkutusu.get(position).getGonderenisim());
                                holder.sonmesaj.setText(gelenkutusu.get(position).getMesaj());
                                holder.mustarih.setText(gelenkutusu.get(position).getGonderilmetarih() + " " + gelenkutusu.get(position).getGonderilmesaat());
                                if (ppurl != null && !ppurl.trim().isEmpty() && !TextUtils.equals(ppurl.trim(), " "))
                                    Picasso.get().load(ppurl).into(holder.kullanicipp);

                                holder.okunmusmesaj.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent mesajagir = new Intent(icerik, Mesajlasma.class);
                                        Bundle mesaja = new Bundle();
                                        mesaja.putString("aliciid", gelenkutusu.get(position).getGonderenid());
                                        mesaja.putString("aliciisim", gelenkutusu.get(position).getGonderenisim());
                                        mesaja.putString("alicipp", ppurl);
                                        mesajagir.putExtra("mesajagirbundle", mesaja);
                                        startActivity(icerik, mesajagir, null);
                                    }
                                });
                                holder.kullanicipp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent profilegit = new Intent(icerik, KullaniciProfili.class);
                                        profilegit.putExtra("kullaniciid", gelenkutusu.get(position).getGonderenid());
                                        profilegit.putExtra("kullaniciad", gelenkutusu.get(position).getGonderenisim());
                                        startActivity(icerik, profilegit, null);
                                    }
                                });
                            }
                            if (position == getItemCount() - 1) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                        mesajlaryuklendi = true;
                                    }
                                });
                            }
                        }
                    }
                }
            });


        }
    }

    @NonNull
    @Override
    public GelenKutuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mesajkutusu_rv, parent, false);
        return new GelenKutuViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return gelenkutusu.size();
    }

}
