package com.ilayda.sinavyardim;

public class Gonderi {
    private String uid, url, postUri, zaman, isim, aciklama, sinav, key;
    private int begenisayisi, yorumsayisi;
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }
    public void setIsim(String isim) {
        this.isim = isim;
    }
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
    public void setZaman(String zaman) {
        this.zaman = zaman;
    }
    public void setSinav(String sinav) {
        this.sinav = sinav;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setYorumsayisi(int yorumsayisi) {
        this.yorumsayisi = yorumsayisi;
    }
    public void setBegenisayisi(int begenisayisi) {
        this.begenisayisi = begenisayisi;
    }
    public String getUid() {
        return uid;
    }
    public String getIsim() {
        return isim;
    }
    public String getPostUri() {
        return postUri;
    }
    public String getAciklama() {
        return aciklama;
    }
    public String getSinav() {
        return sinav;
    }
    public String getUrl() {
        return url;
    }
    public String getZaman() {
        return zaman;
    }
    public String getKey() {
        return key;
    }
    public int getYorumsayisi() {
        return yorumsayisi;
    }
    public int getBegenisayisi() {
        return begenisayisi;
    }
}
