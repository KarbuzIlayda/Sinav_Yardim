package com.ilayda.sinavyardim;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.ContextUtils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, sifreEt;
    Button btn_giris;
    TextView kayitadon;
    CheckBox sifregoster;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.editTextMail);
        sifreEt = findViewById(R.id.editTextSifre);
        btn_giris = findViewById(R.id.buttonGirisYap);
        kayitadon = findViewById(R.id.giristen_kayida);
        sifregoster = findViewById(R.id.checkbox_sifregoster);
        mAuth = FirebaseAuth.getInstance();

        sifregoster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sifreEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else{
                    sifreEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btn_giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = emailEt.getText().toString();
                String sifre = sifreEt.getText().toString();

                if(!mail.isEmpty() && !sifre.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(mail,sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendtoMain();
                            }else{
                                Toast.makeText(LoginActivity.this,"Kullanıcı bulunamadı."+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this,"Lütfen tüm alanları doldurduğunuzdan emin olunuz!",Toast.LENGTH_LONG).show();
                }
            }
        });
        kayitadon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giris = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(giris);
            }
        });
    }
    private void sendtoMain(){
        Intent don = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(don);
        finish();
    }
    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser kullanici =FirebaseAuth.getInstance().getCurrentUser();
        if(kullanici != null){
            Intent gir = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(gir);
            finish();
        }
    }
}