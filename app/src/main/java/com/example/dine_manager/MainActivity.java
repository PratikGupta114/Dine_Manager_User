package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FloatingActionButton floatingActionButton;

    private LinearLayout    viewFeedbacks, viewRecords, newFeedback, dashBoard, upcomingEvents;

    private FirebaseDatabase   firebaseDatabase;
    private DatabaseReference  statisticsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        setupViews();
        setupToolbar();
        setupNavigationDrawer();

        firebaseDatabase = FirebaseDatabase.getInstance();
        statisticsReference = firebaseDatabase.getReference().child("statistics");
        statisticsReference.child(getCurrentDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Statistics todaysStatistics = dataSnapshot.getValue(Statistics.class);
                if(todaysStatistics == null) {

                    Log.e(TAG, "today's statistics is null, hence creating one");
                    todaysStatistics = new Statistics();
                    todaysStatistics.setNumberOfVisits(0);
                    todaysStatistics.setNumberOfSeatsBooked(0);
                    todaysStatistics.setExperienceRatings(getEmptyIntegerList(10));
                    todaysStatistics.setFoodRatings(getEmptyIntegerList(5));
                    todaysStatistics.setServiceRatings(getEmptyIntegerList(10));

                    statisticsReference.child(getCurrentDate()).setValue(todaysStatistics);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(!deviceIsOnline())
            Toast.makeText(this, "You are Offline\nNo Data retrieved", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure to Exit ?")
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
    }
    @Override
    protected void onStart() {
        super.onStart();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addRecordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewFeedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, viewFeedbackRecordsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, viewCustomerRecordsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        newFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, feedBack0_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        dashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        upcomingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, eventsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    public void setupViews() {
        floatingActionButton = findViewById(R.id.fab_1);

        viewFeedbacks = findViewById(R.id.viewFeedbackLayout);
        viewRecords = findViewById(R.id.viewCustomerRecordLayout);
        newFeedback = findViewById(R.id.newFeedbackLayout);
        dashBoard = findViewById(R.id.dashboardLayout);
        upcomingEvents = findViewById(R.id.upcomingEventsLayout);

    }
    public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigration_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.navigation_icon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    public void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.menu_customerDiary :
                    {
                        if(deviceIsOnline()) {
                            Intent intent = new Intent(MainActivity.this, addRecordActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.menu_setFeedback :
                    {
                        if(deviceIsOnline()) {
                            Intent intent = new Intent(MainActivity.this, feedBack0_Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.menu_viewCustomerData :
                    {
                        if(deviceIsOnline()) {
                            Intent intent = new Intent(MainActivity.this, viewCustomerRecordsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.menu_viewFeedback :
                    {
                        if(deviceIsOnline()) {
                            Intent intent = new Intent(MainActivity.this, viewFeedbackRecordsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.menu_dashBoard :
                    {
                        if(deviceIsOnline()) {
                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.menu_upcomingEvents :
                    {
                        if(deviceIsOnline()) {
                            Intent intent = new Intent(MainActivity.this, eventsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                return false;
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
    private ArrayList<Integer> getEmptyIntegerList(int size) {
        ArrayList<Integer> emptyList = new ArrayList<Integer>();
        for(int i=1; i<=size; i++){
            emptyList.add(0);
        }
        return emptyList;
    }

}
