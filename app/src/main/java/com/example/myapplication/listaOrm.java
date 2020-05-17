package com.example.myapplication;

import java.sql.Timestamp;

public class listaOrm {

    private Timestamp   fecha;
    private String      lugar;
    private String      gps;
    private String      imei;

    public listaOrm() {
        super();
    }

    public listaOrm(Timestamp fecha, String lugar, String gps, String imei) {
        this.fecha = fecha;
        this.lugar = lugar;
        this.gps = gps;
        this.imei = imei;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
