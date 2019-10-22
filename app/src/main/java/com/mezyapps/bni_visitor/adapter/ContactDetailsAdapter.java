package com.mezyapps.bni_visitor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.model.ContactDetailsModel;

import java.util.ArrayList;

public class ContactDetailsAdapter extends ArrayAdapter<ContactDetailsModel>{

    private ArrayList<ContactDetailsModel> contactDetailsModelArrayList;
    private Context mContext;
    private LayoutInflater inflater;

    public ContactDetailsAdapter(Context context, ArrayList<ContactDetailsModel> list) {
        super(context, R.layout.list_item_contact_adapter);
        this.mContext = context;
        this.contactDetailsModelArrayList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.list_item_contact_adapter, parent,false);
        ContactDetailsModel contactDetailsModel = contactDetailsModelArrayList.get(position);
        TextView textViewName = view.findViewById(R.id.textName);
        TextView textViewMobile = view.findViewById(R.id.textMobileNumber);
        String name=contactDetailsModel.getContactName();
        if(name.equalsIgnoreCase("")) {
            textViewName.setText(name);
            textViewMobile.setText(contactDetailsModel.getNumber());
        }
        view.setTag(contactDetailsModel);
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((ContactDetailsModel) (resultValue)).getContactName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                ArrayList<ContactDetailsModel> suggestions = new ArrayList<>();

                for (ContactDetailsModel contactDetailsModel  : contactDetailsModelArrayList) {
                    if (contactDetailsModel.getContactName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(contactDetailsModel);
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
                addAll((ArrayList<ContactDetailsModel>) results.values);
            }
            notifyDataSetChanged();
        }
    };


}
