package fr.insset.ccm.m1.sag.travelogue.fragment;

import static android.text.InputType.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.HomeActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.MainActivity;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        ((HomeActivity) requireActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.relativelayout, new SettingsFragment());
            tr.commit();
        });
        SwitchPreference autoGps = findPreference("switch_enable_auto_gps");
        assert autoGps != null;
        autoGps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                Log.d("test", ""+newValue);
                return true;
            }
        });

        EditTextPreference waitTime = findPreference("edittext_time_between_auto");
        assert waitTime != null;
        waitTime.setOnBindEditTextListener(editText -> editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED));


        Preference logOut = findPreference("log_out");
        assert logOut != null;
        logOut.setOnPreferenceClickListener(preference -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            return true;
        });
    }


}