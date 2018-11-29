package com.example.yozacet.aradgimkitap;

/**
 * Created by yozacet on 29.01.2018.
 */

public class MesajVeriTipi {

    private String gonderen;
    private String alici;
    private String icerik;

    public MesajVeriTipi() {

    }

    public MesajVeriTipi(String gonderen, String alici,String icerik) {
        this.gonderen = gonderen;
        this.alici = alici;
        this.icerik = icerik;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getAlici() {
        return alici;
    }

    public void setAlici(String alici) {
        this.alici = alici;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }
}
