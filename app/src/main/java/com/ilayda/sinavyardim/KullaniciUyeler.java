package com.ilayda.sinavyardim;

public class KullaniciUyeler {
    private String isim, uid, kuladi, bio, url;

    public KullaniciUyeler(){

    }
    public String getUrl() {
        return url;
    }
    public String getBio() {
        return bio;
    }
    public String getIsim() {
        return isim;
    }
    public String getKuladi() {
        return kuladi;
    }
    public String getUid() {
        return uid;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setIsim(String isim) {
        this.isim = isim;
    }
    public void setKuladi(String kuladi) {
        this.kuladi = kuladi;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
}
