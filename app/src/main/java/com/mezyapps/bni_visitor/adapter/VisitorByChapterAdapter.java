package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.ChapterByVisitorPieActivity;
import com.mezyapps.bni_visitor.activity.VisitorBySourceListActivity;
import com.mezyapps.bni_visitor.activity.VisitorListByChapterDisplayActivity;
import com.mezyapps.bni_visitor.model.ChapterListModel;

import java.util.ArrayList;

public class VisitorByChapterAdapter extends RecyclerView.Adapter<VisitorByChapterAdapter.MyViewHolder>
{
    private Context mContext;
    private ArrayList<ChapterListModel> chapterListModelArrayList;

    public VisitorByChapterAdapter(Context mContext, ArrayList<ChapterListModel> chapterListModelArrayList) {
        this.mContext = mContext;
        this.chapterListModelArrayList = chapterListModelArrayList;
    }


    @NonNull
    @Override
    public VisitorByChapterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_item_adpater,parent,false);
        return new VisitorByChapterAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull VisitorByChapterAdapter.MyViewHolder holder, final int position) {
        final  ChapterListModel chapterListModel=chapterListModelArrayList.get(position);
        holder.textChapter.setText(chapterListModel.getChapter_name());

        holder.card_layout_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ChapterByVisitorPieActivity.class);
                intent.putExtra("chapter",chapterListModelArrayList.get(position).getChapter_id());
                intent.putExtra("chapter_name",chapterListModelArrayList.get(position).getChapter_name());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterListModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CardView card_layout_chapter;
        private TextView textChapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textChapter=itemView.findViewById(R.id.textChapter);
            card_layout_chapter=itemView.findViewById(R.id.card_layout_chapter);
        }
    }
}
