package com.example.dine_manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

    public static final int EVENT_TYPE_BIRTHDAY = -36;
    public static final int EVENT_TYPE_ANNIVERSARY = -35;

    private ArrayList<OEvent> birthdayEvents;
    private ArrayList<OEvent> anniversaryEvents;
    private int currentEventType = 0;
    private Context context;
    private Calendar calendar;
    private Activity activity;

    public EventsAdapter(Context context, Activity activity, ArrayList<OEvent> eventsList, int eventType) {
        this.birthdayEvents = eventsList;
        this.anniversaryEvents = eventsList;
        this.currentEventType = eventType;
        this.activity = activity;
        this.context = context;

        calendar = Calendar.getInstance();
    }

    public int getCurrentEventType() {
        return currentEventType;
    }
    public void setCurrentEventType(int currentEventType) {
        this.currentEventType = currentEventType;
    }

    public ArrayList<OEvent> getBirthdayEvents() {
        return birthdayEvents;
    }
    public void setBirthdayEvents(ArrayList<OEvent> birthdayEvents) {
        this.birthdayEvents = birthdayEvents;
    }

    public ArrayList<OEvent> getAnniversaryEvents() {
        return anniversaryEvents;
    }
    public void setAnniversaryEvents(ArrayList<OEvent> anniversaryEvents) {
        this.anniversaryEvents = anniversaryEvents;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_events, parent , false);

        return new EventsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsViewHolder holder, int position) {
        if(currentEventType == EVENT_TYPE_BIRTHDAY) {
            final OEvent birthdayEvent = birthdayEvents.get(position);

            holder.customerName.setText(birthdayEvent.getCustomerName());
            holder.customerContact.setText(birthdayEvent.getCustomerContact());
            calendar.set(Calendar.MONTH, birthdayEvent.getMonth() - 1);
            String dateText = birthdayEvent.getDayOfMonth() + getSuffix(birthdayEvent.getDayOfMonth()) +" "+ calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            if(birthdayEvent.getDayOfMonth() == getCurrentDayOfMonth() && birthdayEvent.getMonth() == getCurrentMonth()) {
                holder.eventDate.setText("Today");
                holder.eventDate.setTextColor(Color.parseColor("#1CB845"));
            }
            else {
                holder.eventDate.setText(dateText);
                holder.eventDate.setTextColor(Color.parseColor("#333333"));
            }

            final CustomDialog customDialog = new CustomDialog(this.activity);

            holder.optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu sendMessageOptions = new PopupMenu(context, holder.optionsButton);
                    sendMessageOptions.inflate(R.menu.events_adapter_options_menu);
                    sendMessageOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.sendSMSViaMessage :
                                    customDialog.showDialogForMessageInput(birthdayEvent.getCustomerName());
                                    customDialog.setOnMessageEnteredListener(new CustomDialog.OnMessageEnteredListener() {
                                        @Override
                                        public void onMessageEntered(String Message) {
                                            sendMessageViaSMS(birthdayEvent.getCustomerContact(), Message);
                                            Toast.makeText(context, "Message Sent !", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case R.id.sendSMSViaWhatsapp :
                                    sendMessageViaWatsapp(birthdayEvent.getCustomerContact(), "Hello "+birthdayEvent.getCustomerName()+"\n");
                                    break;
                            }
                            return false;
                        }
                    });
                    sendMessageOptions.show();
                }
            });

        } else if( currentEventType == EVENT_TYPE_ANNIVERSARY) {

            final OEvent anniversaryEvent = anniversaryEvents.get(position);

            holder.customerName.setText(anniversaryEvent.getCustomerName());
            holder.customerContact.setText(anniversaryEvent.getCustomerContact());
            calendar.set(Calendar.MONTH, anniversaryEvent.getMonth() -1);
            String dateText = anniversaryEvent.getDayOfMonth() + getSuffix(anniversaryEvent.getDayOfMonth()) +" "+ calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            if(anniversaryEvent.getDayOfMonth() == getCurrentDayOfMonth() && anniversaryEvent.getMonth() == getCurrentMonth()) {
                holder.eventDate.setText("Today");
                holder.eventDate.setTextColor(Color.parseColor("#1CB845"));
            }
            else {
                holder.eventDate.setText(dateText);
                holder.eventDate.setTextColor(Color.parseColor("#333333"));
            }

            final CustomDialog customDialog = new CustomDialog(activity);

            holder.optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu sendMessageOptions = new PopupMenu(context, holder.optionsButton);
                    sendMessageOptions.inflate(R.menu.events_adapter_options_menu);
                    sendMessageOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.sendSMSViaMessage :
                                    customDialog.showDialogForMessageInput(anniversaryEvent.getCustomerName());
                                    customDialog.setOnMessageEnteredListener(new CustomDialog.OnMessageEnteredListener() {
                                        @Override
                                        public void onMessageEntered(String Message) {
                                            sendMessageViaSMS(anniversaryEvent.getCustomerContact(), Message);
                                            Toast.makeText(context, "Message Sent !", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case R.id.sendSMSViaWhatsapp :
                                    sendMessageViaWatsapp(anniversaryEvent.getCustomerContact(), "Hello "+anniversaryEvent.getCustomerName()+"\n");
                                    break;
                            }
                            return false;
                        }
                    });
                    sendMessageOptions.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(this.currentEventType == EVENT_TYPE_BIRTHDAY)
            return birthdayEvents.size();
        else if(currentEventType == EVENT_TYPE_ANNIVERSARY)
            return anniversaryEvents.size();

        return 0;
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {

        TextView customerName, eventDate, optionsButton, customerContact;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.lie_customerName);
            customerContact = itemView.findViewById(R.id.lie_customerContact);
            optionsButton = itemView.findViewById(R.id.lie_optionButton);
            eventDate = itemView.findViewById(R.id.lie_eventDate);
        }
    }
    private String getSuffix(int date) {
        int r = date % 10;

        switch (r) {
            case 1 :
                return "st";
            case 2 :
                return "nd";
            case 3 :
                return "rd";
        }
        return "th";
    }
    private int getCurrentYear() {
        Calendar calendar1 = Calendar.getInstance();
        int year = calendar1.get(Calendar.YEAR);

        return year;
    }
    private int getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth;
    }
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return (month+1);
    }
    private void sendMessageViaSMS(String contact, String message) {

        int permissionCheck = ContextCompat.checkSelfPermission(this.activity, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact, null, message, null, null);
        } else {
            Toast.makeText(context, "Permission not Granted !", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.SEND_SMS}, 0 );
        }
    }
    private void sendMessageViaWatsapp(String contact, String message){

        Uri destinationURI = Uri.parse("http://api.whatsapp.com/send?phone="+"91"+contact+"&text="+message);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        //sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setData(destinationURI);
        sendIntent.setPackage("com.whatsapp");
        context.startActivity(sendIntent);
    }

    public ArrayList<String> getContactNumbers() {
        ArrayList<String> contactsList = new ArrayList<>();
        for(OEvent event : this.birthdayEvents ){
            contactsList.add(event.getCustomerContact());
        }
        return contactsList;
    }
}
