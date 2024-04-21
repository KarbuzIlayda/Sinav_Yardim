package com.ilayda.sinavyardim;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseOptions;
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
import com.ilayda.sinavyardim.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProfilFragment extends Fragment{
    RecyclerView gonderikutucuk;
    private List<Gonderi> gonderiler;
    KullaniciGonderiAdapter adapter;
    TextView tvkullaniciad, tvisim, tvsinav, tvbio;
    ImageButton ibproduzen, ibmenu;
    ImageView ivprofilfoto;
    FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans;
    String mevcutkullaniciID;
    DocumentReference dokumanreferans;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mevcutkullaniciID = kullanici.getUid();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        ibproduzen = view.findViewById(R.id.ib_produzenle);
        tvkullaniciad = view.findViewById(R.id.tv_kullaniciadgsoter);
        tvisim = view.findViewById(R.id.tv_isimgoster);
        tvsinav = view.findViewById(R.id.tv_sinavgoster);
        tvbio = view.findViewById(R.id.tv_biogoster);
        ibmenu = view.findViewById(R.id.ib_menu);
        ivprofilfoto = view.findViewById(R.id.ivprofilfoto);

        gonderikutucuk = view.findViewById(R.id.gonderiler);
        gonderikutucuk.setHasFixedSize(true);
        gonderikutucuk.setLayoutManager(new LinearLayoutManager(getContext()));

        ibmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new ProfilAltMenu();
                bottomSheetDialogFragment.show(getFragmentManager(),"altmenu");
            }
        });

        ibproduzen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfilOlustur.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        dokumanreferans = firestore.collection("Kullanicilar/").document(mevcutkullaniciID);
        dokumanreferans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String cekilenisim = task.getResult().getString("isim");
                    String cekilenkullanadi = task.getResult().getString("kullaniciAdi");
                    String cekilensinav = task.getResult().getString("sinavSecimi");
                    String cekilenbio = task.getResult().getString("bio");
                    String ppurl = task.getResult().getString("urlFoto");

                    if(ppurl != null && !ppurl.trim().isEmpty() && !TextUtils.equals(ppurl.trim(), " "))
                        Picasso.get().load(ppurl).into(ivprofilfoto);
                    tvkullaniciad.setText(cekilenkullanadi);
                    tvisim.setText(cekilenisim);
                    tvsinav.setText(cekilensinav);
                    tvbio.setText(cekilenbio);

                    if(adapter == null) {
                        gonderiler = new ArrayList<>();
                        vtreferans = veritabani.getReference("Kullanici Postlar").child(mevcutkullaniciID);

                        vtreferans.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                gonderiler.clear();
                                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                                    Gonderi gonderi = datasnapshot.child("Gonderi Bilgileri").getValue(Gonderi.class);
                                    gonderiler.add(gonderi);
                                }
                                adapter = new KullaniciGonderiAdapter(gonderiler, getContext());
                                gonderikutucuk.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Veriler getirilemedi...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Bilgiler alınırken bir sorunla karşılaşıldı!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), ProfilFragment.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Veritabanı bağlantı hatası!",Toast.LENGTH_LONG).show();
            }
        });
    }
}