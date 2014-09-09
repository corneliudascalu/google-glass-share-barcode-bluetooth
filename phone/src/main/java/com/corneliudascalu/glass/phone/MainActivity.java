package com.corneliudascalu.glass.phone;

import com.google.android.gms.common.GooglePlayServicesUtil;

import com.corneliudascalu.glass.device.model.NotificationMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.ConnectingToServerStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.ConnectionErrorMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.IntentUnhandledMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.NoNetworkStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.RegisteredGcmStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.UnsupportedFormatMessage;
import com.corneliudascalu.glass.phone.domain.message.gcm.DeviceUnsupportedStatusMessage;
import com.corneliudascalu.glass.phone.domain.message.gcm.RecoverableErrorStatusMessage;
import com.corneliudascalu.glass.phone.service.BackService;
import com.corneliudascalu.glass.phone.service.BackServiceImpl;
import com.corneliudascalu.glass.phone.service.LocalBinder;
import com.crashlytics.android.Crashlytics;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;

    public static final String LOG_EXTRA = "LOG_EXTRA";

    @InjectView(R.id.text)
    TextView textView;

    @InjectView(R.id.logo)
    ImageView logo;

    private DateTimeFormatter formatter;

    private BackService service;

    private boolean bound = false;

    private ServiceConnection connection;

    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        EventBus.getDefault().register(this);
        connection = getConnection();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this, this);
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        formatter = builder.appendHourOfDay(2).appendLiteral(':')
                .appendMinuteOfHour(2).appendLiteral(':')
                .appendSecondOfMinute(2).toFormatter();

        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTypeface(Typeface.MONOSPACE);

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, BackServiceImpl.class);
            startService(intent);
        } else {
            String logText = savedInstanceState.getString(LOG_EXTRA);
            textView.setText(logText);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, BackServiceImpl.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(LOG_EXTRA, String.valueOf(textView.getText()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bound) {
            unbindService(connection);
            addLogMessage("Unbound from background service");
            service = null;
            bound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem shareMenuItem = menu.findItem(R.id.action_share_log);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(getDefaultShareIntent());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                service.forceGcmRegistration();
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
        addLogMessage("Device registered to GCM. ID: " + message.getRegistrationId());
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

    public void onEventMainThread(NotificationMessage message) {
        addLogMessage(
                "Received: " + message.getTitle() + " - " + message.getMessage());
    }

    public void onEventMainThread(IntentUnhandledMessage message) {
        addLogMessage(message.getMessage());
    }

    public void onEventMainThread(UnsupportedFormatMessage message) {
        addLogMessage("Received unknown message: " + message.getMessage());
    }

    private void addLogMessage(String text) {
        textView.append(new DateTime().toString(formatter) + " - " + text + "\n");
        if (shareActionProvider != null) {
            shareActionProvider
                    .setShareIntent(getShareLogIntent(String.valueOf(textView.getText())));
        }
    }

    private ServiceConnection getConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                service = ((LocalBinder) iBinder).getService();
                addLogMessage("Bound to background service");
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                addLogMessage("Background service disconnected");
                bound = false;
            }
        };
    }

    private Intent getDefaultShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        return intent;
    }

    private Intent getShareLogIntent(String logText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, logText);
        return intent;
    }
}
