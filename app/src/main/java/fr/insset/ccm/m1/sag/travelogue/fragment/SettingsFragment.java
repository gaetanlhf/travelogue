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

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.HomeActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.MainActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.NoConnection;
import fr.insset.ccm.m1.sag.travelogue.helper.MaterialEditTextPreference;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class SettingsFragment extends PreferenceFragmentCompat {

    private FirebaseAuth mAuth;
    private SharedPrefManager sharedPrefManager;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mAuth = FirebaseAuth.getInstance();
        Settings settings = new Settings(mAuth.getCurrentUser().getUid());
        sharedPrefManager = SharedPrefManager.getInstance(requireActivity());
        ((HomeActivity) requireActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.home_activity_relative_layout, new SettingsFragment());
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
        switchAutoGps.setChecked(sharedPrefManager.getBool("AutoGps"));
        switchAutoGps.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.setAutoGps(requireContext(), Boolean.parseBoolean(newValue.toString()));
            if (LocationService.isServiceRunning && !Boolean.parseBoolean(newValue.toString())) {
                Intent intent = new Intent(getContext(), LocationService.class);
                intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
                requireContext().startService(intent);
            }
            if (!LocationService.isServiceRunning && sharedPrefManager.getBool("Travelling") && Boolean.parseBoolean(newValue.toString())) {
                if (!PermissionsHelper.hasPermission(requireContext(), Constants.ACCESS_FINE_LOCATION_PERMISSION)) {
                    PermissionsHelper.requestPermissions(this, new String[]{Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
                }
                Intent intent = new Intent(requireContext(), LocationService.class);
                intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                intent.putExtra("timeBetweenUpdate", sharedPrefManager.getLong("TimeBetweenAutoGps"));
                requireContext().startService(intent);
            }
            return true;
        });
        localisationCat.addPreference(switchAutoGps);

        EditTextPreference timeBetweenAutoGps = new EditTextPreference(requireActivity());
        timeBetweenAutoGps.setTitle("Time between each");
        timeBetweenAutoGps.setDialogTitle("Time between each");
        timeBetweenAutoGps.setOnBindEditTextListener(editText -> editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED));
        timeBetweenAutoGps.setKey("edittext_time_between_auto");
        timeBetweenAutoGps.setText(String.valueOf(sharedPrefManager.getLong("TimeBetweenAutoGps")));
        timeBetweenAutoGps.setOnPreferenceChangeListener((preference, newValue) -> {
            if (!newValue.toString().equals(String.valueOf(sharedPrefManager.getLong("TimeBetweenAutoGps")))) {
                settings.setTimeBetweenAutoGps(requireContext(), Long.parseLong(newValue.toString()));
                sharedPrefManager.updateLong("TimeBetweenAutoGps", Long.parseLong(newValue.toString()));
                Intent intentStop = new Intent(getContext(), LocationService.class);
                intentStop.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
                requireContext().startService(intentStop);
                if (!PermissionsHelper.hasPermission(requireContext(), Constants.ACCESS_FINE_LOCATION_PERMISSION)) {
                    PermissionsHelper.requestPermissions(this, new String[]{Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
                }
                Intent intentStart = new Intent(requireContext(), LocationService.class);
                intentStart.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                intentStart.putExtra("timeBetweenUpdate", sharedPrefManager.getLong("TimeBetweenAutoGps"));
                requireContext().startService(intentStart);
            }
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