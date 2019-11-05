package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.model.VisitorByChapterModel;
import com.mezyapps.bni_visitor.model.VisitorHistoryModel;

import java.util.ArrayList;

public class VisitorHistoryAdapter extends RecyclerView.Adapter<VisitorHistoryAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<VisitorHistoryModel> visitorHistoryModelArrayList;
    private  ArrayList<VisitorHistoryModel> arrayListFiltered;

    public VisitorHistoryAdapter(Context mContext,ArrayList<VisitorHistoryModel> visitorHistoryModelArrayList) {
        this.mContext = mContext;
        this.visitorHistoryModelArrayList = visitorHistoryModelArrayList;
        this.arrayListFiltered = visitorHistoryModelArrayList;
    }

    @NonNull
    @Override
    public VisitorHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history_list,parent,false);
        return new VisitorHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorHistoryAdapter.MyViewHolder holder, int position) {
        final  VisitorHistoryModel visitorHistoryModel=visitorHistoryModelArrayList.get(position);
        holder.textName.setText(visitorHistoryModel.getName());
        holder.textLaunch_dc.setText(visitorHistoryModel.getLaunch_dc());
        holder.textFollowUpDateTime.setText(visitorHistoryModel.getFollow_up_date_time());
        holder.textRemark.setText(visitorHistoryModel.getRemark());

    }

    @Override
    public int getItemCount() {
        return visitorHistoryModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName,textLaunch_dc,textFollowUpDateTime,textRemark;
        private CardView card_view_list;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textName=itemView.findViewById(R.id.textName);
            textLaunch_dc=itemView.findViewById(R.id.textLaunch_dc);
            card_view_list=itemView.findViewById(R.id.card_view_list);
            textFollowUpDateTime=itemView.findViewById(R.id.textFollowUpDateTime);
            textRemark=itemView.findViewById(R.id.textRemark);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s","").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    visitorHistoryModelArrayList = arrayListFiltered;
                } else {
                    ArrayList<VisitorHistoryModel> filteredList = new ArrayList<>();
                    for (int i = 0; i < visitorHistoryModelArrayList.size(); i++) {
                        String date=visitorHistoryModelArrayList.get(i).getFollow_up_date_time().replaceAll("\\s","").toLowerCase().trim();
                        if ((date.contains(charString))) {
                            filteredList.add(visitorHistoryModelArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        visitorHistoryModelArrayList = filteredList;
                    } else {
                        visitorHistoryModelArrayList = arrayListFiltered;
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = visitorHistoryModelArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                visitorHistoryModelArrayList = (ArrayList<VisitorHistoryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
