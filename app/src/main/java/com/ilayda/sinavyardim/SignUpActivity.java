package com.ilayda.sinavyardim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static final String email_regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    EditText emailEt, sifreEt, kullaniciadEt, sifretekrarEt;
    Button btn_kayit;
    TextView girisdon;
    CheckBox sifregoster;
    Spinner sinav_secimi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEt = findViewById(R.id.editTextMail);
        sifreEt = findViewById(R.id.editTextSifre);
        kullaniciadEt = findViewById(R.id.editTextKulAdi);
        sifretekrarEt = findViewById(R.id.editTextSifreOnay);
        btn_kayit = findViewById(R.id.buttonKayitOl);
        girisdon = findViewById(R.id.textViewGirisDon);
        sifregoster = findViewById(R.id.checkbox_sifregoster);
        sinav_secimi =  findViewById(R.id.sinavsecim);

        ArrayAdapter<CharSequence> sinav_adapter = ArrayAdapter.createFromResource(this,R.array.sinavlar,android.R.layout.simple_spinner_item);
        sinav_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sinav_secimi.setAdapter(sinav_adapter);


        btn_kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String sifre = sifreEt.getText().toString();
                String kullaniciad = kullaniciadEt.getText().toString();
                String sifretekrar = sifretekrarEt.getText().toString();
                Object secili_sinav = sinav_secimi.getSelectedItem();
                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();

                if (!email.isEmpty() && !kullaniciad.isEmpty() && !sifre.isEmpty() && !sifretekrar.isEmpty() && !kullaniciad.equals(" ") && email.matches(email_regex) && secili_sinav != null && !secili_sinav.equals(sinav_secimi.getItemAtPosition(0)) && sifre.length() >= 6) {
                    if (sifre.equals(sifretekrar)) {
                        mAuth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser kullanici = mAuth.getCurrentUser();
                                    if(kullanici != null)
                                        kullaniciGuncelle(kullanici, kullaniciad, email, secili_sinav.toString());
                                    else {
                                        Toast.makeText(SignUpActivity.this, "Bu mail hesabıyla bir kullanıcı bulunmakta!\n Giriş sayfasına yönlendiriliyorsunuz.",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    }
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Şifre uyuşmazlığı!\nŞifreleri kontrol ediniz!", Toast.LENGTH_LONG).show();
                    }
                } else if(!email.matches(email_regex)){
                    emailEt.setError("abc@abc.abc şeklinde geçerli bir mail hesabı giriniz!");
                } else if(sifre.length() < 6 || sifretekrar.length() < 6){
                    sifreEt.setError("Şifre uzunluğu 6 karakterden fazla olmalıdır!");
                } else if(secili_sinav  == null || secili_sinav.equals(sinav_secimi.getItemAtPosition(0))){
                    Toast.makeText(SignUpActivity.this, "Lütfen bir sınav seçiniz!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Lütfen tüm alanları doldurduğunuzdan emin olunuz!", Toast.LENGTH_LONG).show();
                }
            }
        });
        girisdon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        sifregoster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sifretekrarEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    sifreEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else{
                    sifreEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    sifretekrarEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
    private void sendtoMain() {
        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void kullaniciGuncelle(FirebaseUser kul, String isim, String mail, String sinavsec) {
        Map<String, Object> map = new HashMap<>();

        map.put("kullaniciAdi", isim);
        map.put("mail", mail);
        map.put("sinavSecimi", sinavsec);
        map.put("urlFoto", " ");
        map.put("uid", kul.getUid());

        FirebaseFirestore.getInstance().collection("Kullanicilar").document(kul.getUid())
                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,"Kayıt başarılı...",Toast.LENGTH_SHORT).show();
                            sendtoMain();
                        } else {
                            Toast.makeText(SignUpActivity.this,"Hata: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();
        if(kullanici != null) {
            sendtoMain();
        }
    }
}