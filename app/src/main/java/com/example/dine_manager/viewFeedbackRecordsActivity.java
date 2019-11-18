package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class viewFeedbackRecordsActivity extends AppCompatActivity {

    public static final String TAG = "viewFeedbackRecords";

    private RecyclerView allFeedbackRecordsList;
    private ImageView    backButton;
    private CustomDialog customDialog;
    private TextView     nothingToDisplay;

    private FirebaseDatabase    firebaseDatabase;
    private DatabaseReference   feedbackReference;
    private ArrayList<Feedback> feedbackRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback_records);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        nothingToDisplay = findViewById(R.id.tv8_nothingToDisplay);
        firebaseDatabase = FirebaseDatabase.getInstance();
        feedbackReference = firebaseDatabase.getReference().child("feedback_records");

        customDialog = new CustomDialog(viewFeedbackRecordsActivity.this);
        customDialog.showProgressBar();

        backButton = findViewById(R.id.imv8_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();super.onResume();

        feedbackRecords = new ArrayList<>();
        feedbackReference.child(getCurrentDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                feedbackRecords.clear();

                for(DataSnapshot recordSnapShot : dataSnapshot.getChildren()) {
                    feedbackRecords.add(recordSnapShot.getValue(Feedback.class));
                }

                Log.e(TAG, "Total records fetched : "+feedbackRecords.size());

                allFeedbackRecordsList = findViewById(R.id.rv8_allFeedbackRecords);
                allFeedbackRecordsList.setLayoutManager(new LinearLayoutManager(viewFeedbackRecordsActivity.this));
                itemOffsetDecoration itemDecoration = new itemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
                allFeedbackRecordsList.addItemDecoration(itemDecoration);
                customDialog.hideProgressdialog();

                if(feedbackRecords.isEmpty()) {
                    nothingToDisplay.setVisibility(View.VISIBLE);
                    allFeedbackRecordsList.setVisibility(View.GONE);
                } else {
                    allFeedbackRecordsList.setVisibility(View.VISIBLE);
                    nothingToDisplay.setVisibility(View.GONE);
                    Collections.reverse(feedbackRecords);
                    FeedbackRecordsAdapter adapter = new FeedbackRecordsAdapter(getApplicationContext(), feedbackRecords);
                    allFeedbackRecordsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String getCurrentDate()
    {
        String dateToday ;
        Date date  = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateToday = dateFormat.format(date);

        return dateToday;
    }
    private String getCurrentTime()
    {
        String timeNow;
        Date date = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();

        SimpleDateFormat timeFomat = new SimpleDateFormat("hh:mm:ss_a");
        timeNow = timeFomat.format(date);
        return timeNow;
    }
    private boolean deviceIsOnline()
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnected())
            return true;

        return false;
    }
}
