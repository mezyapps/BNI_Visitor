package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.mezyapps.bni_visitor.model.VisitorBySourceModel;
import com.mezyapps.bni_visitor.model.VisitorDateFilterModel;

import java.util.ArrayList;

public class VisitorDateFilterAdapter extends RecyclerView.Adapter<VisitorDateFilterAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<VisitorDateFilterModel> visitorDateFilterModelArrayList;

    public VisitorDateFilterAdapter(Context mContext,  ArrayList<VisitorDateFilterModel> visitorDateFilterModelArrayList) {
        this.mContext = mContext;
        this.visitorDateFilterModelArrayList = visitorDateFilterModelArrayList;
    }

    @NonNull
    @Override
    public VisitorDateFilterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_visitor_list, parent, false);
        return new VisitorDateFilterAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorDateFilterAdapter.MyViewHolder holder, int position) {
        final VisitorDateFilterModel visitorDateFilterModel = visitorDateFilterModelArrayList.get(position);
        String status = visitorDateFilterModel.getStatus();
        holder.textName.setText(visitorDateFilterModel.getName());
        holder.textMobileNumber.setText(visitorDateFilterModel.getMobile_no());
        holder.textEmail.setText(visitorDateFilterModel.getEmail_id());
        holder.textChapterName.setText(visitorDateFilterModel.getChapter_name());
        holder.textLaunch_dc.setText(visitorDateFilterModel.getLaunch_dc());
        holder.textSource.setText(visitorDateFilterModel.getSource());
        holder.textCategory.setText(visitorDateFilterModel.getCategory());

        final String mobile_number=visitorDateFilterModel.getMobile_no();

        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mobile_number));
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return visitorDateFilterModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName,textMobileNumber,textEmail,textChapterName,textLaunch_dc,textSource,textFollowUpDateTime,textCategory;
        private LinearLayout llFollowUpDateTime;
        private CardView card_view_list;
        private ImageView iv_call;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textMobileNumber = itemView.findViewById(R.id.textMobileNumber);
            textEmail = itemView.findViewById(R.id.textEmail);
            textChapterName = itemView.findViewById(R.id.textChapterName);
            textLaunch_dc = itemView.findViewById(R.id.textLaunch_dc);
            card_view_list = itemView.findViewById(R.id.card_view_list);
            textSource=itemView.findViewById(R.id.textSource);
            textFollowUpDateTime=itemView.findViewById(R.id.textFollowUpDateTime);
            textCategory=itemView.findViewById(R.id.textCategory);
            llFollowUpDateTime=itemView.findViewById(R.id.llFollowUpDateTime);
            iv_call=itemView.findViewById(R.id.iv_call);
        }
    }
}

