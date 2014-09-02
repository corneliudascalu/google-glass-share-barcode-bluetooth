package com.corneliudascalu.glass.phone;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.text)
    TextView textView;

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
                break;

        }
    }
}
