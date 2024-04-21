package com.ilayda.sinavyardim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MesajFragment extends Fragment {
    RecyclerView gelenmesajlar;
    String mevcutkulid;
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans;
    GelenKutusu gelenler;
    List<GelenKutusu> mesajatanlar;
    GelenKutusuAdapter kutuadapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();
        mevcutkulid = kullanici.getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mesaj, container, false);

        vtreferans = veritabani.getReference("Gelen Kutulari").child(mevcutkulid);
        gelenler = new GelenKutusu();
        mesajatanlar = new ArrayList<>();

        gelenmesajlar = view.findViewById(R.id.rv_mesajkutusu);
        gelenmesajlar.setHasFixedSize(true);
        gelenmesajlar.setLayoutManager(new LinearLayoutManager(getContext()));

        if(kutuadapter == null){
            vtreferans.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mesajatanlar.clear();

                    for(DataSnapshot cocuk : snapshot.getChildren()){
                        if(cocuk != null){
                            GelenKutusu mesaj = cocuk.getValue(GelenKutusu.class);
                            mesajatanlar.add(mesaj);
                        }
                    }
                    kutuadapter = new GelenKutusuAdapter(mesajatanlar, getContext());
                    gelenmesajlar.setAdapter(kutuadapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }
}