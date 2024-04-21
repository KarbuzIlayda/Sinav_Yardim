package com.ilayda.sinavyardim;

public class YorumBilgileri {
    private String postkey, yorumkey, yorumcuid, yorumcuisim, yorumcupp, yorumyazi, yorumfoto;
    private Integer yorumsayisi, begenisayisi;
    public YorumBilgileri(){

    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public void setBegenisayisi(Integer begenisayisi) {
        this.begenisayisi = begenisayisi;
    }

    public void setYorumsayisi(Integer yorumsayisi) {
        this.yorumsayisi = yorumsayisi;
    }

    public void setYorumcuid(String yorumcuid) {
        this.yorumcuid = yorumcuid;
    }

    public void setYorumcuisim(String yorumcuisim) {
        this.yorumcuisim = yorumcuisim;
    }

    public void setYorumcupp(String yorumcupp) {
        this.yorumcupp = yorumcupp;
    }

    public void setYorumfoto(String yorumfoto) {
        this.yorumfoto = yorumfoto;
    }

    public void setYorumkey(String yorumkey) {
        this.yorumkey = yorumkey;
    }

    public void setYorumyazi(String yorumyazi) {
        this.yorumyazi = yorumyazi;
    }

    public String getPostkey() {
        return postkey;
    }

    public Integer getBegenisayisi() {
        return begenisayisi;
    }

    public Integer getYorumsayisi() {
        return yorumsayisi;
    }

    public String getYorumcuid() {
        return yorumcuid;
    }

    public String getYorumcuisim() {
        return yorumcuisim;
    }

    public String getYorumcupp() {
        return yorumcupp;
    }

    public String getYorumfoto() {
        return yorumfoto;
    }

    public String getYorumkey() {
        return yorumkey;
    }

    public String getYorumyazi() {
        return yorumyazi;
    }
}
