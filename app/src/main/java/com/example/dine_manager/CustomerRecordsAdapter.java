package com.example.dine_manager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerRecordsAdapter extends RecyclerView.Adapter<CustomerRecordsAdapter.customerRecordsViewHolder> implements Filterable {

    private ArrayList<CustomerRecord> allRecords;
    private ArrayList<CustomerRecord> allRecordsFull;
    private Context context;

    public CustomerRecordsAdapter(Context context, ArrayList<CustomerRecord> allRecords) {
        this.allRecords = allRecords;
        this.allRecordsFull = new ArrayList<>(this.allRecords);
        this.context = context;
    }

    @NonNull
    @Override
    public customerRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemListView = inflater.inflate(R.layout.list_item_customer_record, parent, false);
        return new customerRecordsViewHolder(itemListView);
    }

    @Override
    public void onBindViewHolder(@NonNull final customerRecordsViewHolder holder, int position) {
        final CustomerRecord record = allRecords.get(position);
        holder.customerName_tv.setText(record.getName());
        holder.customerContact_tv.setText(record.getContact());
        holder.emailAddress_tv.setText(record.getEmailAddress());
        holder.roomNumber_tv.setText(record.getRoomNumber());
        holder.seatsBooked_tv.setText(record.getSeatsBooked());
        holder.tagsList_tv.setText(getList(record.getTags()));
        String dt = record.getDate()+"  "+record.getTime();
        holder.timedate_tv.setText(dt);

        if(record.getVisit() != 0)
            holder.visitNumber.setText("( "+getVisitString(record.getVisit())+" visit )" );
        else
            holder.visitNumber.setText("( -- NA -- )" );

        holder.recordOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu askFeedbackOption = new PopupMenu(context, holder.recordOptions);
                askFeedbackOption.inflate(R.menu.customer_options_menu);
                askFeedbackOption.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.com_askForFeedback :
                                Intent intent1 = new Intent(context, feedBack1_Activity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent1.putExtra("CUSTOMER_NAME", record.getName());
                                intent1.putExtra("CUSTOMER_CONTACT", record.getContact());
                                intent1.putExtra("CUSTOMER_EMAIL", record.getEmailAddress());
                                intent1.putExtra("CUSTOMER_ID", record.getCustomerID());
                                context.startActivity(intent1);
                                break;
                            case R.id.com_editCustomer :
                                Intent intent2 = new Intent(context, EditCustomerDetails.class);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent2.putExtra("CUSTOMER_NAME", record.getName());
                                intent2.putExtra("CUSTOMER_CONTACT", record.getContact());
                                intent2.putExtra("CUSTOMER_EMAIL", record.getEmailAddress());
                                intent2.putExtra("CUSTOMER_ID", record.getCustomerID());
                                intent2.putExtra("CUSTOMER_ROOM_NUMBER", record.getRoomNumber());
                                intent2.putExtra("CUSTOMER_SEATS_BOOKED", record.getSeatsBooked());
                                intent2.putExtra("RECORD_ID", record.getRecordID());
                                intent2.putExtra("RECORD_DATE", record.getDate());
                                context.startActivity(intent2);
                                break;
                        }
                        return false;
                    }
                });
                askFeedbackOption.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return allRecords.size();
    }

    @Override
    public Filter getFilter() {
        return recordFilter;
    }


    public class customerRecordsViewHolder extends RecyclerView.ViewHolder {

        TextView customerName_tv, customerContact_tv, timedate_tv, seatsBooked_tv, roomNumber_tv, emailAddress_tv, tagsList_tv;
        TextView recordOptions, visitNumber;

        public customerRecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            customerContact_tv = itemView.findViewById(R.id.licr_tv_contact);
            customerName_tv = itemView.findViewById(R.id.licr_tv_customerName);
            timedate_tv = itemView.findViewById(R.id.licr_tv_timeDate);
            seatsBooked_tv = itemView.findViewById(R.id.licr_tv_seatsBooked);
            roomNumber_tv = itemView.findViewById(R.id.licr_tv_roomNumber);
            emailAddress_tv = itemView.findViewById(R.id.licr_tv_emailAddress);
            tagsList_tv = itemView.findViewById(R.id.licr_tv_tagsList);
            recordOptions = itemView.findViewById(R.id.licr_recordOptions);
            visitNumber = itemView.findViewById(R.id.licr_tv_visitNumber);
        }
    }

    private String getList(ArrayList<String> tags) {
        if(tags == null){
            return "--No tags Selected--";
        }
        String list = "";
        for(int i=0 ; i<tags.size() ; i++ ) {
            if(i != tags.size()-1)
                list += tags.get(i) + ", ";
            else
                list += tags.get(i);
        }
        return list;
    }

    private String getVisitString(int n) {
        int r = n%10;

        if(r == 1)
            return (n+"st");
        else if(r == 2)
            return (n+"nd");
        else if(r == 3)
            return (n+"rd");

        return (n+"th");
    }

    private Filter recordFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CustomerRecord> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(allRecordsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(CustomerRecord customerRecord : allRecordsFull) {
                    if(customerRecord.getContact().toLowerCase().contains(filterPattern) || customerRecord.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(customerRecord);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allRecords.clear();
            allRecords.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}
