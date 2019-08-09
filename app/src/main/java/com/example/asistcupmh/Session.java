package com.example.asistcupmh;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setid(String id) {
        prefs.edit().putString("id", id).commit();
    }

    public String getid() {
        String id = prefs.getString("id","");
        return id;
    }

    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
    }

    public String getusename() {
        String usename = prefs.getString("usename","");
        return usename;
    }
    public void setfoto(String foto) {
        prefs.edit().putString("foto", foto).commit();
    }

    public String getfoto() {
        String foto = prefs.getString("foto","");
        return foto;
    }

    public void dropAll(Context cntx){
        String PREF_FILE_NAME = PreferenceManager.getDefaultSharedPreferencesName(cntx);
        //System.out.println("PREFERENCESMANAGER NAME= "+ PREF_FILE_NAME);
        SharedPreferences settings = cntx.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }
}