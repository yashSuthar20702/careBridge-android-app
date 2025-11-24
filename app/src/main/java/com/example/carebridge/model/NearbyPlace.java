package com.example.carebridge.model;


import com.google.android.gms.maps.model.LatLng;

public class NearbyPlace {
    public String name;
    public LatLng latLng;
    public String distance;
    public String address; // added field

    public NearbyPlace(String name, LatLng latLng, String distance, String address) {
        this.name = name;
        this.latLng = latLng;
        this.distance = distance;
        this.address = address;
    }
}
