package fr.insset.ccm.m1.sag.travelogue.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.TravelActivity;
import fr.insset.ccm.m1.sag.travelogue.helper.TimestampDate;

public class TravelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<String> initialTitles;
    Map<String, String> idToTravel = new HashMap<String, String>();
    Map<String, String> idToEndTimestamp = new HashMap<String, String>();
    Map<String, String> travelToId = new HashMap<String, String>();
    private List<String> ids;
    private List<String> titles;
    private List<String> endTimestamp;

    public TravelAdapter(Context context, List<String> ids, List<String> titles, List<String> endTimestamp) {
        this.context = context;
        this.initialTitles = new ArrayList<>(titles);
        this.ids = new ArrayList<>(ids);
        this.endTimestamp = new ArrayList<>(endTimestamp);

        for (int i = 0; i < ids.size(); i++) {
            idToTravel.put(ids.get(i), initialTitles.get(i));
            travelToId.put(initialTitles.get(i), ids.get(i));
            idToEndTimestamp.put(ids.get(i), endTimestamp.get(i));
        }

        Collections.reverse(this.ids);
        Collections.reverse(this.initialTitles);
        Collections.reverse(this.endTimestamp);


        this.titles = new ArrayList<>(this.initialTitles); // Maintenant, 'titles' est aussi inversée

    }

    public void filterList(String text) {
        List<String> filteredTitleslist = new ArrayList<>();
        List<String> filteredIdslist = new ArrayList<>();
        for (String item : initialTitles) {
            if (item.trim().toLowerCase().contains(text.trim().toLowerCase())) {
                filteredTitleslist.add(item);
                filteredIdslist.add(travelToId.get(item));
            }
        }
        if (!filteredTitleslist.isEmpty()) {
            ids = filteredIdslist;
            titles = filteredTitleslist;
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }

    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.travel_item, parent, false);
        viewHolder = new ItemVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemVH itemVH = (ItemVH) holder;

        itemVH.travelNameTextView.setText(idToTravel.get(ids.get(position)));
        itemVH.startTimeTextView.setText(itemVH.itemView.getResources().getString(R.string.from_date) + TimestampDate.getDate(ids.get(position)));
        itemVH.endTimeTextView.setText(itemVH.itemView.getResources().getString(R.string.to_date) + TimestampDate.getDate(idToEndTimestamp.get(ids.get(position))));

        itemVH.viewTravelbtn.setOnClickListener(v -> {
            String travelName = itemVH.travelNameTextView.getText().toString();
            Intent intent = new Intent(context.getApplicationContext(), TravelActivity.class);
            intent.putExtra("travelName", travelName);
            intent.putExtra("travelId", ids.get(position));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void remove(String title) {
        int position = titles.indexOf(title);
        if (position > -1) {
            titles.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected class ItemVH extends RecyclerView.ViewHolder {
        private final TextView travelNameTextView;
        private final TextView startTimeTextView;
        private final TextView endTimeTextView;
        private final Button viewTravelbtn;

        public ItemVH(View itemView) {
            super(itemView);

            travelNameTextView = itemView.findViewById(R.id.travel_item_name_text_view);
            startTimeTextView = itemView.findViewById(R.id.travel_item_start_date_text_view);
            endTimeTextView = itemView.findViewById(R.id.travel_item_end_date_text_view);
            viewTravelbtn = itemView.findViewById(R.id.travel_item_button);
        }
    }
}