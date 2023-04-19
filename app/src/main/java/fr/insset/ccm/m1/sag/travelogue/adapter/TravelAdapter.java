package fr.insset.ccm.m1.sag.travelogue.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.TravelActivity;

public class TravelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private List<String> titles;

    public TravelAdapter(Context context, List<String> titles)
    {
        this.context = context;
        this.titles = titles;
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

        itemVH.travelNameTextView.setText(titles.get(position));
        itemVH.viewTravelbtn.setText("View");


        itemVH.viewTravelbtn.setOnClickListener(v -> {
            String travelName = itemVH.travelNameTextView.getText().toString();
            Intent intent = new Intent(context.getApplicationContext(), TravelActivity.class);
            intent.putExtra("travelName", travelName);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void addItem(String title) {
        titles.add(title);
        notifyItemInserted(titles.size() - 1);
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
        private final Button viewTravelbtn;

        public ItemVH(View itemView) {
            super(itemView);

            travelNameTextView = itemView.findViewById(R.id.travel_name_textView);
            viewTravelbtn = itemView.findViewById(R.id.view_travel_btn);
        }
    }
}