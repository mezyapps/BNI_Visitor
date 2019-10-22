package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.EditChapterActivity;
import com.mezyapps.bni_visitor.activity.EditLaunchDcActivity;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.LunchDcModel;

import java.util.ArrayList;

public class LunchDcAdapter  extends RecyclerView.Adapter<LunchDcAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<LunchDcModel> lunchDcModelArrayList;

    public LunchDcAdapter(Context mContext, ArrayList<LunchDcModel> lunchDcModelArrayList) {
        this.mContext = mContext;
        this.lunchDcModelArrayList = lunchDcModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(mContext).inflate(R.layout.list_item__lunch_dc_adpater,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final  LunchDcModel lunchDcModel=lunchDcModelArrayList.get(position);
        holder.textLunchDc.setText(lunchDcModel.getLaunch_dc_name());
        holder.card_layout_LunchDc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, EditLaunchDcActivity.class);
                intent.putExtra("launch_dc_id",lunchDcModelArrayList.get(position).getLaunch_dc_id());
                intent.putExtra("launch_dc_name",lunchDcModelArrayList.get(position).getLaunch_dc_name());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lunchDcModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView card_layout_LunchDc;
        private TextView textLunchDc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textLunchDc=itemView.findViewById(R.id.textLunchDc);
            card_layout_LunchDc=itemView.findViewById(R.id.card_layout_LunchDc);
        }
    }
}
