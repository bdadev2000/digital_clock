package com.bdadevfs.threadtutorial02;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private EditText changeTextEditor;
    private Button changeTextButton;
    private TextView changeTextTextView;
    // This is the activity main thread Handler.
    private Handler updateUIHandler = null;
    // Message type code.
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Handler.
        createUpdateUiHandler();

        // Change text button.
        changeTextButton = (Button)findViewById(R.id.change_text_in_child_thread_button);
        // Show text textview.
        changeTextTextView = (TextView)findViewById(R.id.change_text_textview);

        // Click this button to start a child thread.
        changeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeTextButton.performClick();
                    }
                }, 1000);
                Thread workerThread = new Thread()
                {
                    @Override
                    public void run() {
                        // Can not update ui component directly when child thread run.
                        // updateText();
                        // Build message object.

                        Message message = new Message();
                        // Set message type.
                        message.what = MESSAGE_UPDATE_TEXT_CHILD_THREAD;
                        // Send message to main thread Handler.
                        updateUIHandler.sendMessage(message);
                    }
                };
                workerThread.start();
            }
        });

    }
    /* Update ui text.*/
    private void updateText()
    {
        Calendar calendar = Calendar.getInstance();
        String hour = (calendar.getTime().getHours() > 9) ?
                "" + calendar.getTime().getHours() + ""
                : "0" + calendar.getTime().getHours();
        String minute = (calendar.getTime().getMinutes() > 9) ?
                "" + calendar.getTime().getMinutes() + ""
                : "0" + calendar.getTime().getMinutes();
        String second = (calendar.getTime().getSeconds() > 9) ?
                "" + calendar.getTime().getSeconds() + ""
                : "0" + calendar.getTime().getSeconds();
        changeTextTextView.setText(hour + ":" + minute + ":" + second);

    }
    /* Create Handler object in main thread. */
    private void createUpdateUiHandler()
    {
        if(updateUIHandler == null)
        {
            updateUIHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    // Means the message is sent from child thread.
                    if(msg.what == MESSAGE_UPDATE_TEXT_CHILD_THREAD)
                    {
                        // Update ui in main thread.
                        updateText();
                    }
                }
            };
        }
    }
}