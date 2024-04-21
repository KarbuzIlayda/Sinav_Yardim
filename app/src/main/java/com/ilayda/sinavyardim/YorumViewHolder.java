package com.ilayda.sinavyardim;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class YorumViewHolder extends RecyclerView.ViewHolder {
    ImageView yorumcufoto, yorumunfoto;
    TextView yorumcuad, yorumaciklama, yorumbegenisayi, yorumsayi, yorumtarih;
    ImageButton yorumopt, yorumyap, yorumbegen;
    Integer begenisayi, yorumsayac;
    String yorumid;
    FirebaseDatabase veritabani = FirebaseDatabase.getInstance();
    DatabaseReference vtreferans;
    public YorumViewHolder(@NonNull View itemView) {
        super(itemView);

    }

    public void setYorum(Application application, String postkey, String yorumkey, String yorumcuid, String yorumcuisim, String yorumcupp
                , String yorumyazi, String yorumfoto, Integer yorumsayisi) {

        yorumcufoto = itemView.findViewById(R.id.yorumcufoto);
        yorumunfoto = itemView.findViewById(R.id.yorumfoto);
        yorumcuad = itemView.findViewById(R.id.yorumcuad);
        yorumaciklama = itemView.findViewById(R.id.yorumaciklama);
        yorumbegenisayi = itemView.findViewById(R.id.yorumbegensayi);
        yorumsayi = itemView.findViewById(R.id.yorumsayi);
        yorumtarih = itemView.findViewById(R.id.yorumtarih);
        yorumopt = itemView.findViewById(R.id.yorum_opt);
        yorumbegen = itemView.findViewById(R.id.yorumbegen);
        yorumyap = itemView.findViewById(R.id.yorumyaz);

        yorumcuad.setText(yorumcuisim);
        if(!TextUtils.equals(yorumyazi, " ") || !TextUtils.isEmpty(yorumyazi)) {
            yorumaciklama.setText(yorumyazi);
            yorumaciklama.setVisibility(View.VISIBLE);
        }
        else
            yorumaciklama.setVisibility(View.INVISIBLE);

        yorumid = yorumkey;
        yorumsayac = yorumsayisi;

        yorumsayi.setText(String.valueOf(yorumsayisi));
        Picasso.get().load(yorumcupp).into(yorumcufoto);

        if(!TextUtils.isEmpty(yorumfoto) || !TextUtils.equals(yorumfoto,"")){
            Picasso.get().load(yorumfoto).into(yorumunfoto);
            yorumunfoto.setVisibility(View.VISIBLE);
        } else {
            yorumunfoto.setVisibility(View.INVISIBLE);
        }

    }
}
