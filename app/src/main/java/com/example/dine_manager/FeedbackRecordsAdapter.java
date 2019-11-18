package com.example.dine_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

class FeedbackRecordsAdapter extends RecyclerView.Adapter<FeedbackRecordsAdapter.customerFeedbackViewHolder> {

    private ArrayList<Feedback> allFeedbacks;
    private Context context;

    public FeedbackRecordsAdapter(Context applicationContext, ArrayList<Feedback> feedbackRecords) {
        this.context = applicationContext;
        this.allFeedbacks = feedbackRecords;
    }

    @NonNull
    @Override
    public customerFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemListView = inflater.inflate(R.layout.list_item_feedback_record, parent, false);
        return new customerFeedbackViewHolder(itemListView);
    }

    @Override
    public void onBindViewHolder(@NonNull customerFeedbackViewHolder holder, int position) {
        Feedback feedback = allFeedbacks.get(position);

        holder.customerName_tv.setText(feedback.getName());
        holder.customerContact_tv.setText(feedback.getContact());
        holder.customerRemarks_tv.setText(feedback.getRemarks());
        holder.foodRatingBar.setRating(feedback.getFoodRating());
        holder.experienceRatingBar.setRating(feedback.getExperienceRating());

        try {
            holder.servcieRatingBar.setRating(feedback.getServiceRating());
        } catch (Exception e ){
            holder.servcieRatingBar.setRating(0);
        }

        String dt = feedback.getDate()+"  "+feedback.getTime();
        holder.dateTime_tv.setText(dt);
    }

    @Override
    public int getItemCount() {
        return allFeedbacks.size();
    }

    public class customerFeedbackViewHolder extends RecyclerView.ViewHolder{

        TextView customerName_tv, customerContact_tv, customerRemarks_tv, dateTime_tv;
        RatingBar foodRatingBar, experienceRatingBar, servcieRatingBar;

        public customerFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName_tv = itemView.findViewById(R.id.lifr_tv_customerName);
            customerContact_tv = itemView.findViewById(R.id.lifr_tv_contactNumber);
            customerRemarks_tv = itemView.findViewById(R.id.lifr_tv_remarks);
            dateTime_tv = itemView.findViewById(R.id.lifr_tv_dateTime);
            foodRatingBar = itemView.findViewById(R.id.lifr_foodRating);
            experienceRatingBar = itemView.findViewById(R.id.lifr_experienceRating);
            servcieRatingBar = itemView.findViewById(R.id.lifr_serviceRating);
        }
    }
}
