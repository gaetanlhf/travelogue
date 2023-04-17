package fr.insset.ccm.m1.sag.travelogue.fragment;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.HomeActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.MainActivity;
import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;
import fr.insset.ccm.m1.sag.travelogue.helper.MaterialEditTextPreference;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class SettingsFragment extends PreferenceFragmentCompat {

    private FirebaseAuth mAuth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mAuth = FirebaseAuth.getInstance();
        Settings settings = new Settings(mAuth.getCurrentUser().getUid());

        ((HomeActivity) requireActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.relativelayout, new SettingsFragment());
            tr.commit();
        });

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(requireActivity());

        PreferenceCategory localisationCat = new PreferenceCategory(requireActivity());
        localisationCat.setTitle("Localisation");
        screen.addPreference(localisationCat);

        SwitchPreferenceCompat switchAutoGps = new SwitchPreferenceCompat(requireActivity());
        switchAutoGps.setTitle("Enable auto getting GPS point");
        switchAutoGps.setWidgetLayoutResource(R.layout.preference_widget_material_switch);
        switchAutoGps.setKey("switch_enable_auto_gps");
        switchAutoGps.setChecked(AppSettings.getAutoGps());
        switchAutoGps.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.setAutoGps(Boolean.parseBoolean(newValue.toString()));
            if (LocationService.isServiceRunning && !Boolean.parseBoolean(newValue.toString())) {
                //TODO SERVICE SHUTDOWN
            }
            if (!LocationService.isServiceRunning && AppSettings.getTravelling() && Boolean.parseBoolean(newValue.toString())) {
                //TODO SERVICE ACTIVATION
            }
            return true;
        });
        localisationCat.addPreference(switchAutoGps);

        EditTextPreference timeBetweenAutoGps = new EditTextPreference(requireActivity());
        timeBetweenAutoGps.setTitle("Time between each");
        timeBetweenAutoGps.setDialogTitle("Time between each");
        timeBetweenAutoGps.setOnBindEditTextListener(editText -> editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED));
        timeBetweenAutoGps.setKey("edittext_time_between_auto");
        timeBetweenAutoGps.setText(AppSettings.getTimeBetweenAutoGps().toString());
        timeBetweenAutoGps.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.setTimeBetweenAutoGps(Integer.parseInt(newValue.toString()));
            return true;
        });
        screen.addPreference(timeBetweenAutoGps);

        PreferenceCategory accountCat = new PreferenceCategory(requireActivity());
        accountCat.setTitle("Account");
        screen.addPreference(accountCat);

        Preference logOut = new Preference(requireActivity());
        logOut.setTitle("Logout");
        logOut.setSummary("Click here to log out from your account");
        logOut.setOnPreferenceClickListener(preference -> {
            mAuth.signOut();
            requireActivity().finish();
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            return true;
        });
        screen.addPreference(logOut);

        setPreferenceScreen(screen);
        timeBetweenAutoGps.setDependency(switchAutoGps.getKey());
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof EditTextPreference) {
            showEditTextPreferenceDialog((EditTextPreference) preference);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    private void showEditTextPreferenceDialog(EditTextPreference preference) {
        DialogFragment dialogFragment = new MaterialEditTextPreference();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getParentFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
    }


}