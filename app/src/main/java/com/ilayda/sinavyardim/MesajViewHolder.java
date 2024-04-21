package com.ilayda.sinavyardim;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MesajViewHolder extends RecyclerView.ViewHolder {
    TextView tvgondericimesaj, tvalicimesaj, goruldusekli, gonderisaat, alicigonderim;
    String mevcutkulid;
    Context icerik;
    public MesajViewHolder(@NonNull View itemView) {
        super(itemView);
        icerik = itemView.getContext();
    }
    public void setMesaj(Application application, String gondericiid, String aliciid, String gondericiisim, String aliciisim, String mesaj,
                         String gonderimsaat, String gonderimtarih, String gorulmesaat, String gorulmetarih, String mesajid) {

        tvgondericimesaj = itemView.findViewById(R.id.gonderici_mesaj);
        tvalicimesaj = itemView.findViewById(R.id.alici_mesaj);
        goruldusekli = itemView.findViewById(R.id.gorulme_oku);
        gonderisaat = itemView.findViewById(R.id.gonderilsaat);
        alicigonderim = itemView.findViewById(R.id.alicigonderim);

        FirebaseUser mevcutkullanici = FirebaseAuth.getInstance().getCurrentUser();
        mevcutkulid = mevcutkullanici.getUid();

        if (TextUtils.equals(mevcutkulid, gondericiid)) {
            tvalicimesaj.setVisibility(View.GONE);
            tvgondericimesaj.setText(mesaj);
            goruldusekli.setVisibility(View.VISIBLE);
            gonderisaat.setText(gonderimsaat);
            alicigonderim.setVisibility(View.GONE);
        } else if (TextUtils.equals(mevcutkulid, aliciid)) {
            tvgondericimesaj.setVisibility(View.GONE);
            tvalicimesaj.setText(mesaj);
            goruldusekli.setVisibility(View.GONE);
            gonderisaat.setVisibility(View.GONE);
            alicigonderim.setText(gonderimsaat);
        }


    }
}
