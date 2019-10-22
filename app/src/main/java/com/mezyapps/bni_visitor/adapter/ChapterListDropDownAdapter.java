package com.mezyapps.bni_visitor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.model.ChapterListModel;

import java.util.ArrayList;

public class ChapterListDropDownAdapter extends ArrayAdapter<ChapterListModel> {

    private ArrayList<ChapterListModel> chapterListModelArrayList;
    private Context mContext;
    private LayoutInflater inflater;

    public ChapterListDropDownAdapter(Context context, ArrayList<ChapterListModel> list) {
        super(context, R.layout.list_item_contact_adapter);
        this.mContext = context;
        this.chapterListModelArrayList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.list_item_contact_adapter, null);
        ChapterListModel chapterListModel = getItem(position);
        TextView textViewName = view.findViewById(R.id.text_name);
        textViewName.setText(chapterListModel.getChapter_name());
        view.setTag(chapterListModel);
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((ChapterListModel) (resultValue)).getChapter_name();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                ArrayList<ChapterListModel> suggestions = new ArrayList<>();

                for (ChapterListModel chapterListModel  : chapterListModelArrayList) {
                    if (chapterListModel.getChapter_name().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(chapterListModel);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                addAll((ArrayList<ChapterListModel>) results.values);
            }
            notifyDataSetChanged();
        }
    };
}

