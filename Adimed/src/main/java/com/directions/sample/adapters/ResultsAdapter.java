package com.directions.sample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.directions.sample.R;
import com.directions.sample.volley.RouteResult;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private static final String TAG ="ResultsAdapter" ;
    private List<RouteResult> routeResultsItemList;
    private Context mContext;

    public ResultsAdapter(Context context, List<RouteResult> routeResultItemList) {
        this.routeResultsItemList = routeResultItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder customViewHolder, int i) {
        RouteResult routeItem = routeResultsItemList.get(i);
        Log.i(TAG, "listAdapter" + routeResultsItemList.get(0).getFareCost());

        //Setting text view title
        customViewHolder.tvFareCost.setText("Całkowity koszt: " + routeItem.getFareCost());
        customViewHolder.tvDistance.setText("Szacowany dystans: " + routeItem.getDistanceText());
        customViewHolder.tvTime.setText("Szacowany czas przejazdu: " + routeItem.getDurationText());

    }

    @Override
    public int getItemCount() {
        return (null != routeResultsItemList ? routeResultsItemList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvFareCost, tvDistance, tvTime;

        public ViewHolder(View view) {
            super(view);
            this.tvFareCost = (TextView) view.findViewById(R.id.tvFareCost);
            this.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            this.tvTime = (TextView) view.findViewById(R.id.tvTime);
        }
    }
}