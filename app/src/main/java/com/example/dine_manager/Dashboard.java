package com.example.dine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dashboard extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "Dashboard";

    private ImageView backButton;
    private Toolbar toolbar;
    private TextView visitsCountTextView, seatsCountTextView, nothingToDisplay, dateSelectedView;
    private PieChart pieChart;
    private ArrayList<Integer> coloursList;
    private LinearLayout    statisticsLayout;
    private String dateSelected;

    private FirebaseDatabase    firebaseDatabase;
    private DatabaseReference   statisticsReference;
    private Statistics          todaysStatistics;
    private BarChart            foodRatingsBarGraph, serviceRatingsBarGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        toolbar = findViewById(R.id.toolbar15);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        statisticsReference = firebaseDatabase.getReference().child("statistics");

        setupViews();

        dateSelected = getCurrentDate();
        dateSelectedView.setText("( "+dateSelected+" )");
    }

    @Override
    protected void onStart() {
        super.onStart();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_BACK));
                Dashboard.super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP , KeyEvent.KEYCODE_BACK));
            }
        });

        statisticsReference.child(getCurrentDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todaysStatistics = dataSnapshot.getValue(Statistics.class);
                if(todaysStatistics == null) {

                    Log.e(TAG, "today's statistics is null, hence creating one");
                    todaysStatistics = new Statistics();
                    todaysStatistics.setNumberOfVisits(0);
                    todaysStatistics.setNumberOfSeatsBooked(0);
                    todaysStatistics.setExperienceRatings(getEmptyIntegerList(10));
                    todaysStatistics.setFoodRatings(getEmptyIntegerList(5));
                    todaysStatistics.setServiceRatings(getEmptyIntegerList(10));

                    statisticsReference.child(getCurrentDate()).setValue(todaysStatistics);

                } else {

                    Log.e(TAG, "today's statistics isn't null ");

                }

                startCountAnimation(visitsCountTextView, todaysStatistics.getNumberOfVisits(), 1000);
                startCountAnimation(seatsCountTextView, todaysStatistics.getNumberOfSeatsBooked(), 1000);

                ArrayList<Integer> experienceRatings = todaysStatistics.getExperienceRatings();
                ArrayList<Integer> foodRatings = todaysStatistics.getFoodRatings();
                ArrayList<Integer> serviceRatings = todaysStatistics.getServiceRatings();


                ArrayList<PieEntry> ratingValues = new ArrayList<>();
                ArrayList<BarEntry> foodRatingVaues = new ArrayList<>();
                ArrayList<BarEntry> serviceRatingValues = new ArrayList<>();

                float counter = 0.0f;
                for(int i=0 ; i<experienceRatings.size() ; i++) {
                    counter += 0.5;
                    ratingValues.add(new PieEntry(experienceRatings.get(i) , counter+" rating"));
                    Log.e(TAG , "\n"+counter+" rating : "+experienceRatings.get(i));
                }

                for(int i=0 ; i<foodRatings.size() ; i++) {
                    foodRatingVaues.add(new BarEntry(  (i+1), foodRatings.get(i) ));
                    Log.e(TAG , "\n"+counter+" rating : "+foodRatings.get(i));
                }

                counter = 0.0f;
                for(int i=0 ; i<serviceRatings.size() ; i++) {
                    counter += 0.5;
                    serviceRatingValues.add(new BarEntry(  (i+1), serviceRatings.get(i)));
                    Log.e(TAG , "\n"+counter+" rating : "+serviceRatings.get(i));
                }

                preparePieChart(ratingValues);
                prepareBarGraphForFoodRatings(foodRatingVaues);
                prepareBarGraphForServiceRatings(serviceRatingValues);
                pieChart.animateX(1000, Easing.EaseInOutCubic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashbaord_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashBoardMenu_selectDate :
                DialogFragment anniversaryPicker = new DatePickerFragment();
                anniversaryPicker.show(getSupportFragmentManager(), "Statistics Date Picker");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String date = decimalFormat.format(dayOfMonth)+"-"+decimalFormat.format((month+1))+"-"+decimalFormat.format(year);
        dateSelected = date;
        dateSelectedView.setText(date);
        applyFilter(dateSelected);
    }

    private void applyFilter(String date){
        statisticsReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Statistics thisDayStatistics = dataSnapshot.getValue(Statistics.class);
                if(thisDayStatistics == null) {
                    statisticsLayout.setVisibility(View.GONE);
                    nothingToDisplay.setVisibility(View.VISIBLE);
                } else {
                    statisticsLayout.setVisibility(View.VISIBLE);
                    nothingToDisplay.setVisibility(View.GONE);

                    startCountAnimation(visitsCountTextView, thisDayStatistics.getNumberOfVisits(), 1000);
                    startCountAnimation(seatsCountTextView, thisDayStatistics.getNumberOfSeatsBooked(), 1000);

                    ArrayList<Integer> experienceRatings = thisDayStatistics.getExperienceRatings();
                    ArrayList<Integer> foodRatings = thisDayStatistics.getFoodRatings();
                    ArrayList<Integer> serviceRatings = thisDayStatistics.getServiceRatings();

                    ArrayList<PieEntry> ratingValues = new ArrayList<>();
                    ArrayList<BarEntry> foodRatingVaues = new ArrayList<>();
                    ArrayList<BarEntry> serviceRatingValues = new ArrayList<>();

                    float counter = 0.0f;
                    for(int i=0 ; i<experienceRatings.size() ; i++) {
                        counter += 0.5;
                        ratingValues.add(new PieEntry(experienceRatings.get(i) , counter+" rating"));
                        Log.e(TAG , "\n"+counter+" rating : "+experienceRatings.get(i));
                    }

                    for(int i=0 ; i<foodRatings.size() ; i++) {
                        foodRatingVaues.add(new BarEntry(  (i+1), foodRatings.get(i) ));
                        Log.e(TAG , "\n"+counter+" rating : "+foodRatings.get(i));
                    }

                    counter = 0.0f;
                    for(int i=0 ; i<serviceRatings.size() ; i++) {
                        counter += 0.5;
                        serviceRatingValues.add(new BarEntry(  (i+1), serviceRatings.get(i)));
                        Log.e(TAG , "\n"+counter+" rating : "+serviceRatings.get(i));
                    }

                    preparePieChart(ratingValues);
                    prepareBarGraphForFoodRatings(foodRatingVaues);
                    prepareBarGraphForServiceRatings(serviceRatingValues);
                    pieChart.notifyDataSetChanged();
                    pieChart.animateX(1000, Easing.EaseInOutCubic);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setupViews() {
        backButton = findViewById(R.id.imv15_backButton);
        toolbar = findViewById(R.id.toolbar15);
        seatsCountTextView = findViewById(R.id.tv16_seatsCount);
        visitsCountTextView = findViewById(R.id.tv16_visitsCount);

        nothingToDisplay = findViewById(R.id.tv15_nothingToDisplay);
        statisticsLayout = findViewById(R.id.fragment_container);
        dateSelectedView = findViewById(R.id.tv15_dateSelectedView);
    }
    private ArrayList<Integer> getEmptyIntegerList(int size) {
        ArrayList<Integer> emptyList = new ArrayList<Integer>();
        for(int i=1; i<=size; i++){
            emptyList.add(0);
        }
        return emptyList;
    }

    private void preparePieChart(ArrayList<PieEntry> ratingValues) {
        pieChart = findViewById(R.id.statisticsPieChart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        coloursList = new ArrayList<>();
        coloursList.add(Color.parseColor("#a90101"));
        coloursList.add(Color.parseColor("#ef4301"));
        coloursList.add(Color.parseColor("#fd7501"));
        coloursList.add(Color.parseColor("#feab01"));
        coloursList.add(Color.parseColor("#e1d801"));
        coloursList.add(Color.parseColor("#a2fe04"));
        coloursList.add(Color.parseColor("#57f804"));
        coloursList.add(Color.parseColor("#43bb04"));
        coloursList.add(Color.parseColor("#037104"));
        coloursList.add(Color.parseColor("#035f34"));

        PieDataSet dataSet = new PieDataSet(ratingValues, "Experience Ratings");
        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(20f);
        dataSet.setColors(coloursList);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        pieChart.setExtraBottomOffset(25f);
        pieChart.setData(data);
    }
    private void prepareBarGraphForFoodRatings(ArrayList<BarEntry> foodRatings) {
        foodRatingsBarGraph = findViewById(R.id.barChart_foodRating);
        foodRatingsBarGraph.setDrawGridBackground(true);
        foodRatingsBarGraph.setDrawValueAboveBar(true);
        foodRatingsBarGraph.setPinchZoom(false);
        foodRatingsBarGraph.setMaxVisibleValueCount(50);
        foodRatingsBarGraph.setDrawBarShadow(false);

        ArrayList<String> foodRatingLabels = new ArrayList<>();
        foodRatingLabels.add("Terrible");
        foodRatingLabels.add("Bad");
        foodRatingLabels.add("Okay");
        foodRatingLabels.add("Good");
        foodRatingLabels.add("Great");

        coloursList = new ArrayList<>();
        coloursList.add(Color.parseColor("#a90101"));
        coloursList.add(Color.parseColor("#fd7501"));
        coloursList.add(Color.parseColor("#e1d801"));
        coloursList.add(Color.parseColor("#57f804"));
        coloursList.add(Color.parseColor("#037104"));

        BarDataSet foodDataSet = new BarDataSet(foodRatings, "Food Ratings");
        foodDataSet.setColors(coloursList);
        BarData foodData = new BarData(foodDataSet);
        foodData.setBarWidth(0.5f);

        foodRatingsBarGraph.setData(foodData);

        XAxis xAxis = foodRatingsBarGraph.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(foodRatingLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5);

        foodRatingsBarGraph.notifyDataSetChanged();
        foodRatingsBarGraph.animateY(1000);
    }
    private void prepareBarGraphForServiceRatings(ArrayList<BarEntry> serviceRatings) {
        serviceRatingsBarGraph = findViewById(R.id.barChart_serviceRating);
        serviceRatingsBarGraph.setDrawBarShadow(false);
        serviceRatingsBarGraph.setDrawValueAboveBar(true);
        serviceRatingsBarGraph.setMaxVisibleValueCount(50);
        serviceRatingsBarGraph.setPinchZoom(false);
        serviceRatingsBarGraph.setDrawGridBackground(true);

        ArrayList<String> serviceRatingLabels = new ArrayList<>();
        serviceRatingLabels.add("0.5");
        serviceRatingLabels.add("1.0");
        serviceRatingLabels.add("1.5");
        serviceRatingLabels.add("2.0");
        serviceRatingLabels.add("2.5");
        serviceRatingLabels.add("3.0");
        serviceRatingLabels.add("3.5");
        serviceRatingLabels.add("4.0");
        serviceRatingLabels.add("4.5");
        serviceRatingLabels.add("5.5");

        coloursList = new ArrayList<>();
        coloursList.add(Color.parseColor("#a90101"));
        coloursList.add(Color.parseColor("#ef4301"));
        coloursList.add(Color.parseColor("#fd7501"));
        coloursList.add(Color.parseColor("#feab01"));
        coloursList.add(Color.parseColor("#e1d801"));
        coloursList.add(Color.parseColor("#a2fe04"));
        coloursList.add(Color.parseColor("#57f804"));
        coloursList.add(Color.parseColor("#43bb04"));
        coloursList.add(Color.parseColor("#037104"));
        coloursList.add(Color.parseColor("#035f34"));

        BarDataSet serviceDataSet = new BarDataSet(serviceRatings, "Service Ratings");
        serviceDataSet.setColors(coloursList);
        BarData serviceData = new BarData(serviceDataSet);
        serviceData.setBarWidth(0.5f);

        serviceRatingsBarGraph.setData(serviceData);

        XAxis xAxis = serviceRatingsBarGraph.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(serviceRatingLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(10);

        serviceRatingsBarGraph.notifyDataSetChanged();
        serviceRatingsBarGraph.animateY(1000);

    }
    private void startCountAnimation(final TextView textView, int endValue, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(0, endValue);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
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



    private class XAxisValueFormatter extends IndexAxisValueFormatter {

        private ArrayList<String> labels;
        public  XAxisValueFormatter(ArrayList<String> labels) {
            this.labels = labels;
        }

        public ArrayList<String> getLabels() {
            return labels;
        }

        @Override
        public String getFormattedValue(float value) {
            return labels.get((int)(value - 1.0f));
        }
    }
}
