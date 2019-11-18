package com.example.dine_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteCustomerAdapter extends ArrayAdapter<Customer> {

    private List<Customer> customersListFull;

    public AutoCompleteCustomerAdapter(@NonNull Context context, @NonNull List<Customer> customersList) {
        super(context, 0, customersList);
        customersListFull = new ArrayList<>(customersList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return customerFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView  == null ){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(
                    R.layout.customer_autocomplete_row_layout,
                    parent,
                    false
            );
        }

        TextView tv_customerName = convertView.findViewById(R.id.carl1_customerName);
        TextView tv_customerContact = convertView.findViewById(R.id.carl1_customerContact);
        TextView tv_customerVisits = convertView.findViewById(R.id.carl1_customerVisits);

        Customer customer = getItem(position);

        if(customer != null) {
            tv_customerName.setText(String.valueOf(customer.getName()));
            tv_customerContact.setText(String.valueOf(customer.getContact()));
            tv_customerVisits.setText(String.valueOf(customer.getNumberOfVisits())+" visits");
        }

        return convertView;
    }

    private Filter customerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Customer> suggestions = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                suggestions.addAll(customersListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Customer customer : customersListFull) {
                    if(customer.getContact().toLowerCase().contains(filterPattern)) {
                        suggestions.add(customer);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Customer)resultValue).getContact();
        }
    };
}
