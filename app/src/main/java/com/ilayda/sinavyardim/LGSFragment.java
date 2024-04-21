package com.ilayda.sinavyardim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LGSFragment extends Fragment {
    RecyclerView postlar;
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans;
    private List<Gonderi> gonderiler;
    KullaniciGonderiAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lgs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postlar = view.findViewById(R.id.rv_lgshome);
        postlar.setHasFixedSize(true);
        postlar.setLayoutManager(new LinearLayoutManager(getContext()));

        if(adapter == null) {
            gonderiler = new ArrayList<>();
            vtreferans = veritabani.getReference("LGS postlar");

            vtreferans.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    gonderiler.clear();
                    for(DataSnapshot datasnapshot : snapshot.getChildren()){
                        Gonderi gonderi = datasnapshot.child("Gonderi Bilgileri").getValue(Gonderi.class);
                        gonderiler.add(gonderi);
                    }
                    adapter = new KullaniciGonderiAdapter(gonderiler, getContext());
                    postlar.setAdapter(adapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Veriler çekilemedi...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}