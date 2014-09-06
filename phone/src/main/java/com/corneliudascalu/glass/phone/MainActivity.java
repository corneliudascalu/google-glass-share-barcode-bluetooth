package com.corneliudascalu.glass.phone;

import com.google.android.gms.common.GooglePlayServicesUtil;

import com.corneliudascalu.glass.phone.domain.message.backservice.ConnectingToServerStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.ConnectionErrorMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.NoNetworkStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.RegisteredGcmStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.gcm.DeviceUnsupportedStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.gcm.RecoverableErrorStatusMessage;
import com.corneliudascalu.glass.phone.service.BackService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;

    @InjectView(R.id.text)
    TextView textView;

    @InjectView(R.id.logo)
    ImageView logo;

    private DateTimeFormatter formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        formatter = builder.appendHourOfDay(2).appendLiteral(':')
                .appendMinuteOfHour(2).appendLiteral(':')
                .appendSecondOfMinute(2).toFormatter();

        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTypeface(Typeface.MONOSPACE);

        startService(new Intent(this, BackService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                return true;
            case R.id.action_clear:
                textView.setText("");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLAY_SERVICES_RESOLUTION_REQUEST:
                if (resultCode == RESULT_OK) {
                    // connected to Play Services
                    addLogMessage("Connected to Play Services");
                } else {
                    addLogMessage("Failed to connect to Play Services");
                }
                break;
        }
    }

    public void onEventMainThread(Pair<Integer, String> pair) {
        switch (pair.first) {
            case Message.MSG_CONNECTED:
                addLogMessage(pair.second);
                break;
            case Message.MSG_READ_DATA:
                handleData(pair.second);
                addLogMessage(pair.second);
                break;
            case Message.MSG_DEBUG:
                addLogMessage(pair.second);
                break;
        }
    }

    public void onEventMainThread(NoNetworkStatusMessage message) {
        addLogMessage("No active network connection");
    }

    public void onEventMainThread(RecoverableErrorStatusMessage message) {
        GooglePlayServicesUtil
                .getErrorDialog(message.getResult(), this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
    }

    public void onEventMainThread(DeviceUnsupportedStatusMessage message) {
        addLogMessage("This device is not supported by Google Play Services");
    }

    public void onEventMainThread(RegisteredGcmStatusMessage message) {
        addLogMessage("Device registered to GCM");
    }

    public void onEventMainThread(ConnectingToServerStatusMessage message) {
        switch (message.getStatus()) {

            case None:
                break;
            case Failed:
                addLogMessage("Server connection failed: " + message.getMessage());
                break;
            case Success:
                addLogMessage("Server connection success: " + message.getMessage());
                break;
        }
    }

    public void onEventMainThread(ConnectionErrorMessage message) {
        addLogMessage("Connection error: " + message.getException().getMessage());
    }

    private void handleData(String second) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(second));

        PackageManager manager = getApplicationContext().getPackageManager();
        List<ResolveInfo> activities = manager.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "There is no application to handle this barcode",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addLogMessage(String text) {
        textView.append(new DateTime().toString(formatter) + " - " + text + "\n");
    }
}
