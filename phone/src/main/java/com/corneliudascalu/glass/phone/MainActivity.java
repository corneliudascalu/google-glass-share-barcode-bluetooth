package com.corneliudascalu.glass.phone;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

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
        startService(new Intent(this, BluetoothService.class));

        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTypeface(Typeface.MONOSPACE);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEventMainThread(new Pair<Integer, String>(
                        BluetoothService.MSG_READ_DATA,
                        "http://d.android.com"
                ));
            }
        });
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            textView.setText("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(Pair<Integer, String> pair) {
        DateTime dateTime = new DateTime();
        switch (pair.first) {
            case BluetoothService.MSG_CONNECTED:
                //textView.setText(dateTime.toString(formatter) + " - "+ pair.second + "\n" + textView.getText());
                textView.append(dateTime.toString(formatter) + " - "
                        + pair.second + "\n");
                break;
            case BluetoothService.MSG_READ_DATA:
                //textView.setText(dateTime.toString(formatter) + " - "+ pair.second + "\n" + textView.getText());
                textView.append(dateTime.toString(formatter) + " - "
                        + pair.second + "\n");
                handleData(pair.second);
                break;

        }
    }

    private void handleData(String second) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(second));

        PackageManager manager = getApplicationContext().getPackageManager();
        List<ResolveInfo> activities = manager.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            //Then there is application can handle your intent
            startActivity(intent);
        } else {
            //No Application can handle your intent
            Toast.makeText(this, "There is no application to handle this barcode",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
