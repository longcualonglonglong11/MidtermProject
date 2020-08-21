package com.apcs2.midtermmoblie;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class Landmark {
    private String _name;
    private String _description;
    private LatLng _latLng;
    private String _phone;
    private int _emergencyLevel;//1 la bt , 2 la medium, 3 la high
    // Use for direct
    private ArrayList<Polyline> directPolylines;

    public void setLatLng(LatLng latLng) {
        this._latLng = latLng;
    }

    public LatLng getLatLng() {
        return _latLng;
    }

    public Landmark(String name, String description, String phone, LatLng latLng, int emergencyLevel, ArrayList<Polyline> directPolylines) {
        this._name = name;
        this._description = description;
        this._phone = phone;
        this._latLng = latLng;
        this._emergencyLevel = emergencyLevel;
        this.directPolylines = directPolylines;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public int get_emergencyLevel() {
        return _emergencyLevel;
    }

    public void set_emergencyLevel(int _emergencyLevel) {
        this._emergencyLevel = _emergencyLevel;
    }

    public ArrayList<Polyline> getPolylines() {
        return directPolylines;
    }

    public void setPolylines(ArrayList<Polyline> polylines) {
        this.directPolylines = polylines;
    }
}
