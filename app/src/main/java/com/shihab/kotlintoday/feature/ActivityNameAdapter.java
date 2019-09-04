package com.shihab.kotlintoday.feature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.shihab.kotlintoday.R;

import java.util.List;

public class ActivityNameAdapter extends
        RecyclerView.Adapter<ActivityNameAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<String> requestList;
    private Context context;

    private OnButtonClickListener onButtonClickListener;

    interface OnButtonClickListener {
        void onButtonClick(int position);
    }

    public ActivityNameAdapter(Context context,
                               List<String> items,
                               OnButtonClickListener onButtonClickListener) {
        this.context = context;
        this.requestList = items;
        this.onButtonClickListener = onButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item_notifications, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String name = requestList.get(position);


        holder.name.setText("" + name);

        holder.contact_select_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickListener.onButtonClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        ConstraintLayout contact_select_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            contact_select_layout = itemView.findViewById(R.id.contact_select_layout);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
