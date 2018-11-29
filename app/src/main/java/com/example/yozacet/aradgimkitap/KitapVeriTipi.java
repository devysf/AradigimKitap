package com.example.yozacet.aradgimkitap;

/**
 * Created by yozacet on 27.01.2018.
 */
public class KitapVeriTipi {

    private String kitapIsmi;
    private String yazarIsmi;
    private String photoUrl;
    private String kAdi;

    public KitapVeriTipi() {

    }

    public KitapVeriTipi(String text, String name,String kAdi, String photoUrl) {
        this.kitapIsmi = text;
        this.yazarIsmi = name;
        this.kAdi = kAdi;
        this.photoUrl = photoUrl;
    }

    public String getKitapIsmi() {
        return kitapIsmi;
    }

    public void setKitapIsmi(String text) {
        this.kitapIsmi = text;
    }

    public String getYazarIsmi() {
        return yazarIsmi;
    }

    public void setYazarIsmi(String name) {
        this.yazarIsmi = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getkAdi() {return kAdi;}

    public void setkAdi(String kAdi) {this.kAdi = kAdi;}
}
