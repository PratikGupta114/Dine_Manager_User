package com.example.dine_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class feedBack1_Activity extends AppCompatActivity {

    private TextView    helloTextView;
    private ImageView   backButton;
    private Button      proceedButton;
    private String      customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback1);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        String cName = "";
        String cContact = "";
        String cID = "";
        String emailADD = "";
        String[] arr ;

        try {
            Intent intent = getIntent();
            cName = intent.getStringExtra("CUSTOMER_NAME");
            cContact = intent.getStringExtra("CUSTOMER_CONTACT");
            cID = intent.getStringExtra("CUSTOMER_ID");
            emailADD = intent.getStringExtra("CUSTOMER_EMAIL");

            arr = cName.trim().split(" ");
            if(arr.length > 2)
                customerName = arr[0]+" "+arr[1];
            else
                customerName = arr[0];
        } catch (Exception e) {
            cName = "Unknown";
            cContact = "-NA-";
            customerName = cName;
        }

        FeedbackRecord.setCustomerContact(cContact);
        FeedbackRecord.setCustomerName(cName);
        FeedbackRecord.setCustomerID(cID);
        FeedbackRecord.setEmailAddress(emailADD);

        setupView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack1_Activity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_BACK));
                feedBack1_Activity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP , KeyEvent.KEYCODE_BACK));
            }
        });
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(feedBack1_Activity.this, feedback2_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setupView() {
        helloTextView = findViewById(R.id.tv3_helloText);
        backButton = findViewById(R.id.imv3_backButton);
        proceedButton = findViewById(R.id.bt3_proceedButton);

        if(customerName.equals("Unknown"))
            helloTextView.setText("Hello There ?");
        else
            helloTextView.setText("Hello "+customerName+" ?");
    }
}
