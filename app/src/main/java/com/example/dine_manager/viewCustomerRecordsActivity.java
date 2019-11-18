package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class viewCustomerRecordsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "viewCustomerRecords";

    private final int INF = -1;

    private RecyclerView     allCustomerRecordsList;
    private ImageView        backButton;
    private CustomDialog     customDialog;
    private TextView         nothingToDisplay, dateDisplayText;
    private Toolbar          toolbar;

    private DatabaseReference recordsReference;
    private FirebaseDatabase firebaseDatabase;
    private CustomerRecordsAdapter   customerRecordsAdapter;
    private ArrayList<CustomerRecord> customerRecords;
    private String dateSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer_records);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));
        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getActionBar().setDisplayShowTitleEnabled(false);

        itemOffsetDecoration itemDecoration = new itemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);

        nothingToDisplay = findViewById(R.id.tv7_nothingToDisplay);
        dateDisplayText = findViewById(R.id.tv7_recordDate);
        allCustomerRecordsList = findViewById(R.id.rv7_allCustomerRecords);
        allCustomerRecordsList.setLayoutManager(new LinearLayoutManager(viewCustomerRecordsActivity.this));
        allCustomerRecordsList.addItemDecoration(itemDecoration);
        customerRecords = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        recordsReference = firebaseDatabase.getReference().child("customer_records");

        customDialog = new CustomDialog(this);
        customDialog.showProgressBar();

        dateSelected = getCurrentDate();
        dateDisplayText.setText("( "+dateSelected+" )");

        backButton = findViewById(R.id.imv7_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordsReference.child(dateSelected).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                customerRecords.clear();
                for(DataSnapshot recordSnapShot : dataSnapshot.getChildren()) {
                    customerRecords.add(recordSnapShot.getValue(CustomerRecord.class));
                }
                Log.e(TAG, "Total records fetched : "+customerRecords.size());
                customDialog.hideProgressdialog();

                if(customerRecords.isEmpty()) {
                    allCustomerRecordsList.setVisibility(View.GONE);
                    nothingToDisplay.setVisibility(View.VISIBLE);
                } else {
                    nothingToDisplay.setVisibility(View.GONE);
                    allCustomerRecordsList.setVisibility(View.VISIBLE);
                    Collections.reverse(customerRecords);
                    customerRecordsAdapter = new CustomerRecordsAdapter(getApplicationContext(), customerRecords);
                    allCustomerRecordsList.setAdapter(customerRecordsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tbm9_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.tbm9_menuItemSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);
        searchEditText.setCompoundDrawableTintList(ColorStateList.valueOf(Color.WHITE));
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customerRecordsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.tbm9_visitFilter1 :
                applyFilter(1,3, dateSelected);
                return true;
            case R.id.tbm9_visitFilter2 :
                applyFilter(3,6, dateSelected);
                return true;
            case R.id.tbm9_visitFilter3 :
                applyFilter(6,10, dateSelected);
                return true;
            case R.id.tbm9_visitFilter4 :
                applyFilter(10, INF, dateSelected);
                return true;
            case R.id.tbm9_visitFilter5 :
                applyFilter(1, INF, dateSelected);
                return true;
            case R.id.tbm9_dateFilter :
                DialogFragment anniversaryPicker = new DatePickerFragment();
                anniversaryPicker.show(getSupportFragmentManager(), "Record Date Picker");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyFilter(int startVisitValue, int endVisitValue, String dateSelected) {
        customDialog.showProgressBar();

        Query filteredRecordQuery;

        if(startVisitValue == 1 && endVisitValue == INF) {
            filteredRecordQuery = recordsReference.child(dateSelected);
        }
        else if (startVisitValue == 10 && endVisitValue == INF) {
            filteredRecordQuery = recordsReference.child(dateSelected)
                    .orderByChild("visit")
                    .startAt(startVisitValue);
        } else {
            filteredRecordQuery = recordsReference.child(dateSelected)
                    .orderByChild("visit")
                    .startAt(startVisitValue)
                    .endAt(endVisitValue);
        }

        filteredRecordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<CustomerRecord> filteredList = new ArrayList<>();
                for(DataSnapshot recordSnapShot : dataSnapshot.getChildren()) {
                    filteredList.add(recordSnapShot.getValue(CustomerRecord.class));
                }
                if(filteredList.isEmpty()) {
                    nothingToDisplay.setVisibility(View.VISIBLE);
                    allCustomerRecordsList.setVisibility(View.GONE);
                    customDialog.hideProgressdialog();
                } else {
                    nothingToDisplay.setVisibility(View.GONE);
                    allCustomerRecordsList.setVisibility(View.VISIBLE);
                    CustomerRecordsAdapter filteredRecordsAdapter = new CustomerRecordsAdapter(getApplicationContext(), filteredList);
                    allCustomerRecordsList.setAdapter(filteredRecordsAdapter);
                    customDialog.hideProgressdialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                customDialog.hideProgressdialog();
                Toast.makeText(viewCustomerRecordsActivity.this, "Couldn't fetch data", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String date = decimalFormat.format(dayOfMonth)+"-"+decimalFormat.format((month+1))+"-"+decimalFormat.format(year);
        dateSelected = date;
        dateDisplayText.setText("( "+dateSelected+" )");

        recordsReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CustomerRecord> filteredList = new ArrayList<>();
                for(DataSnapshot recordSnapShot : dataSnapshot.getChildren()) {
                    filteredList.add(recordSnapShot.getValue(CustomerRecord.class));
                }
                if(filteredList.isEmpty()) {
                    nothingToDisplay.setVisibility(View.VISIBLE);
                    allCustomerRecordsList.setVisibility(View.GONE);
                    customDialog.hideProgressdialog();
                } else {
                    nothingToDisplay.setVisibility(View.GONE);
                    allCustomerRecordsList.setVisibility(View.VISIBLE);
                    CustomerRecordsAdapter filteredRecordsAdapter = new CustomerRecordsAdapter(getApplicationContext(), filteredList);
                    allCustomerRecordsList.setAdapter(filteredRecordsAdapter);
                    customDialog.hideProgressdialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
