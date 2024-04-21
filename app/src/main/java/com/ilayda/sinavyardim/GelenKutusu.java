package com.ilayda.sinavyardim;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GelenKutusu {
    String gonderenid, gonderenisim, aliciid, mesaj, gonderilmetarih, gonderilmesaat, gorulmesaat, gorulmetarih;

    public void setGonderenisim(String gonderenisim) {
        this.gonderenisim = gonderenisim;
    }

    public void setGorulmesaat(String gorulmesaat) {
        this.gorulmesaat = gorulmesaat;
    }

    public void setGorulmetarih(String gorulmetarih) {
        this.gorulmetarih = gorulmetarih;
    }
    public void setGonderenid(String gonderenid) {
        this.gonderenid = gonderenid;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public void setAliciid(String aliciid) {
        this.aliciid = aliciid;
    }

    public void setGonderilmesaat(String gonderilmesaat) {
        this.gonderilmesaat = gonderilmesaat;
    }

    public void setGonderilmetarih(String gonderilmetarih) {
        this.gonderilmetarih = gonderilmetarih;
    }

    public String getGonderenisim() {
        return gonderenisim;
    }

    public String getGonderenid() {
        return gonderenid;
    }

    public String getAliciid() {
        return aliciid;
    }

    public String getMesaj() {
        return mesaj;
    }

    public String getGonderilmesaat() {
        return gonderilmesaat;
    }

    public String getGonderilmetarih() {
        return gonderilmetarih;
    }

    public String getGorulmetarih() {
        return gorulmetarih;
    }

    public String getGorulmesaat() {
        return gorulmesaat;
    }
}
