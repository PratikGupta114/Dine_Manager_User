package com.example.dine_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

public class feedback2_Activity extends AppCompatActivity {

    private RatingBar experienceRatingBar;
    private Button    previousButton, nextButton;
    private ImageView backButton;
    private float     experienceRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback2);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        setupUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                experienceRating = experienceRatingBar.getRating();

                if(experienceRating <= 0.5f)
                    experienceRating = 0.5f;

                FeedbackRecord.setExperienceRatingLevel(experienceRating);
                Intent intent = new Intent(feedback2_Activity.this, feedback3_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setupUI() {
        previousButton = findViewById(R.id.bt4_previousButton);
        nextButton = findViewById(R.id.bt4_nextButton);
        experienceRatingBar = findViewById(R.id.rb4_ratingBar);
        backButton = findViewById(R.id.imv4_backButton);
    }
}
