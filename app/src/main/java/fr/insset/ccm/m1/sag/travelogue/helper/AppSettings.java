package fr.insset.ccm.m1.sag.travelogue.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.SharedPreferencesCompat;

public class AppSettings {
    private static SharedPreferences sharedPreferences = null;

    public static void setup(Context context) {
        sharedPreferences = context.getSharedPreferences("travelogue.sharedprefs", Context.MODE_PRIVATE);
    }

    public static Boolean getTravelling() {
        return Key.Travelling.getBoolean();
    }

    public static void setTravelling(Boolean value) {
        Key.Travelling.setBoolean(value);

    }

    public static String getTravel() {
        return Key.Travel.getString();
    }

    public static void setTravel(String value) {
        Key.Travel.setString(value);

    }

    public static Boolean getAutoGps() {
        return Key.AutoGps.getBoolean();
    }

    public static void setAutoGps(Boolean value) {
        Key.AutoGps.setBoolean(value);

    }

    public static Long getTimeBetweenAutoGps() {
        return Key.TimeBetweenAutoGps.getLong();
    }

    public static void setTimeBetweenAutoGps(Long value) {
        Key.TimeBetweenAutoGps.setLong(value);
    }

    private enum Key {
        Travelling("travelling"), Travel("travel"), TimeBetweenAutoGps("timeBetweenAutoGps"), AutoGps("autoGps");

        private final String name;

        Key(String name) {
            this.name = name;
        }

        public Boolean getBoolean() {
            return sharedPreferences.contains(name) ? sharedPreferences.getBoolean(name, false) : null;
        }

        public void setBoolean(Boolean value) {
            if (value != null) {
                SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences.edit().putBoolean(name, value));
            } else {
                remove();
            }
        }

        public Float getFloat() {
            return sharedPreferences.contains(name) ? sharedPreferences.getFloat(name, 0f) : null;
        }

        public void setFloat(Float value) {
            if (value != null) {
                SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences.edit().putFloat(name, value));
            } else {
                remove();
            }
        }

        public Integer getInt() {
            return sharedPreferences.contains(name) ? sharedPreferences.getInt(name, 0) : null;
        }

        public void setInt(Integer value) {
            if (value != null) {
                SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences.edit().putInt(name, value));
            } else {
                remove();
            }
        }

        public Long getLong() {
            return sharedPreferences.contains(name) ? sharedPreferences.getLong(name, 0) : null;
        }

        public void setLong(Long value) {
            if (value != null) {
                SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences.edit().putLong(name, value));
            } else {
                remove();
            }
        }

        public String getString() {
            return sharedPreferences.contains(name) ? sharedPreferences.getString(name, "") : null;
        }

        public void setString(String value) {
            if (value != null) {
                SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences.edit().putString(name, value));
            } else {
                remove();
            }
        }

        public void remove() {
            SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences.edit().remove(name));
        }
    }
}
