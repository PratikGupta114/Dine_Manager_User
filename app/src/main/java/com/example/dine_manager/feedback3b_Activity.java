package com.example.dine_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class feedback3b_Activity extends AppCompatActivity {

    private ImageView   backButton;
    private Button      prevButton, nextButton;
    private RatingBar   ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback3b);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackRecord.setServiceRatingLevel((ratingBar.getRating() <= 0.5f) ? 0.5f : ratingBar.getRating());
                Intent intent = new Intent(feedback3b_Activity.this, feedback4_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setupViews() {
        prevButton = findViewById(R.id.bt12_previousButton);
        backButton = findViewById(R.id.imv12_backButton);
        nextButton = findViewById(R.id.bt12_nextButton);
        ratingBar = findViewById(R.id.rb12_ratingBar);
    }
}
