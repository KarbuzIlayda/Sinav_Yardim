package com.ilayda.sinavyardim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilAltMenu extends BottomSheetDialogFragment {

    FirebaseFirestore vt = FirebaseFirestore.getInstance();
    DocumentReference dokumanreferans;
    CardView cv_cikis, cv_sil;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference vtreferans;
    String url;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.profil_alt_menu, container, false);

        cv_sil = view.findViewById(R.id.cv_hesapsil);
        cv_cikis = view.findViewById(R.id.cv_cikisyap);
        vtreferans = FirebaseDatabase.getInstance().getReference("Tum Kullanicilar");

        FirebaseUser kullanici = mAuth.getCurrentUser();
        String mevcutkullaniciID = kullanici.getUid();

        dokumanreferans = vt.collection("Kullanicilar/").document(mevcutkullaniciID);
        dokumanreferans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    url = task.getResult().getString("urlFoto");
                } else {

                }
            }
        });

        cv_cikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Çıkış Yap").setMessage("Çıkış yapmak istediğinize emin misiniz?")
                        .setPositiveButton("Evet, Çıkış Yap", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                            }
                        }).setNegativeButton("Hayır, Çıkış Yapma", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view = getLayoutInflater().inflate(R.layout.profil_alt_menu, container, false);
                            }
                        });
                builder.create();
                builder.show();
            }
        });

        cv_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Hesabı Sil").setMessage("Hesabu kalıcı olarak silmek istediğinizden emin misiniz?")
                        .setPositiveButton("Evet, Hesabı Sil", (dialogInterface, i) -> {
                                dokumanreferans.delete().addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                                    Query sorgu = vtreferans.orderByChild("uid").equalTo(mevcutkullaniciID);
                                    sorgu.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                dataSnapshot.getRef().removeValue();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    StorageReference deporeferans = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                    deporeferans.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(),"Hesap silme işlemi başarılı.",Toast.LENGTH_LONG).show();
                                            view = getLayoutInflater().inflate(R.layout.activity_signup, container, false);
                                        }
                                    });
                                });
                        }).setNegativeButton("Hayır, Hesabı Silme", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view = getLayoutInflater().inflate(R.layout.profil_alt_menu, container, false);
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        return view;
    }

}