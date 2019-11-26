package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.EditVisitorActivity;
import com.mezyapps.bni_visitor.activity.EditVisitorDetailsActivity;
import com.mezyapps.bni_visitor.activity.HistoryActivity;
import com.mezyapps.bni_visitor.model.VisitorListAllModel;
import com.mezyapps.bni_visitor.model.VisitorListStatusModel;

import java.util.ArrayList;

public class AllVisitorListAdapter  extends RecyclerView.Adapter<AllVisitorListAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<VisitorListAllModel> visitorListAllModelArrayList;

    public AllVisitorListAdapter(Context mContext, ArrayList<VisitorListAllModel> visitorListAllModelArrayList) {
        this.mContext = mContext;
        this.visitorListAllModelArrayList = visitorListAllModelArrayList;
    }

    @NonNull
    @Override
    public AllVisitorListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_visitor_list,parent,false);
        return new AllVisitorListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllVisitorListAdapter.MyViewHolder holder, final int position) {
        final  VisitorListAllModel visitorListAllModel=visitorListAllModelArrayList.get(position);
        String status=visitorListAllModel.getStatus();
        holder.textName.setText(visitorListAllModel.getName());
        holder.textFollowUpDateTime.setText(visitorListAllModel.getFollow_up_date_time());
        String mobile_number =visitorListAllModel.getMobile_no().toString().replaceAll("\\s","").toLowerCase().trim();
        String lastTenDigits = "";

        if (mobile_number.length() > 10) {
            lastTenDigits = mobile_number.substring(mobile_number.length() - 10);
        } else {
            lastTenDigits = mobile_number;
        }
        holder.textMobileNumber.setText(lastTenDigits);
        holder.textEmail.setText(visitorListAllModel.getEmail_id());
        holder.textChapterName.setText(visitorListAllModel.getChapter_name());
        holder.textLaunch_dc.setText(visitorListAllModel.getLaunch_dc());
        holder.textSource.setText(visitorListAllModel.getSource());
        holder.textCategory.setText(visitorListAllModel.getCategory());
        holder.textDescription.setText(visitorListAllModel.getDescription());
        final String mobile_number_call=lastTenDigits;
        final String finalLastTenDigits = lastTenDigits;

        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mobile_number_call));
                mContext.startActivity(intent);
            }
        });


        holder.iv_edit_follow_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditVisitorDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("VISITOR_ID",  visitorListAllModelArrayList.get(position).getVisitor_id());
                intent.putExtra("VISITOR_STATUS",  visitorListAllModelArrayList.get(position).getStatus());
                intent.putExtra("VISITOR_NAME",  visitorListAllModelArrayList.get(position).getName());
                intent.putExtra("VISITOR_LAUNCH_DC",  visitorListAllModelArrayList.get(position).getLaunch_dc());
                intent.putExtra("VISITOR_FOLLOW_DATE",  visitorListAllModelArrayList.get(position).getFollow_up_date_time());
                intent.putExtra("FOLLOW_DATE",  visitorListAllModelArrayList.get(position).getInserted_date_time());
                mContext.startActivity(intent);
            }
        });
        holder.textHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("VISITOR", visitorListAllModelArrayList.get(position).getVisitor_id());
                mContext.startActivity(intent);
            }
        });

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditVisitorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("VISITOR_ID",  visitorListAllModelArrayList.get(position).getVisitor_id());
                intent.putExtra("VISITOR_NAME",  visitorListAllModelArrayList.get(position).getName());
                intent.putExtra("VISITOR_MOBILE", finalLastTenDigits);
                intent.putExtra("VISITOR_EMAIL",  visitorListAllModelArrayList.get(position).getEmail_id());
                intent.putExtra("VISITOR_CATEGORY",  visitorListAllModelArrayList.get(position).getCategory());
                intent.putExtra("VISITOR_LOCATION",  visitorListAllModelArrayList.get(position).getLocation());
                intent.putExtra("VISITOR_CHAPTER",  visitorListAllModelArrayList.get(position).getChapter_name());
                intent.putExtra("VISITOR_SOURCE",  visitorListAllModelArrayList.get(position).getSource());
                intent.putExtra("VISITOR_LAUNCH_DC",  visitorListAllModelArrayList.get(position).getLaunch_dc());
                intent.putExtra("VISITOR_DESCRIPTION",  visitorListAllModelArrayList.get(position).getDescription());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return visitorListAllModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName,textMobileNumber,textEmail,textChapterName,textLaunch_dc,textSource,textFollowUpDateTime,textCategory,textHistory,textDescription;
        private LinearLayout llFollowUpDateTime;
        private CardView card_view_list;
        private ImageView iv_call,iv_edit,iv_edit_follow_up;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textName=itemView.findViewById(R.id.textName);
            iv_edit_follow_up=itemView.findViewById(R.id.iv_edit_follow_up);
            textMobileNumber=itemView.findViewById(R.id.textMobileNumber);
            textEmail=itemView.findViewById(R.id.textEmail);
            textChapterName=itemView.findViewById(R.id.textChapterName);
            textLaunch_dc=itemView.findViewById(R.id.textLaunch_dc);
            card_view_list=itemView.findViewById(R.id.card_view_list);
            textSource=itemView.findViewById(R.id.textSource);
            textFollowUpDateTime=itemView.findViewById(R.id.textFollowUpDateTime);
            textCategory=itemView.findViewById(R.id.textCategory);
            llFollowUpDateTime=itemView.findViewById(R.id.llFollowUpDateTime);
            iv_call=itemView.findViewById(R.id.iv_call);
            textHistory=itemView.findViewById(R.id.textHistory);
            iv_edit=itemView.findViewById(R.id.iv_edit);
            textDescription=itemView.findViewById(R.id.textDescription);
        }
    }
}
