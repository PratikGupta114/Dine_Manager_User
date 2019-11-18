package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class feedback5_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "feedback5_Activity";

    private ImageView    backButton;
    private Button       selectBirthday, selectAnniversary, doneButton;

    private FirebaseDatabase    firebaseDatabase;
    private DatabaseReference   feedBackReference;
    private DatabaseReference   birthdaysReference;
    private DatabaseReference   anniversariesReference;
    private Statistics          todaysStatistics;
    private String              feedBackID;
    private CustomDialog        customDialog;

    private boolean      birthdaySelected = false;
    private boolean      anniversarySelected = false;
    private OEvent       Birthday;
    private OEvent       Anniversary;
    private String       customerID;
    private String       customerName;
    private String       customerContact;

    private int processStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback5);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        setupViews();

        firebaseDatabase = FirebaseDatabase.getInstance();
        feedBackReference = firebaseDatabase.getReference("feedback_records").child(getCurrentDate());
        birthdaysReference = firebaseDatabase.getReference("birthdays");
        anniversariesReference = firebaseDatabase.getReference("anniversaries");
        feedBackID = feedBackReference.push().getKey();
        customerID = FeedbackRecord.getCustomerID();

        DatabaseReference statisticsReference = firebaseDatabase.getReference().child("statistics").child(getCurrentDate());
        statisticsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todaysStatistics = dataSnapshot.getValue(Statistics.class);
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
                finish();
            }
        });

        selectBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment birthdayPicker = new DatePickerFragment();
                birthdayPicker.show(getSupportFragmentManager(), "Birthday Picker");
            }
        });
        selectAnniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment anniversaryPicker = new DatePickerFragment();
                anniversaryPicker.show(getSupportFragmentManager(), "Anniversary Picker");
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if device is Offline.
                if(!deviceIsOnline()) {
                    Toast.makeText(feedback5_Activity.this, "Device is Offline", Toast.LENGTH_SHORT).show();
                    return;
                }

                FeedbackRecord.setDate(getCurrentDate());
                FeedbackRecord.setTime(getCurrentTime());

                final Feedback feedback = new Feedback();
                feedback.setContact(FeedbackRecord.getCustomerContact());
                feedback.setName(FeedbackRecord.getCustomerName());
                feedback.setDate(FeedbackRecord.getDate());
                feedback.setTime(FeedbackRecord.getTime());
                feedback.setEmail(FeedbackRecord.getEmailAddress());
                feedback.setFoodRating(FeedbackRecord.getFoodRatingLevel());
                feedback.setExperienceRating(FeedbackRecord.getExperienceRatingLevel());
                feedback.setRemarks(FeedbackRecord.getRemarks());
                feedback.setFeedbackID(feedBackID);
                feedback.setCustomerID(FeedbackRecord.getCustomerID());
                feedback.setServiceRating(FeedbackRecord.getServiceRatingLevel());

                sendMessageViaSMS(FeedbackRecord.getCustomerContact(), getSMSTemplate(FeedbackRecord.getCustomerName()));

                // initiating the process
                customDialog.showProgressDialogWithMessage("Submitting Your Response");

                if(feedback.getCustomerID() == null || feedback.getCustomerID().equals("")) {
                       DatabaseReference customersReference = firebaseDatabase.getReference().child("customers");
                       String newCustomerID = customersReference.push().getKey();
                       customerID = newCustomerID;
                       customerName = feedback.getName();
                       customerContact = feedback.getContact();

                       Customer newCustomer = new Customer();
                       newCustomer.setName(feedback.getName());
                       newCustomer.setEmailAddress(feedback.getEmail());
                       newCustomer.setContact(feedback.getContact());
                       newCustomer.setLastVisitedOn(feedback.getDate()+" "+feedback.getTime());
                       newCustomer.setNumberOfVisits(1);
                       newCustomer.setTotalSeatsBooked(1);
                       newCustomer.setCustomerID(newCustomerID);

                       customersReference.child(newCustomerID).setValue(newCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    uploadData(feedback);
                                } else {
                                    customDialog.hideProgressdialog();
                                    Toast.makeText(feedback5_Activity.this, "Couldn't Upload data", Toast.LENGTH_SHORT).show();
                                }
                           }
                       });
                }
                else {
                    final DatabaseReference customerReference = firebaseDatabase.getReference().child("customers").child(feedback.getCustomerID());
                    customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Customer thisCustomer = dataSnapshot.getValue(Customer.class);

                            customerID = thisCustomer.getCustomerID();
                            customerName = thisCustomer.getName();
                            customerContact = thisCustomer.getContact();

                            thisCustomer.setLastVisitedOn(getCurrentDate()+" "+getCurrentTime());
                            thisCustomer.setNumberOfVisits(thisCustomer.getNumberOfVisits()+1);
                            thisCustomer.setTotalSeatsBooked(thisCustomer.getTotalSeatsBooked()+1);

                            customerReference.setValue(thisCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    uploadData(feedback);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        StringBuilder dateStringBuilder = new StringBuilder();
        StringBuilder dateStringBuilder2 = new StringBuilder();

        month = month+1;
        dateStringBuilder.append(dayOfMonth).append("-")
                         .append(month).append("-")
                         .append(year);

        dateStringBuilder2.append(new DecimalFormat("00").format(dayOfMonth)).append("/")
                          .append(new DecimalFormat("00").format(month)).append("/")
                          .append(new DecimalFormat("0000").format(year));

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager.findFragmentByTag("Birthday Picker") != null) {
            birthdaySelected = true;

            Birthday = new OEvent();
            Birthday.setDate(dateStringBuilder2.toString().trim());
            Birthday.setDayOfMonth(dayOfMonth);
            Birthday.setMonth(month);
            Birthday.setYear(year);
            Birthday.setCustomerName(FeedbackRecord.getCustomerName());
            Birthday.setCustomerContact(FeedbackRecord.getCustomerContact());

            selectBirthday.setText(dateStringBuilder2.toString().trim());
            Log.e(TAG, "Birthday selected : "+Birthday.getDate());
        }
        else if(fragmentManager.findFragmentByTag("Anniversary Picker") != null) {
            anniversarySelected = true;

            Anniversary = new OEvent();
            Anniversary.setDate(dateStringBuilder.toString().trim());
            Anniversary.setDayOfMonth(dayOfMonth);
            Anniversary.setMonth(month);
            Anniversary.setYear(year);
            Anniversary.setCustomerName(FeedbackRecord.getCustomerName());
            Anniversary.setCustomerContact(FeedbackRecord.getCustomerContact());

            selectAnniversary.setText(dateStringBuilder2.toString().trim());
            Log.e(TAG, "Anniversary selected : "+Anniversary.getDate());
        }
    }

    private void setupViews() {
        backButton = findViewById(R.id.imv13_backButton);
        selectAnniversary = findViewById(R.id.bt13_selectAnniversay);
        selectBirthday = findViewById(R.id.bt13_selectBirthday);
        doneButton = findViewById(R.id.bt13_doneButton);

        customDialog = new CustomDialog(feedback5_Activity.this);
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
    private void uploadData(Feedback feedback) {
        // Upload the feedback to the reference
        feedBackReference.child(feedBackID).setValue(feedback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // When Record in successfully uploaded Update the statistics
                        ArrayList<Integer> foodRatings = todaysStatistics.getFoodRatings();
                        ArrayList<Integer> experienceRatings = todaysStatistics.getExperienceRatings();
                        ArrayList<Integer> serviceRatings = todaysStatistics.getServiceRatings();

                        int index1 = ((int)FeedbackRecord.getFoodRatingLevel()-1);
                        int index2 = ((int)(FeedbackRecord.getExperienceRatingLevel()/0.5) - 1);
                        int index3 = ((int)(FeedbackRecord.getServiceRatingLevel()/0.5) - 1);

                        foodRatings.set(index1 , foodRatings.get(index1)+1);
                        experienceRatings.set(index2 , experienceRatings.get(index2)+1);
                        serviceRatings.set(index3 , serviceRatings.get(index3)+1);

                        todaysStatistics.setServiceRatings(serviceRatings);
                        todaysStatistics.setFoodRatings(foodRatings);
                        todaysStatistics.setExperienceRatings(experienceRatings);

                        DatabaseReference statisticsReference = firebaseDatabase.getReference().child("statistics").child(getCurrentDate());
                        statisticsReference.setValue(todaysStatistics)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {customDialog.hideProgressdialog();
                                        customDialog.hideProgressdialog();
                                        customDialog.showCompletedMessage("Response Submitted");
                                        customDialog.setOnButtonPressListener(new CustomDialog.OnButtonPressListener() {
                                            @Override
                                            public void onButtonPressed() {
                                                customDialog.hideProgressdialog();
                                                synchronized (this) {
                                                    if(processStatus < 2)
                                                        processStatus += 1;
                                                    else {
                                                        // Jump to main activity
                                                        Intent intent = new Intent(feedback5_Activity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // When Uploading Failed
                        customDialog.hideProgressdialog();
                        Toast.makeText(feedback5_Activity.this, "Uploading failed ! Try Again", Toast.LENGTH_SHORT).show();

                    }
                });
        if(birthdaySelected) {
            // Upload Birthday
            Birthday.setCustomerID(customerID);
            birthdaysReference.child(customerID).setValue(Birthday).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    synchronized (this){
                        if(processStatus < 2)
                            processStatus += 1;
                        else {
                            // jump to Main Activity
                            Intent intent = new Intent(feedback5_Activity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        } else {
            synchronized (this){
                if(processStatus < 2)
                    processStatus += 1;
                else {
                    // jump to Main Activity
                    Intent intent = new Intent(feedback5_Activity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }
        if(anniversarySelected) {
            // Upload Anniversary.
            Anniversary.setCustomerID(customerID);
            anniversariesReference.child(customerID).setValue(Anniversary).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    synchronized (this) {
                        if(processStatus < 2)
                            processStatus += 1;
                        else {
                            // Jump to main activity
                            Intent intent = new Intent(feedback5_Activity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        } else {
            synchronized (this) {
                if(processStatus < 2)
                    processStatus += 1;
                else {
                    // Jump to main activity
                    Intent intent = new Intent(feedback5_Activity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
    private void sendMessageViaSMS(String contact, String message) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact, null, message, null, null);
        } else {
            Toast.makeText(this, "Permission not granted !", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0 );
        }
    }
    private String getSMSTemplate(String customerName) {
        String firstName = customerName.trim().split(" ")[0];
        String template = "Hi "+firstName+"\nGreetings from Shimla Biryani. Thank you for dining with us, please do visit again :D !";
        return template;
    }


}
