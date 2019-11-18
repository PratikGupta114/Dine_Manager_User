package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class addRecordActivity extends AppCompatActivity {

    public static final String TAG = "addRecordActivity";

    private AutoCompleteTextView contactNumberEditText;
    private TextInputEditText    customerNameEditText, emailAddressEditText;
    private ImageView            numberOfSeatsIncrementButton, numberOfSeatsDecrementButton, backButton;
    private EditText             numberOfSeatsEditText, roomNumberEditText;
    private Button               submitRecordButton;
    private CustomDialog         customDialog;

    private int[] tagResArray = new int[]{R.id.tv2_tag1, R.id.tv2_tag2, R.id.tv2_tag3, R.id.tv2_tag4, R.id.tv2_tag5, R.id.tv2_tag6};
    private ArrayList<String>    tagsSelected = new ArrayList<>();
    private ArrayList<TextView>  tagViewList = new ArrayList<>();
    private List<String> crowdCategoryList;
    private Statistics   todaysStatistics;

    private String contactNumber;
    private String customerName;
    private String emailAddress;
    private String dateToday;
    private String timeNow;
    private int    numberOfSeats;
    private int    roomNumber;
    private int    ordersMade;
    private ArrayList<Customer> listOfCustomers;

    private FirebaseDatabase    firebaseDatabase;
    private DatabaseReference   databaseReference;
    private DatabaseReference   customersReference;

    private int processStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        crowdCategoryList = Arrays.asList(getResources().getStringArray(R.array.crowdCategory));
        setupView();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("customer_records").child(getCurrentDate());
        customersReference = firebaseDatabase.getReference().child("customers");
        customDialog = new CustomDialog(addRecordActivity.this);

        listOfCustomers = new ArrayList<>();

        firebaseDatabase.getReference().child("statistics").child(getCurrentDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todaysStatistics = dataSnapshot.getValue(Statistics.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        customersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    listOfCustomers.add(customerSnapshot.getValue(Customer.class));
                }
                Toast.makeText(addRecordActivity.this, "List Ready", Toast.LENGTH_SHORT).show();
                AutoCompleteCustomerAdapter adapter = new AutoCompleteCustomerAdapter(getApplicationContext(), listOfCustomers);
                contactNumberEditText.setAdapter(adapter);
                contactNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Customer customer = listOfCustomers.get(position);
                        Log.e(TAG, "Customer Selected : "+customer.getName());
                        customerNameEditText.setText(customer.getName());
                        Log.e(TAG, "Customer's contact : "+customer.getContact() );
                        contactNumberEditText.setText(customer.getContact());
                        Log.e(TAG, "Customer's email ID : "+customer.getEmailAddress());
                        emailAddressEditText.setText(customer.getEmailAddress());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        for(int i : tagResArray) {
            TextView textView = findViewById(i);
            textView.setTag(getResources().getString(R.string.unselected));
            tagViewList.add(textView);

        }
        for( TextView textView : tagViewList) {
            textView.setOnClickListener(new View.OnClickListener() {
                private boolean selected = false;
                @Override
                public void onClick(View v) {
                    TextView txtvw = (TextView) v;
                    //Toast.makeText(addRecordActivity.this, txtvw.getText().toString(), Toast.LENGTH_SHORT).show();
                    if(!selected) {
                        txtvw.setBackground(getDrawable(R.drawable.selected_tag));
                        txtvw.setTextColor(Color.parseColor("#ffffff"));
                        txtvw.setTag(getResources().getString(R.string.selected));
                    }
                    else {
                        txtvw.setBackground(getDrawable(R.drawable.unselected_tag));
                        txtvw.setTextColor(Color.parseColor("#000000"));
                        txtvw.setTag(getResources().getString(R.string.unselected));
                    }
                    selected = !selected;
                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecordActivity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_BACK));
                addRecordActivity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP , KeyEvent.KEYCODE_BACK));
            }
        });
        submitRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        numberOfSeatsIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(numberOfSeatsEditText.getText().toString().trim());
                if(value >= 30)
                    value = 30;
                else
                    value++;
                numberOfSeatsEditText.setText(String.valueOf(value));
            }
        });
        numberOfSeatsDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(numberOfSeatsEditText.getText().toString().trim());
                if(value <= 1)
                    value = 1;
                else
                    value--;
                numberOfSeatsEditText.setText(String.valueOf(value));
            }
        });
        numberOfSeatsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(numberOfSeatsEditText.getText().toString().trim().equals("")){
                        numberOfSeatsEditText.setText(String.valueOf(1));
                    }
                }
            }
        });
        roomNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(roomNumberEditText.getText().toString().trim().equals("")){
                        roomNumberEditText.setText(String.valueOf(1));
                    }
                }
            }
        });
        /*crowdCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(addRecordActivity.this);
        builder.setMessage("Going back will clear the form.")
                .setPositiveButton("Go back", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setNegativeButton("Stay",null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setupView() {
        contactNumberEditText = findViewById(R.id.actv2_mobileNumberInput);
        customerNameEditText = findViewById(R.id.actv2_customerName);
        emailAddressEditText = findViewById(R.id.actv2_emailAddress);
        numberOfSeatsIncrementButton = findViewById(R.id.imv2_numberOfSeatsIncrementButton);
        numberOfSeatsDecrementButton = findViewById(R.id.imv2_numberOfSeatsDecrementButton);
        numberOfSeatsEditText = findViewById(R.id.et2_numberOfSeats);
        roomNumberEditText = findViewById(R.id.et2_roomNumber);
        submitRecordButton = findViewById(R.id.bt2_submitButton);
        backButton = findViewById(R.id.imv2_backButton);

    }
    public void submitForm() {

        contactNumber = contactNumberEditText.getText().toString().trim();
        customerName = customerNameEditText.getText().toString().trim();
        emailAddress = emailAddressEditText.getText().toString().trim();
        numberOfSeats = Integer.parseInt(numberOfSeatsEditText.getText().toString().trim());
        roomNumber = Integer.parseInt(roomNumberEditText.getText().toString().trim());
        dateToday = getCurrentDate();
        timeNow = getCurrentTime();

        if(contactNumber.isEmpty()) {
            contactNumberEditText.setError("Contact Number Required");
            contactNumberEditText.requestFocus();
        }
        else if(!Patterns.PHONE.matcher(contactNumber).matches()) {
            contactNumberEditText.setError("Invalid Contact Number");
            contactNumberEditText.requestFocus();
        }
        else if(!(contactNumber.length() >= 6 && contactNumber.length() <= 12)) {
            contactNumberEditText.setError("Invalid Contact Number");
            contactNumberEditText.requestFocus();
        }
        else if(customerName.isEmpty() || customerName.trim().length() < 3) {
            customerNameEditText.setError("Enter a Valid Name");
            customerNameEditText.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            emailAddressEditText.setError("Enter a Valid Email Address");
        }
        else {

            // Preparing the record.
            for (TextView textView : tagViewList) {
                String t = (String) textView.getTag();
                if (t.equals(getResources().getString(R.string.selected))) {
                    String tag = textView.getText().toString().trim();
                    tagsSelected.add(tag);
                    Log.i(TAG, tag);
                }
            }

            customDialog.showProgressBar();

            if(!deviceIsOnline()) {
                customDialog.hideProgressdialog();
                Toast.makeText(this, "Device is Offline", Toast.LENGTH_SHORT).show();
                return;
            }

            Query customerQuery = customersReference.orderByChild("contact").equalTo(contactNumber);
            customerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {

                        Customer thisCustomer = null;
                        // if the customer exists.
                        for(DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                            thisCustomer = customerSnapshot.getValue(Customer.class);
                        }
                        if(thisCustomer == null) {
                            Log.e(TAG, "thisCustomer is null");
                            return;
                        }
                        else {
                            Log.e(TAG, "Customer ID : " + thisCustomer.getCustomerID());
                            Log.e(TAG, "Customer Name : "+thisCustomer.getName());
                            Log.e(TAG, "Customer Contact : "+thisCustomer.getContact());
                            Log.e(TAG, "Customer Visited "+thisCustomer.getNumberOfVisits()+" times before");
                        }

                        CustomerRecord record = new CustomerRecord(customerName, contactNumber);
                        record.setEmailAddress(emailAddress);
                        record.setRoomNumber(Integer.toString(roomNumber));
                        record.setSeatsBooked(Integer.toString(numberOfSeats));
                        record.setTags(tagsSelected);
                        record.setDate(dateToday);
                        record.setTime(timeNow);
                        record.setCustomerID(thisCustomer.getCustomerID());
                        record.setVisit(thisCustomer.getNumberOfVisits()+1);


                        thisCustomer.setNumberOfVisits( thisCustomer.getNumberOfVisits() + 1 );
                        thisCustomer.setTotalSeatsBooked( thisCustomer.getTotalSeatsBooked() + 1);
                        thisCustomer.setLastVisitedOn(getCurrentDate()+" "+getCurrentTime());
                        String customerID = thisCustomer.getCustomerID();


                        uploadRecord(record);

                        customersReference.child(customerID).setValue(thisCustomer)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(addRecordActivity.this, "Customer Added", Toast.LENGTH_SHORT).show();
                                        synchronized (this) {
                                            if(processStatus+1 == 2){
                                                customDialog.hideProgressdialog();
                                                finish();
                                            }
                                            else
                                                processStatus += 1;
                                        }
                                    }
                                });
                    }
                    else {


                        CustomerRecord record = new CustomerRecord(customerName, contactNumber);
                        record.setEmailAddress(emailAddress);
                        record.setRoomNumber(Integer.toString(roomNumber));
                        record.setSeatsBooked(Integer.toString(numberOfSeats));
                        record.setTags(tagsSelected);
                        record.setDate(dateToday);
                        record.setTime(timeNow);
                        record.setVisit(1);


                        // if its a new customer.
                        Customer newCustomer = new Customer();
                        String customerID = customersReference.push().getKey();

                        record.setCustomerID(customerID);

                        newCustomer.setName(customerName);
                        newCustomer.setContact(contactNumber);
                        newCustomer.setEmailAddress(emailAddress);
                        newCustomer.setNumberOfVisits(1);
                        newCustomer.setTotalSeatsBooked(numberOfSeats);
                        newCustomer.setCustomerID(customerID);
                        newCustomer.setLastVisitedOn(getCurrentDate()+" "+getCurrentTime());

                        uploadRecord(record);

                        customersReference.child(customerID).setValue(newCustomer)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(addRecordActivity.this, "Customer Added", Toast.LENGTH_SHORT).show();
                                        synchronized (this) {
                                            if(processStatus+1 == 2){
                                                customDialog.hideProgressdialog();
                                                finish();
                                            }
                                            else
                                                processStatus += 1;
                                        }
                                    }
                                });
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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

    private void uploadRecord(CustomerRecord record) {
        // creating the reference for the record and adding data to it.
        String recordID = databaseReference.push().getKey();
        record.setRecordID(recordID);
        databaseReference.child(recordID).setValue(record)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Update the Statistics
                        todaysStatistics.setNumberOfVisits(todaysStatistics.getNumberOfVisits()+1);
                        todaysStatistics.setNumberOfSeatsBooked(todaysStatistics.getNumberOfSeatsBooked()+numberOfSeats);
                        DatabaseReference statisticsReference = firebaseDatabase.getReference().child("statistics").child(getCurrentDate());
                        statisticsReference.setValue(todaysStatistics)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(addRecordActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();
                                        synchronized (this) {
                                            if(processStatus+1 == 2){
                                                customDialog.hideProgressdialog();
                                                finish();
                                            }
                                            else
                                                processStatus += 1;
                                        }
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customDialog.hideProgressdialog();
                        Toast.makeText(addRecordActivity.this, "Failed to save record.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
