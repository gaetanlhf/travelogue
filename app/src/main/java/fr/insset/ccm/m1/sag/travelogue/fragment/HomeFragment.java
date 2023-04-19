package fr.insset.ccm.m1.sag.travelogue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.HomeActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.NewTravelActivity;
import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        spinner = view.findViewById(R.id.fragment_home_spinner);
        spinner.setVisibility(View.VISIBLE);
        State state = new State(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        Button newTravelBtn = view.findViewById(R.id.start_new_travel_btn);
        Button stopTravelBtn = view.findViewById(R.id.stop_travel_btn);

        TextView noCurrentTravel = view.findViewById(R.id.no_travel_home_textview);
        newTravelBtn.setVisibility(View.GONE);
        noCurrentTravel.setVisibility(View.GONE);
        stopTravelBtn.setVisibility(View.GONE);

        state.isTravelling(travelling -> {
            if (travelling.get()) {
                AppSettings.setTravelling(travelling.get());
                //TODO SERVICE ACTIVATION
                spinner.setVisibility(View.GONE);

                stopTravelBtn.setVisibility(View.VISIBLE);
                stopTravelBtn.setOnClickListener(v -> {
                    stopLocationService();
                    state.setTravelling(false);
                    AppSettings.setTravelling(!travelling.get());
                    Toast.makeText(getContext(), "isTravelling set to false", Toast.LENGTH_SHORT).show();
                    newTravelBtn.setVisibility(View.VISIBLE);
                    noCurrentTravel.setVisibility(View.VISIBLE);
                    stopTravelBtn.setVisibility(View.GONE);
                    Log.d("TEST", "ok");
                    AppSettings.setTravelling(false);
                });

            } else {
                spinner.setVisibility(View.GONE);
                newTravelBtn.setVisibility(View.VISIBLE);
                noCurrentTravel.setVisibility(View.VISIBLE);

            }
        });
        newTravelBtn.setOnClickListener(v -> {
            Intent newTravelActivity = new Intent(getActivity(), NewTravelActivity.class);
            startActivity(newTravelActivity);
        });

        ((HomeActivity) getActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.relativelayout, new HomeFragment());
            tr.commit();
        });
        return view;
    }

    private void stopLocationService() {
        if (LocationService.isServiceRunning) {
            Intent intent = new Intent(getContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
            Toast.makeText(getContext(), "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
}