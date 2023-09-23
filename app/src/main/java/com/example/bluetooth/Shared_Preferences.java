package com.example.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Shared_Preferences {
    private String PREFERENCES_NAME = "PREFERENCES_SAVED_LOGGER";
    private int LINE_THICKNESS = 2;
    private int MIN_Y = 2;
    private int MAX_Y = 2;
    private int GRAPH_COLOR = 8;
    private int LINE_COLOR = 0;
    private int GRAPH_FREQ = 10;
    private Context context;

    Shared_Preferences(Context context) {
        this.context = context;
    }

    void updateValue(String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    void updateValue(String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    int getInt(String key, int defaultValue) {
        Log.d("defaultValue", defaultValue + "");
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            return preferences.getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    void clearStrings() {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < getInt("last", 0); i++) {
            editor.remove((i + 1) + "");
        }
        try {
            editor.remove("last");
        } catch (Exception e) {}
        editor.apply();
    }

    String getString(String key, String defaultValue) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            return preferences.getString(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    boolean checkAvailable(int i) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return !preferences.getString(i + "", "nothing...").equals("nothing...");
    }

    int getColorID(String colorName) {
        colorName = colorName.toLowerCase();
        switch (colorName) {
            case "white":
                return R.color.white;
            case "black":
                return R.color.black;
            case "red":
                return R.color.red;
            case "orange":
                return R.color.orange;
            case "yellow":
                return R.color.yellow;
            case "green":
                return R.color.green;
            case "violet":
                return R.color.violet;
            case "grey":
                return R.color.grey;
            default:
                return R.color.brown;
        }
    }

    public String getPREFERENCES_NAME() {
        return PREFERENCES_NAME;
    }

    public int getLINE_THICKNESS() {
        return LINE_THICKNESS;
    }

    public int getMIN_Y() {
        return MIN_Y;
    }

    public int getMAX_Y() {
        return MAX_Y;
    }

    public int getGRAPH_COLOR() {
        return GRAPH_COLOR;
    }

    public int getLINE_COLOR() {
        return LINE_COLOR;
    }

    public int getGRAPH_FREQ() {
        return GRAPH_FREQ;
    }

    public Context getContext() {
        return context;
    }

}
