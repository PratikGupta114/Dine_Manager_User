package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.Inflater;

public class eventsActivity extends AppCompatActivity {

    private static final String TAG = "eventsActivity";

    private static final int THIS_WEEK_BIRTHDAYS = -1;
    private static final int THIS_MONTH_ANNIVERSARY = -2;
    private static final int THIS_MONTH_BIRTHDAYS = -3;
    private static final int THIS_WEEK_ANNIVERSARY = -4;

    private static final int PAST_BIRTHDAYS = -5;
    private static final int PAST_ANNIVERSARIES = -6;
    private static final int ALL_BIRTHDAYS = -7;
    private static final int ALL_ANNIVERSARIES = -8;

    private ImageView backButton;
    private Toolbar toolbar;
    private TextView eventTypeDisplay, eventsDateDisplay, nothingToDisplay;
    private RecyclerView    listOfEventsDisplay;
    private EventsAdapter   listOfEventsAdapter;
    private CustomDialog    customDialog;

    private FirebaseDatabase    firebaseDatabase;
    private DatabaseReference   birthdaysReference, anniversariesReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        firebaseDatabase = FirebaseDatabase.getInstance();
        birthdaysReference = firebaseDatabase.getReference().child("birthdays");
        anniversariesReference = firebaseDatabase.getReference().child("anniversaries");

        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsActivity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_BACK));
                eventsActivity.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP , KeyEvent.KEYCODE_BACK));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilter(ALL_BIRTHDAYS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.events_activity_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.R_id_thisWeekBirthdaysOption :
                applyFilter(THIS_WEEK_BIRTHDAYS);
                return true;
            case R.id.R_id_thisMonthBirthdaysOption :
                applyFilter(THIS_MONTH_BIRTHDAYS);
                return true;
            case R.id.R_id_thisWeekAnniversariesOption :
                applyFilter(THIS_WEEK_ANNIVERSARY);
                return true;
            case R.id.R_id_thisMonthAnniversariesOption :
                applyFilter(THIS_MONTH_ANNIVERSARY);
                return true;
            case R.id.R_id_allAnniversariesOption :
                applyFilter(ALL_ANNIVERSARIES);
                return true;
            case R.id.R_id_allBirthdaysOption :
                applyFilter(ALL_BIRTHDAYS);
                return true;
            case R.id.sendMessageToAllOption :
                final ArrayList<String> contactsList = new ArrayList<>(listOfEventsAdapter.getContactNumbers());
                if(contactsList.isEmpty()) {
                    Toast.makeText(this, "No Contacts Available", Toast.LENGTH_SHORT).show();
                    return true;
                }
                customDialog.showDialogForMessageInput("[ Multiple Customers ]");
                customDialog.setOnMessageEnteredListener(new CustomDialog.OnMessageEnteredListener() {
                    @Override
                    public void onMessageEntered(String Message) {
                        for(String contact : contactsList) {
                            sendMessageViaSMS(contact, Message);
                        }
                        Toast.makeText(eventsActivity.this, "Messages Sent", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
         }
        return true;
    }

    private void applyFilter(final int filter) {

        customDialog.showProgressBar();
        Query eventsQuery;
        Calendar calendar = Calendar.getInstance();
        final int numberOfDaysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Log.e(TAG, "Number of days in this month : "+numberOfDaysInCurrentMonth);
        switch (filter) {
            case THIS_WEEK_BIRTHDAYS :
                if(getCurrentDayOfMonth()+7 > numberOfDaysInCurrentMonth)
                    eventsQuery = birthdaysReference.orderByChild("month").equalTo(getCurrentMonth());
                else
                    eventsQuery = birthdaysReference.orderByChild("month").startAt(getCurrentMonth()).endAt( (getCurrentMonth()+1)%12 );
                break;
            case THIS_WEEK_ANNIVERSARY :
                if(getCurrentDayOfMonth()+7 > numberOfDaysInCurrentMonth)
                    eventsQuery = anniversariesReference.orderByChild("month").equalTo(getCurrentMonth());
                else
                    eventsQuery = anniversariesReference.orderByChild("month").startAt(getCurrentMonth()).endAt( (getCurrentMonth()+1)%12 );
                break;
            case THIS_MONTH_BIRTHDAYS :
                eventsQuery = birthdaysReference.orderByChild("month").equalTo(getCurrentMonth());
                break;
            case THIS_MONTH_ANNIVERSARY :
                eventsQuery = anniversariesReference.orderByChild("month").equalTo(getCurrentMonth());
                break;
            case ALL_ANNIVERSARIES :
                eventsQuery = anniversariesReference.orderByChild("month");
                break;
            case ALL_BIRTHDAYS :
                eventsQuery = birthdaysReference.orderByChild("month");
                break;
            default:
                eventsQuery = null;
        }

        if(eventsQuery != null) {
            eventsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ArrayList<OEvent> events = new ArrayList<>();
                    int eventType = 0;

                    switch (filter) {
                        case THIS_WEEK_BIRTHDAYS: {
                            eventType = THIS_WEEK_BIRTHDAYS;
                            eventTypeDisplay.setText("Birthdays");
                            eventsDateDisplay.setText("( This Week )");
                            if (getCurrentDayOfMonth() + 7 > 31) {
                                int nextWeekDate = (getCurrentDayOfMonth() + 7) % numberOfDaysInCurrentMonth;
                                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                    OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                    assert thisEvent != null;
                                    if ((thisEvent.getMonth() == getCurrentMonth() && thisEvent.getDayOfMonth() >= getCurrentDayOfMonth()) || (thisEvent.getDayOfMonth() == getCurrentMonth() + 1 && thisEvent.getDayOfMonth() <= nextWeekDate))
                                        events.add(thisEvent);
                                }
                            } else {
                                int nextWeekDate = getCurrentDayOfMonth() + 7;
                                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                    OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                    assert thisEvent != null;
                                    if (thisEvent.getDayOfMonth() >= getCurrentDayOfMonth() && thisEvent.getDayOfMonth() <= nextWeekDate)
                                        events.add(thisEvent);
                                }
                            }
                        }
                        break;
                        case THIS_WEEK_ANNIVERSARY: {
                            eventType = THIS_WEEK_ANNIVERSARY;
                            eventTypeDisplay.setText("Anniveraries");
                            eventsDateDisplay.setText("( This Week )");
                            if (getCurrentDayOfMonth() + 7 > 31) {
                                int nextWeekDate = (getCurrentDayOfMonth() + 7) % numberOfDaysInCurrentMonth;
                                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                    OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                    assert thisEvent != null;
                                    if ((thisEvent.getMonth() == getCurrentMonth() && thisEvent.getDayOfMonth() >= getCurrentDayOfMonth()) || (thisEvent.getDayOfMonth() == getCurrentMonth() + 1 && thisEvent.getDayOfMonth() <= nextWeekDate))
                                        events.add(thisEvent);
                                }
                            } else {
                                int nextWeekDate = getCurrentDayOfMonth() + 7;
                                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                    OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                    assert thisEvent != null;
                                    if (thisEvent.getDayOfMonth() >= getCurrentDayOfMonth() && thisEvent.getDayOfMonth() <= nextWeekDate)
                                        events.add(thisEvent);
                                }
                            }
                        }
                        break;
                        case THIS_MONTH_BIRTHDAYS: {
                            eventType = THIS_MONTH_BIRTHDAYS;
                            eventTypeDisplay.setText("Birthdays");
                            eventsDateDisplay.setText("( This Month )");
                            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                assert thisEvent != null;
                                if (thisEvent.getDayOfMonth() >= getCurrentDayOfMonth())
                                    events.add(thisEvent);
                            }
                        }
                        break;
                        case THIS_MONTH_ANNIVERSARY : {
                            eventType = THIS_MONTH_ANNIVERSARY;
                            eventTypeDisplay.setText("Anniversaries");
                            eventsDateDisplay.setText("( This Month )");
                            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                assert thisEvent != null;
                                if (thisEvent.getDayOfMonth() >= getCurrentDayOfMonth())
                                    events.add(thisEvent);
                            }
                        }
                        break;
                        case ALL_BIRTHDAYS : {
                            eventType = ALL_BIRTHDAYS;
                            eventTypeDisplay.setText("Birthdays");
                            eventsDateDisplay.setText("( All )");
                            for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                OEvent thisEvent = eventSnapshot.getValue(OEvent.class);
                                events.add(thisEvent);
                            }
                        }
                        break;
                        case ALL_ANNIVERSARIES : {
                            eventType = ALL_ANNIVERSARIES;
                            eventTypeDisplay.setText("Anniversaries");
                            eventsDateDisplay.setText("( All )");
                            for(DataSnapshot eventsSnapshot : dataSnapshot.getChildren())
                                events.add(eventsSnapshot.getValue(OEvent.class));
                        }
                        break;
                    }

                    customDialog.hideProgressdialog();
                    if(events.isEmpty()) {
                        nothingToDisplay.setVisibility(View.VISIBLE);
                        listOfEventsDisplay.setVisibility(View.GONE);
                    } else {
                        nothingToDisplay.setVisibility(View.GONE);
                        listOfEventsDisplay.setVisibility(View.VISIBLE);

                        if(eventType == THIS_WEEK_BIRTHDAYS || eventType == THIS_MONTH_BIRTHDAYS || eventType == ALL_BIRTHDAYS)
                            listOfEventsAdapter = new EventsAdapter(getApplicationContext(), eventsActivity.this, events, EventsAdapter.EVENT_TYPE_BIRTHDAY);
                        else
                            listOfEventsAdapter = new EventsAdapter(getApplicationContext(), eventsActivity.this, events, EventsAdapter.EVENT_TYPE_ANNIVERSARY);

                        listOfEventsDisplay.setAdapter(listOfEventsAdapter);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    customDialog.hideProgressdialog();
                    Toast.makeText(eventsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "eventsQuery is null");
            nothingToDisplay.setVisibility(View.VISIBLE);
            listOfEventsDisplay.setVisibility(View.GONE);
        }
    }

    private void setupViews() {
        backButton = findViewById(R.id.imv14_backButton);
        toolbar = findViewById(R.id.toolbar14);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        itemOffsetDecoration itemDecoration = new itemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);

        listOfEventsDisplay = findViewById(R.id.rv14_listOfEvents);
        listOfEventsDisplay.setLayoutManager(new LinearLayoutManager(eventsActivity.this));
        listOfEventsDisplay.addItemDecoration(itemDecoration);

        nothingToDisplay = findViewById(R.id.tv14_nothingToDisplay);
        eventTypeDisplay = findViewById(R.id.tv14_eventTypeDisplay);
        eventsDateDisplay = findViewById(R.id.tv14_eventsDateDisplay);

        customDialog = new CustomDialog(eventsActivity.this);
    }

    private int getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth;
    }
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return month+1;
    }

    private String getCurrentDate()
    {
        String dateToday ;
        Date date  = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateToday = dateFormat.format(date);

        return dateToday;
    }
    private void sendMessageViaSMS(String contact, String message) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact, null, message, null, null);
        } else {
            Toast.makeText(this, "Permission not Granted !", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.SEND_SMS}, 0 );
        }
    }

}
