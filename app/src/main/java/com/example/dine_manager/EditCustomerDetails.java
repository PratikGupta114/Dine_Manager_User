package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditCustomerDetails extends AppCompatActivity {

    public static final String TAG = "EditCustomerDetails";

    private ImageView   nameEB, emailEB, contactEB, seatsEB, visitsEB, backButton;
    private TextView    tv_name, tv_contact, tv_email, tv_seats, tv_roomNumber;
    private Button      doneButton;

    private FirebaseDatabase    firebaseDatabase;
    private DatabaseReference   customersReference;
    private DatabaseReference   recordsReference;
    private Customer            thisCustomer;
    private CustomerRecord      thisRecord;
    private String              recordDate;
    private int                 previouslyBookedSeat;
    private int                 processStatus = 0;

    private CustomDialog        customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_details);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        setupViews();

        firebaseDatabase = FirebaseDatabase.getInstance();
        customersReference = firebaseDatabase.getReference().child("customers");
        recordsReference = firebaseDatabase.getReference().child("customer_records");
        customDialog = new CustomDialog(EditCustomerDetails.this);

        String customerID = "";
        String recordID = "";


        Intent intent = getIntent();
        if(intent.hasExtra("CUSTOMER_ID")) {
            customerID = intent.getStringExtra("CUSTOMER_ID");
            recordID = intent.getStringExtra("RECORD_ID");
            recordDate = intent.getStringExtra("RECORD_DATE");
            Log.e(TAG, "ID : "+customerID);
        } else { finish(); }

        Query recordQuery = recordsReference.child(recordDate)
                                .orderByChild("recordID")
                                .equalTo(recordID);
        recordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                synchronized (this) {
                    if(processStatus == 0)
                        processStatus += 1;
                    else {
                        customDialog.hideProgressdialog();
                        processStatus = 0;
                    }
                }

                for (DataSnapshot recordSnapshot : dataSnapshot.getChildren())
                    thisRecord = recordSnapshot.getValue(CustomerRecord.class);

                    if(thisRecord == null) {
                        Log.e(TAG, "customer details is null hence quitting");
                        finish();

                } else {
                    updateUI(thisRecord);
                    previouslyBookedSeat = Integer.valueOf(thisRecord.getSeatsBooked());
                    Log.e(TAG, "customer name : "+thisRecord.getName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        customersReference.child(customerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                synchronized (this) {
                    if(processStatus == 0)
                        processStatus += 1;
                    else {
                        customDialog.hideProgressdialog();
                        processStatus = 0;
                    }
                }

                thisCustomer = dataSnapshot.getValue(Customer.class);
                if(thisCustomer != null)
                    Log.e(TAG, "Customer Received");
                else
                    Log.e(TAG , "Customer is null");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCustomerDetails.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_BACK));
                EditCustomerDetails.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP , KeyEvent.KEYCODE_BACK));
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceIsOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditCustomerDetails.this);
                    builder.setMessage("Save changes to the data ?.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveChanges();
                                }
                            })
                            .setNegativeButton("No", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(EditCustomerDetails.this, "You are Offline", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nameEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.showDialogForInput(CustomDialog.NAME, thisRecord.getName());
                customDialog.setOnInputSetListener(new CustomDialog.OnInputSetListener() {
                    @Override
                    public void onInputSet(String userInput, int requestCode) {
                        tv_name.setText(userInput);
                    }
                });
            }
        });

        emailEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.showDialogForInput(CustomDialog.EMAIL, thisRecord.getEmailAddress());
                customDialog.setOnInputSetListener(new CustomDialog.OnInputSetListener() {
                    @Override
                    public void onInputSet(String userInput, int requestCode) {
                        tv_email.setText(userInput);
                    }
                });
            }
        });

        contactEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.showDialogForInput(CustomDialog.CONTACT, thisRecord.getContact());
                customDialog.setOnInputSetListener(new CustomDialog.OnInputSetListener() {
                    @Override
                    public void onInputSet(String userInput, int requestCode) {
                        tv_contact.setText(userInput);
                    }
                });
            }
        });

        seatsEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.showDialogForInput(CustomDialog.NUMBER_OF_SEATS, String.valueOf(thisRecord.getSeatsBooked()));
                customDialog.setOnInputSetListener(new CustomDialog.OnInputSetListener() {
                    @Override
                    public void onInputSet(String userInput, int requestCode) {
                        tv_seats.setText(userInput);
                    }
                });
            }
        });

        visitsEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.showDialogForInput(CustomDialog.ROOM_NUMBER, String.valueOf(thisRecord.getRoomNumber()));
                customDialog.setOnInputSetListener(new CustomDialog.OnInputSetListener() {
                    @Override
                    public void onInputSet(String userInput, int requestCode) {
                        tv_roomNumber.setText(userInput);
                    }
                });
            }
        });
    }
    private void setupViews() {
        nameEB = findViewById(R.id.bt10_customerNameEB);
        contactEB = findViewById(R.id.bt10_contactNumberED);
        emailEB = findViewById(R.id.bt10_emailAddressEB);
        seatsEB = findViewById(R.id.bt10_numberOfSeatsEB);
        visitsEB = findViewById(R.id.bt10_numberOfVisitsEB);

        tv_name = findViewById(R.id.tv10_customerName);
        tv_contact = findViewById(R.id.tv10_contactNumber);
        tv_email = findViewById(R.id.tv10_emailAddress);
        tv_seats = findViewById(R.id.tv10_numberOfSeatsBooked);
        tv_roomNumber = findViewById(R.id.tv10_roomNumber);

        backButton = findViewById(R.id.imv10_backButton);
        doneButton = findViewById(R.id.bt10_doneButton);
    }
    private void updateUI(CustomerRecord customerRecord) {
        tv_name.setText(customerRecord.getName());
        tv_contact.setText(customerRecord.getContact());
        tv_email.setText(customerRecord.getEmailAddress());
        tv_roomNumber.setText(String.valueOf(customerRecord.getRoomNumber()));
        tv_seats.setText(String.valueOf(customerRecord.getSeatsBooked()));
    }
    private void saveChanges() {
        thisRecord.setName(tv_name.getText().toString().trim());
        thisRecord.setContact(tv_contact.getText().toString().trim());
        thisRecord.setEmailAddress(tv_email.getText().toString().trim());
        thisRecord.setRoomNumber(tv_roomNumber.getText().toString().trim());
        thisRecord.setSeatsBooked(tv_seats.getText().toString().trim());

        thisCustomer.setName(tv_name.getText().toString().trim());
        thisCustomer.setContact(tv_contact.getText().toString().trim());
        thisCustomer.setEmailAddress(tv_email.getText().toString().trim());
        thisCustomer.setTotalSeatsBooked( thisCustomer.getTotalSeatsBooked() - previouslyBookedSeat + Integer.valueOf(tv_seats.getText().toString().trim()));

        customDialog.showProgressBar();
        recordsReference.child(recordDate).child(thisRecord.getRecordID()).setValue(thisRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            synchronized (this) {
                                if(processStatus == 0)
                                    processStatus += 1;
                                else {
                                    customDialog.hideProgressdialog();
                                    Toast.makeText(EditCustomerDetails.this, "Data saved !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        } else {
                            customDialog.hideProgressdialog();
                            Toast.makeText(EditCustomerDetails.this, "Couldn't update record !", Toast.LENGTH_SHORT).show();
                            processStatus = 0;
                        }
                    }

                });
        customersReference.child(thisCustomer.getCustomerID()).setValue(thisCustomer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            synchronized (this) {
                                if(processStatus == 0)
                                    processStatus += 1;
                                else {
                                    customDialog.hideProgressdialog();
                                    Toast.makeText(EditCustomerDetails.this, "Data saved !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        } else {
                            customDialog.hideProgressdialog();
                            Toast.makeText(EditCustomerDetails.this, "Could not Update Customer Details !", Toast.LENGTH_SHORT).show();
                            processStatus = 0;
                        }
                    }
                });

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
