package com.mezyapps.bni_visitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.model.ContactListModel;
import com.mezyapps.bni_visitor.utils.ContactListInterface;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<ContactListModel> contactListModelArrayList;
    private ArrayList<ContactListModel> arrayListFiltered;
    private ContactListInterface contactListInterface;

    public ContactListAdapter(Context mContext, ArrayList<ContactListModel> contactListModelArrayList, ContactListInterface contactListInterface) {
        this.mContext = mContext;
        this.contactListModelArrayList = contactListModelArrayList;
        this.arrayListFiltered = contactListModelArrayList;
        this.contactListInterface = (ContactListInterface) contactListInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_contact_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
               final ContactListModel contactListModel = contactListModelArrayList.get(position);
               holder.textName.setText(contactListModel.getName());
               holder.textMobileNumber.setText(contactListModel.getContact());

               holder.card_view_list.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       contactListInterface.selectContact(contactListModel.getName(), contactListModel.getContact());
                   }
               });


    }

    @Override
    public int getItemCount() {
        return contactListModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName, textMobileNumber;
        private CardView card_view_list;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textMobileNumber = itemView.findViewById(R.id.textMobileNumber);
            card_view_list = itemView.findViewById(R.id.card_view_list);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s", "").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    contactListModelArrayList = arrayListFiltered;
                } else {
                    try {
                        ArrayList<ContactListModel> filteredList = new ArrayList<>();
                        for (int i = 0; i < contactListModelArrayList.size(); i++) {
                            String name = contactListModelArrayList.get(i).getName().replaceAll("\\s", "").toLowerCase().trim();
                            String contact = contactListModelArrayList.get(i).getContact().replaceAll("\\s", "").toLowerCase().trim();
                            if ((name.contains(charString))||(contact.contains(charString))) {
                                filteredList.add(contactListModelArrayList.get(i));
                            }
                        }
                        if (filteredList.size() > 0) {
                            contactListModelArrayList = filteredList;
                        } else {
                            contactListModelArrayList = arrayListFiltered;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        contactListModelArrayList = arrayListFiltered;
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListModelArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListModelArrayList = (ArrayList<ContactListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
