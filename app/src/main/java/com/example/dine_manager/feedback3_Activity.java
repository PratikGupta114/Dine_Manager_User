package com.example.dine_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

public class feedback3_Activity extends AppCompatActivity {

    private SmileRating smileyRating;
    private Button nextButton, previousButton;
    private ImageView backButton;
    private int foodRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback3);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        setupUI();
    }
    @Override
    protected void onStart() {
        super.onStart();

        smileyRating.setSelectedSmile(BaseRating.OKAY);
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
                foodRating = smileyRating.getRating();
                FeedbackRecord.setFoodRatingLevel(foodRating);
                Intent intent = new Intent(feedback3_Activity.this, feedback3b_Activity.class);
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
        smileyRating = findViewById(R.id.smiley_rating);
        nextButton = findViewById(R.id.bt5_nextButton);
        previousButton = findViewById(R.id.bt5_previousButton);
        backButton = findViewById(R.id.imv5_backButton);
    }
}
