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
import com.mezyapps.bni_visitor.activity.CommonVisitorActivity;
import com.mezyapps.bni_visitor.activity.VisitorBySourceListActivity;
import com.mezyapps.bni_visitor.activity.VisitorBySourcePieActivity;

import java.util.ArrayList;

public class SourceListAdapter extends RecyclerView.Adapter<SourceListAdapter.MyViewHolder> {

        private Context mContext;
        private ArrayList<String> sourceArrayList;

    public SourceListAdapter(Context mContext, ArrayList<String> sourceArrayList) {
            this.mContext = mContext;
            this.sourceArrayList = sourceArrayList;
        }


        @NonNull
        @Override
        public SourceListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view= LayoutInflater.from(mContext).inflate(R.layout.list_item_source_adpater,parent,false);
            return new SourceListAdapter.MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder.textSource.setText(sourceArrayList.get(position));

            holder.card_layout_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, VisitorBySourcePieActivity.class);
                    intent.putExtra("source",sourceArrayList.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return sourceArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            private CardView card_layout_source;
            private TextView textSource;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textSource=itemView.findViewById(R.id.textSource);
                card_layout_source=itemView.findViewById(R.id.card_layout_source);
            }
        }
}
