package com.example.dine_manager;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

import cdflynn.android.library.checkview.CheckView;

public class CustomDialog
{
    public static final String TAG = "CustomDialog.java";

    public static final int NAME = 1;
    public static final int CONTACT = 2;
    public static final int EMAIL = 3;
    public static final int NUMBER_OF_SEATS = 4;
    public static final int ROOM_NUMBER = 5;

    private Dialog   dialogBox;
    private Activity activity;
    private TextView displayText;
    private RotateLoading  progressBar;
    private CheckView      checkView;

    private OnButtonPressListener onButtonPressListener;
    private OnInputSetListener    onInputSetListener;
    private OnInputsSetListener   onInputsSetListener;
    private OnMessageEnteredListener onMessageEnteredListener;

    public CustomDialog(Activity activity) {
        this.activity = activity;
    }

    public void showProgressDialogWithMessage(String message) {
        if(dialogBox == null)
        {
            dialogBox = new Dialog(this.activity);
            dialogBox.setContentView(R.layout.custom_progress_bar_with_message);
            dialogBox.setCancelable(false);
            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            displayText = dialogBox.findViewById(R.id.dbtv1_displayText);
            progressBar = dialogBox.findViewById(R.id.dbpb1_rotateloading);
        }

        displayText.setText(message);
        progressBar.start();
        dialogBox.show();
    }
    public void showCompletedMessage(String message) {

        Button closeButton = null;

        if(dialogBox == null)
        {
            dialogBox = new Dialog(this.activity);
            dialogBox.setContentView(R.layout.custom_completed_message_box);
            dialogBox.setCancelable(false);
            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            displayText = dialogBox.findViewById(R.id.dbtv2_displayText);
            checkView = dialogBox.findViewById(R.id.dbpb2_checkView);
            closeButton = dialogBox.findViewById(R.id.dbpb2_doneButton);

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonPressListener.onButtonPressed();
                }
            });
        }

        displayText.setText(message);
        dialogBox.show();
        checkView.check();
    }
    public void showProgressBar() {
        if(dialogBox == null){
            dialogBox = new Dialog(this.activity);
            dialogBox.setContentView(R.layout.custom_indeterminate_progress_bar);
            dialogBox.setCancelable(false);
            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            progressBar = dialogBox.findViewById(R.id.dbpb3_rotateloading);
        }
        progressBar.start();
        dialogBox.show();
    }

    public void setOnButtonPressListener(OnButtonPressListener onButtonPressListener) {
        if(dialogBox != null) {
            this.onButtonPressListener = onButtonPressListener;
        } else {
            Log.e(TAG, "Dialog Box needs to be initialized. Call 'showCompletedMessage()' fisrt.");
        }
    }
    public void setOnInputSetListener(OnInputSetListener onInputSetListener) {
        if(dialogBox != null) {
            this.onInputSetListener = onInputSetListener;
        } else {
            Log.e(TAG , "Dialog Box needs to be initialized. Call 'showCompletedMessage()' fisrt.");
        }
    }
    public void setOnInputsSetListener(OnInputsSetListener onInputsSetListener) {
        this.onInputsSetListener = onInputsSetListener;
    }
    public void setOnMessageEnteredListener(OnMessageEnteredListener onMessageEnteredListener){
        this.onMessageEnteredListener = onMessageEnteredListener;
    }

    public void hideProgressdialog()
    {
        if(dialogBox != null) {
            dialogBox.dismiss();
            dialogBox = null;
        }
    }

    public void showDialogForInput(final int requestCode, String originalData) {
        if(dialogBox == null) {
            dialogBox = new Dialog(this.activity);
            dialogBox.setContentView(R.layout.dialog_with_input_layout);
            dialogBox.setCancelable(false);

            TextView prompt = dialogBox.findViewById(R.id.dwi1_tv_prompt);
            final TextInputEditText newData = dialogBox.findViewById(R.id.dwi1_et_newData);
            ImageView closeButton = dialogBox.findViewById(R.id.dwi1_imv_closeButton);
            Button saveButton = dialogBox.findViewById(R.id.dwi1_bt_saveButton);
            TextInputLayout inputLayout = dialogBox.findViewById(R.id.dwi1_textInputLayout);

            newData.setText(originalData);
            newData.setSelection(originalData.length());


            switch (requestCode) {
                case NAME :
                    prompt.setText("Enter new Name");
                    newData.setHint("Name");
                    inputLayout.setHint("Name");
                    newData.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case EMAIL :
                    prompt.setText("Enter new Email Address");
                    newData.setHint("Email Address");
                    inputLayout.setHint("Email Address");
                    newData.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
                case CONTACT :
                    prompt.setText("Enter new Contact Number");
                    newData.setHint("Contact Number");
                    inputLayout.setHint("Contact Number");
                    newData.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case NUMBER_OF_SEATS :
                    prompt.setText("Edit Number of seats");
                    newData.setHint("Seats");
                    inputLayout.setHint("Number of seats");
                    newData.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case ROOM_NUMBER :
                    prompt.setText("Edit Room number");
                    newData.setHint("Room number");
                    inputLayout.setHint("Room number");
                    newData.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBox.dismiss();
                    dialogBox = null;
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nData = newData.getText().toString().trim();

                    if(nData.isEmpty()) {
                        newData.setError("Field cannot be empty");
                        newData.requestFocus();
                    } else {
                        switch (requestCode) {

                            case NAME :
                                if(nData.length() < 3) {
                                    newData.setError("Enter a valid name");
                                    newData.requestFocus();
                                }
                                else {
                                    onInputSetListener.onInputSet(nData, requestCode);
                                    dialogBox.dismiss();
                                    dialogBox = null;
                                }
                                break;

                            case EMAIL :
                                if(!(Patterns.EMAIL_ADDRESS.matcher(nData).matches())) {
                                    newData.setError("Enter a valid Email Address");
                                    newData.requestFocus();
                                }
                                else {
                                    onInputSetListener.onInputSet(nData, requestCode);
                                    dialogBox.dismiss();
                                    dialogBox = null;
                                }
                                break;

                            case CONTACT :
                                if( (!(Patterns.PHONE.matcher(nData).matches())) || (nData.length() < 6) ) {
                                    newData.setError("Enter a valid contact number");
                                    newData.requestFocus();
                                }
                                else {
                                    onInputSetListener.onInputSet(nData, requestCode);
                                    dialogBox.dismiss();
                                    dialogBox = null;
                                }
                                break;
                            default :
                                onInputSetListener.onInputSet(nData, requestCode);
                                dialogBox.dismiss();
                                dialogBox = null;
                                break;
                        }
                    }
                }
            });

            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //dialogBox.getWindow().getAttributes().x = 0;
            //dialogBox.getWindow().getAttributes().y = 0;

            dialogBox.show();
        }
    }
    public void showDialogForTwoInputs() {
        if(dialogBox == null) {
            dialogBox = new Dialog(this.activity);
            dialogBox.setContentView(R.layout.dialog_with_two_inputs);
            dialogBox.setCancelable(false);

            final TextInputEditText nameET = dialogBox.findViewById(R.id.dwi2_nameInput);
            final TextInputEditText emailET = dialogBox.findViewById(R.id.dwi2_emailInput);
            final TextInputLayout   nameInputLayout = dialogBox.findViewById(R.id.dwi2_nameInputLayout);
            final TextInputLayout   emailInputLayout = dialogBox.findViewById(R.id.dwi2_emailInputLayout);
            final ImageView closeDialogButton = dialogBox.findViewById(R.id.dwi2_closeDialogButton);
            Button saveButton = dialogBox.findViewById(R.id.dwi2_buttonSave);

            closeDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBox.dismiss();
                    dialogBox = null;
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameET.getText().toString().trim();
                    String emailAddress = emailET.getText().toString().trim();

                    if(name.isEmpty()) {
                        nameInputLayout.setError("Name Required !");
                        nameET.requestFocus();
                    }
                    else if(name.length() < 3) {
                        nameInputLayout.setError("Enter a valid Name !");
                        nameET.requestFocus();
                    }
                    else if(!emailAddress.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                        emailInputLayout.setError("Enter a valid Email Address");
                    }
                    else {
                        onInputsSetListener.onInputsSet(name, emailAddress);
                        dialogBox.dismiss();
                        dialogBox = null;
                    }

                }
            });

            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            dialogBox.show();
        }
    }
    public void showDialogForMessageInput(String CustomerName) {
        if(dialogBox == null) {
            dialogBox = new Dialog(this.activity);
            dialogBox.setContentView(R.layout.dialog_for_message_input);
            dialogBox.setCancelable(false);

            final TextInputLayout messageInputLayout = dialogBox.findViewById(R.id.dbfmi_messageInputLayout);
            final TextInputEditText messageInputEditText = dialogBox.findViewById(R.id.dbfmi_messageInputEditText);
            ImageView closeButton = dialogBox.findViewById(R.id.dbfmi_closeButton);
            TextView customerName = dialogBox.findViewById(R.id.dbfmi_customerName);
            Button sendButton = dialogBox.findViewById(R.id.dbfmi_sendButton);

            customerName.setText(CustomerName);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = Objects.requireNonNull(messageInputEditText.getText()).toString().trim();
                    if(message.isEmpty()) {
                        messageInputLayout.setError("Enter a message !");
                        messageInputEditText.setError("Enter a message !");
                        messageInputEditText.requestFocus();
                    } else{
                        onMessageEnteredListener.onMessageEntered(message);
                        dialogBox.dismiss();
                        dialogBox = null;
                    }
                }
            });

            messageInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().trim().length() >= 153)
                        messageInputLayout.setError("Limit Exceeded ! max 153");
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBox.dismiss();
                    dialogBox = null;
                }
            });

            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            dialogBox.show();
        }
    }

    public interface OnButtonPressListener {
        void onButtonPressed();
    }

    public interface OnInputSetListener {
        void onInputSet(String userInput, int requestCode);
    }

    public interface OnInputsSetListener {
        void onInputsSet(String name, String emailAddress);
    }

    public interface OnMessageEnteredListener {
        void onMessageEntered(String Message);
    }

}
