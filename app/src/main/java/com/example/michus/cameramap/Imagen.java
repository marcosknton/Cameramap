package com.example.michus.cameramap;

import java.io.Serializable;

/**
 * Created by 46453895j on 17/02/17.
 */

public class Imagen implements Serializable {

    private String rutaimagen;
    private Double latitude;
    private Double longitude;
    private String adress;

    public Imagen(){}


    public Imagen(String rutaimagen, Double latitude, Double longitude, String adress) {

        this.rutaimagen = rutaimagen;
        this.latitude=latitude;
        this.longitude=longitude;
        this.adress=adress;

    }


    public String fecha_hora(String ruta){
        String sfecha_hora="";
        String[] afecha_hora = ruta.split("_");
        sfecha_hora=afecha_hora[1];
        return sfecha_hora;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAdress() {
        return adress;
    }

    public String getRutaimagen() {
        return rutaimagen;
    }

    public void setRutaimagen(String rutaimagen) {
        this.rutaimagen = rutaimagen;
    }
}
