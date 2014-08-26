package com.corneliudascalu.testglass;

import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class DeviceChooserActivity extends Activity {

    public static final String EXTRA_SELECTED_DEVICE = "SelectedDevice";

    @InjectView(R.id.chooseDeviceScroller)
    CardScrollView mCardScroller;

    private DeviceCardScrollAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.choose_device);
        ButterKnife.inject(this, this);

        ArrayList<BluetoothDevice> devices = getIntent().getParcelableArrayListExtra(
                MainActivity.EXTRA_DEVICES);

        adapter = new DeviceCardScrollAdapter(this, devices);
        mCardScroller.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SELECTED_DEVICE, (BluetoothDevice) adapter.getItem(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

}
