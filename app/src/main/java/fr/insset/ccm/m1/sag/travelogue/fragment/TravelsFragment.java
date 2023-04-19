package fr.insset.ccm.m1.sag.travelogue.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.HomeActivity;
import fr.insset.ccm.m1.sag.travelogue.adapter.TravelAdapter;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TravelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;

    private ProgressBar spinner;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TravelsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TravelsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TravelsFragment newInstance(String param1, String param2) {
        TravelsFragment fragment = new TravelsFragment();
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
        View view = inflater.inflate(R.layout.fragment_travels, container, false);
        View noTripContent = view.findViewById(R.id.fragment_travels_no_content);
        View tripContent = view.findViewById(R.id.fragment_travels_content);
        noTripContent.setVisibility(View.GONE);
        tripContent.setVisibility(View.GONE);
        spinner = view.findViewById(R.id.fragment_home_spinner);
        // create list:
        List<String> titles = new ArrayList<>();
        spinner.setVisibility(View.VISIBLE);
        if (isAdded()) {
            TravelHelper travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());
            travelHelper.getTravels(data -> {
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        titles.add(String.valueOf(data.get(i).getTitle()));
                    }
                    TravelAdapter adapter = new TravelAdapter(requireActivity(), titles);

                    // set the RecyclerView:
                    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
                    spinner.setVisibility(View.GONE);
                    tripContent.setVisibility(View.VISIBLE);
                } else {
                    spinner.setVisibility(View.GONE);
                    noTripContent.setVisibility(View.VISIBLE);
                }


            });
        }


        ((HomeActivity) getActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.relativelayout, new TravelsFragment());
            tr.commit();
        });

        // define the adapter:

        return view;
    }
}