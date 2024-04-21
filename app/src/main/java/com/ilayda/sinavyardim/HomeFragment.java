package com.ilayda.sinavyardim;

import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser kullanici = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference data_cek;
    String sinav;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (kullanici != null) {
            data_cek = db.collection("Kullanicilar/").document(kullanici.getUid());
            data_cek.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        sinav = task.getResult().getString("sinavSecimi");
                        FragmentTransaction degisim = getChildFragmentManager().beginTransaction();
                        switch (sinav) {
                            case "LGS":
                                degisim.replace(R.id.container_fragment, new LGSFragment());
                                break;
                            case "YKS":
                                degisim.replace(R.id.container_fragment, new YKSFragment());
                                break;
                            case "KPSS":
                                degisim.replace(R.id.container_fragment, new KPSSFragment());
                                break;
                            case "ALES":
                                degisim.replace(R.id.container_fragment, new ALESFragment());
                                break;
                            case "YDS":
                                degisim.replace(R.id.container_fragment, new YDSFragment());
                                break;
                            default:
                                break;
                        }
                        degisim.commit();
                    } else {
                        Toast.makeText(getContext(), "Bilgiler çekilirken bir sorunla karşılaşıldı!", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Veritabanı bağlantısı başarısız!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}