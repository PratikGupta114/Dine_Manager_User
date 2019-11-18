package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class feedBack0_Activity extends AppCompatActivity {

    private static final String TAG = "feedback0_Activity";
    private AutoCompleteTextView    contactNumberEditText;
    private ImageView               backButton;
    private Button                  doneButton;

    private FirebaseDatabase        firebaseDatabase;
    private DatabaseReference       customersReference;
    private ArrayList<Customer>     listOfAllCustomers;
    private Customer                thisCustomer;
    private CustomDialog            customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback0);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        firebaseDatabase = FirebaseDatabase.getInstance();
        customersReference = firebaseDatabase.getReference().child("customers");
        customDialog = new CustomDialog(feedBack0_Activity.this);

        listOfAllCustomers = new ArrayList<>();
        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResponse();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack0_Activity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_BACK));
                feedBack0_Activity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP , KeyEvent.KEYCODE_BACK));
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(feedBack0_Activity.this);
        builder.setMessage("Are you sure to leave ?.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setNegativeButton("No",null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        customersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfAllCustomers.clear();
                for(DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    listOfAllCustomers.add(customerSnapshot.getValue(Customer.class));
                }
                AutoCompleteCustomerAdapter adapter = new AutoCompleteCustomerAdapter(getApplicationContext(), listOfAllCustomers);
                contactNumberEditText.setAdapter(adapter);
                contactNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        thisCustomer = new Customer(listOfAllCustomers.get(position));
                        contactNumberEditText.setText(thisCustomer.getContact());

                        /*Log.e(TAG, "Customer Selected : "+customer.getName());
                        customerNameEditText.setText(customer.getName());
                        Log.e(TAG, "Customer's contact : "+customer.getContact() );

                        Log.e(TAG, "Customer's email ID : "+customer.getEmailAddress());
                        emailAddressEditText.setText(customer.getEmailAddress());*/
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupViews() {
        contactNumberEditText = findViewById(R.id.actv11_mobileNumberInput);
        doneButton = findViewById(R.id.bt11_doneButton);
        backButton = findViewById(R.id.imv11_backButton);
    }

    private void submitResponse() {

        final String cn = contactNumberEditText.getText().toString().trim();

        if(thisCustomer == null || cn.isEmpty()) {
            //Toast.makeText(this, "No Customer Selected", Toast.LENGTH_SHORT).show();
            customDialog.showDialogForTwoInputs();
            customDialog.setOnInputsSetListener(new CustomDialog.OnInputsSetListener() {
                @Override
                public void onInputsSet(String name, String emailAddress) {
                    Intent intent = new Intent(feedBack0_Activity.this, feedBack1_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("CUSTOMER_NAME", name);
                    intent.putExtra("CUSTOMER_CONTACT", cn);
                    intent.putExtra("CUSTOMER_EMAIL", emailAddress);
                    FeedbackRecord.setCustomerName(name);
                    FeedbackRecord.setCustomerContact(cn);
                    if(!emailAddress.isEmpty() )
                        FeedbackRecord.setEmailAddress(emailAddress);
                    else
                        FeedbackRecord.setEmailAddress("-- NA --");
                    //customDialog.hideProgressdialog();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            return;
        }
        else if(!(cn.equals(thisCustomer.getContact() ))) {
            Toast.makeText(this, "Customer not found !\nPlease enter a new record", Toast.LENGTH_SHORT).show();
            return;
        }
            Intent intent = new Intent(feedBack0_Activity.this, feedBack1_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("CUSTOMER_NAME", thisCustomer.getName());
            intent.putExtra("CUSTOMER_CONTACT", thisCustomer.getContact());
            intent.putExtra("CUSTOMER_ID", thisCustomer.getCustomerID());
            intent.putExtra("CUSTOMER_EMAIL", thisCustomer.getEmailAddress());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
