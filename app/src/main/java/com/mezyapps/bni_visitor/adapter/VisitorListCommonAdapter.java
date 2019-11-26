package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.EditVisitorActivity;
import com.mezyapps.bni_visitor.activity.EditVisitorDetailsActivity;
import com.mezyapps.bni_visitor.activity.HistoryActivity;
import com.mezyapps.bni_visitor.model.VisitorDateFilterModel;
import com.mezyapps.bni_visitor.model.VisitorListStatusModel;

import java.util.ArrayList;

public class VisitorListCommonAdapter extends RecyclerView.Adapter<VisitorListCommonAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<VisitorListStatusModel> visitorListStatusModelArrayList;
    private  ArrayList<VisitorListStatusModel> arrayListFiltered;

    public VisitorListCommonAdapter(Context mContext, ArrayList<VisitorListStatusModel> visitorListStatusModelArrayList) {
        this.mContext = mContext;
        this.visitorListStatusModelArrayList = visitorListStatusModelArrayList;
        this.arrayListFiltered = visitorListStatusModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_visitor_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final VisitorListStatusModel visitorListStatusModel = visitorListStatusModelArrayList.get(position);
        String status = visitorListStatusModel.getStatus();

        holder.textFollowUpDateTime.setText(visitorListStatusModel.getFollow_up_date_time());
        holder.textName.setText(visitorListStatusModel.getName());
        String mobile_number =visitorListStatusModel.getMobile_no().toString().replaceAll("\\s","").toLowerCase().trim();
        String lastTenDigits = "";

        if (mobile_number.length() > 10) {
            lastTenDigits = mobile_number.substring(mobile_number.length() - 10);
        } else {
            lastTenDigits = mobile_number;
        }
        holder.textMobileNumber.setText(lastTenDigits);
        holder.textEmail.setText(visitorListStatusModel.getEmail_id());
        holder.textChapterName.setText(visitorListStatusModel.getChapter_name());
        holder.textLaunch_dc.setText(visitorListStatusModel.getLaunch_dc());
        holder.textSource.setText(visitorListStatusModel.getSource());
        holder.textCategory.setText(visitorListStatusModel.getCategory());
        holder.textDescription.setText(visitorListStatusModel.getDescription());
        final String finalLastTenDigits = lastTenDigits;


        holder.iv_edit_follow_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditVisitorDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("VISITOR_ID",  visitorListStatusModelArrayList.get(position).getVisitor_id());
                intent.putExtra("VISITOR_NAME",  visitorListStatusModelArrayList.get(position).getName());
                intent.putExtra("VISITOR_STATUS",  visitorListStatusModelArrayList.get(position).getStatus());
                intent.putExtra("VISITOR_LAUNCH_DC",  visitorListStatusModelArrayList.get(position).getLaunch_dc());
                intent.putExtra("VISITOR_FOLLOW_DATE",  visitorListStatusModelArrayList.get(position).getFollow_up_date_time());
                intent.putExtra("FOLLOW_DATE",  visitorListStatusModelArrayList.get(position).getInserted_date_time());
                mContext.startActivity(intent);
            }
        });

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditVisitorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("VISITOR_ID",  visitorListStatusModelArrayList.get(position).getVisitor_id());
                intent.putExtra("VISITOR_NAME",  visitorListStatusModelArrayList.get(position).getName());
                intent.putExtra("VISITOR_MOBILE", finalLastTenDigits);
                intent.putExtra("VISITOR_EMAIL",  visitorListStatusModelArrayList.get(position).getEmail_id());
                intent.putExtra("VISITOR_CATEGORY",  visitorListStatusModelArrayList.get(position).getCategory());
                intent.putExtra("VISITOR_LOCATION",  visitorListStatusModelArrayList.get(position).getLocation());
                intent.putExtra("VISITOR_CHAPTER",  visitorListStatusModelArrayList.get(position).getChapter_name());
                intent.putExtra("VISITOR_SOURCE",  visitorListStatusModelArrayList.get(position).getSource());
                intent.putExtra("VISITOR_LAUNCH_DC",  visitorListStatusModelArrayList.get(position).getLaunch_dc());
                intent.putExtra("VISITOR_DESCRIPTION",  visitorListStatusModelArrayList.get(position).getDescription());
                mContext.startActivity(intent);
            }
        });
        holder.textHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("VISITOR", visitorListStatusModelArrayList.get(position).getVisitor_id());
                mContext.startActivity(intent);
            }
        });

        final String mobile_number_call = lastTenDigits;

        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile_number_call));
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return visitorListStatusModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName, textMobileNumber, textEmail, textChapterName, textLaunch_dc, textSource, textFollowUpDateTime, textCategory, textHistory,textDescription;
        private LinearLayout llFollowUpDateTime, ll_history;
        private CardView card_view_list;
        private ImageView iv_call,iv_edit,iv_edit_follow_up;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textMobileNumber = itemView.findViewById(R.id.textMobileNumber);
            textEmail = itemView.findViewById(R.id.textEmail);
            textChapterName = itemView.findViewById(R.id.textChapterName);
            textLaunch_dc = itemView.findViewById(R.id.textLaunch_dc);
            card_view_list = itemView.findViewById(R.id.card_view_list);
            textSource = itemView.findViewById(R.id.textSource);
            textFollowUpDateTime = itemView.findViewById(R.id.textFollowUpDateTime);
            textCategory = itemView.findViewById(R.id.textCategory);
            llFollowUpDateTime = itemView.findViewById(R.id.llFollowUpDateTime);
            iv_call = itemView.findViewById(R.id.iv_call);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            ll_history = itemView.findViewById(R.id.ll_history);
            textHistory = itemView.findViewById(R.id.textHistory);
            textDescription = itemView.findViewById(R.id.textDescription);
            iv_edit_follow_up = itemView.findViewById(R.id.iv_edit_follow_up);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s","").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    visitorListStatusModelArrayList = arrayListFiltered;
                } else {
                    ArrayList<VisitorListStatusModel> filteredList = new ArrayList<>();
                    for (int i = 0; i < visitorListStatusModelArrayList.size(); i++) {
                        String name=visitorListStatusModelArrayList.get(i).getName().replaceAll("\\s","").toLowerCase().trim();
                        String  category=visitorListStatusModelArrayList.get(i).getCategory().toLowerCase().replaceAll("\\s","").toLowerCase().trim();
                        if ((name.contains(charString))||(category.contains(charString))) {
                            filteredList.add(visitorListStatusModelArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        visitorListStatusModelArrayList = filteredList;
                    } else {
                        visitorListStatusModelArrayList = arrayListFiltered;
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = visitorListStatusModelArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                visitorListStatusModelArrayList = (ArrayList<VisitorListStatusModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
