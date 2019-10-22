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
import com.mezyapps.bni_visitor.model.LunchDcModel;

import java.util.ArrayList;

public class LaunchDpListDropDownAdapter extends ArrayAdapter<LunchDcModel> {

    private ArrayList<LunchDcModel> lunchDcModelArrayList;
    private Context mContext;
    private LayoutInflater inflater;

    public LaunchDpListDropDownAdapter(Context context, ArrayList<LunchDcModel> list) {
        super(context, R.layout.list_item_contact_adapter);
        this.mContext = context;
        this.lunchDcModelArrayList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.list_item_contact_adapter, null);
        LunchDcModel lunchDcModel = getItem(position);
        TextView textViewName = view.findViewById(R.id.text_name);
        textViewName.setText(lunchDcModel.getLaunch_dc_name());
        view.setTag(lunchDcModel);
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((LunchDcModel) (resultValue)).getLaunch_dc_name();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                ArrayList<LunchDcModel> suggestions = new ArrayList<>();

                for (LunchDcModel lunchDcModel  : lunchDcModelArrayList) {
                    if (lunchDcModel.getLaunch_dc_name().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(lunchDcModel);
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
                addAll((ArrayList<LunchDcModel>) results.values);
            }
            notifyDataSetChanged();
        }
    };
}

