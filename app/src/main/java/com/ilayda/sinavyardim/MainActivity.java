package com.ilayda.sinavyardim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView altmenu = findViewById(R.id.alt_menu);
        altmenu.setOnNavigationItemSelectedListener(onNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment secili = null;

            if(item.getItemId() == R.id.alt_profil)
                secili = new ProfilFragment();
            else if(item.getItemId() == R.id.alt_mesaj)
                secili = new MesajFragment();
            else if(item.getItemId() == R.id.alt_paylas)
                secili = new PaylasFragment();
            else if(item.getItemId() == R.id.alt_home)
                secili = new HomeFragment();

            if(secili != null)
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, secili).commit();
            return true;
        }
    };
}