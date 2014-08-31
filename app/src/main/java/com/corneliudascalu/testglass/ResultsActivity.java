package com.corneliudascalu.testglass;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import com.github.barcodeeye.scan.CaptureActivity;
import com.github.barcodeeye.scan.api.CardPresenter;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends BluetoothConnectedActivity {

    private static final String TAG = ResultsActivity.class.getSimpleName();

    private static final String EXTRA_CARDS = "EXTRA_CARDS";

    private static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";

    private final List<CardPresenter> mCardPresenters = new ArrayList<CardPresenter>();

    private CardScrollView mCardScrollView;

    public static Intent newIntent(Context context,
            List<CardPresenter> cardResults) {

        Intent intent = new Intent(context, ResultsActivity.class);
        if (cardResults != null) {
            intent.putExtra(EXTRA_CARDS,
                    cardResults.toArray(new CardPresenter[cardResults.size()]));
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            readExtras(intent.getExtras());
        } else if (intent != null && intent.getExtras() != null) {
            readExtras(intent.getExtras());
        } else {
            Log.e(TAG, "No extras were present");
            finish();
            return;
        }

        if (mCardPresenters.size() == 0) {
            Log.w(TAG, "There were no cards to display");
            finish();
            return;
        }

        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(new CardScrollViewAdapter(this,
                mCardPresenters));
        mCardScrollView.activate();

        setContentView(mCardScrollView);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.share_bt_menu, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_bt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.shareBluetooth:
                    CardPresenter cardPresenter = mCardPresenters
                            .get(mCardScrollView.getSelectedItemPosition());
                    if (cardPresenter != null) {
                        sendData(cardPresenter.getFooter());
                    }
                    return true;
                case R.id.moreDetails:
                    sendItemPendingIntent(mCardScrollView.getSelectedItemPosition());
                    return true;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void readExtras(Bundle extras) {
        Parcelable[] parcelCardsArray = extras.getParcelableArray(CaptureActivity.EXTRA_CARDS);
        for (Parcelable aParcelCardsArray : parcelCardsArray) {
            mCardPresenters.add((CardPresenter) aParcelCardsArray);
        }
    }

    public static class CardScrollViewAdapter extends CardScrollAdapter {

        private final Context mContext;

        private final List<CardPresenter> mCardPresenters;

        public CardScrollViewAdapter(Context context,
                List<CardPresenter> cardPresenter) {
            mContext = context;
            mCardPresenters = cardPresenter;
        }

        @Override
        public int getCount() {
            return mCardPresenters.size();
        }

        @Override
        public Object getItem(int position) {
            return mCardPresenters.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CardPresenter cardPresenter = mCardPresenters.get(position);
            return cardPresenter.getCardView(mContext);
        }

        @Override
        public int getPosition(Object item) {
            return mCardPresenters.indexOf(item);
        }
    }

    private void sendItemPendingIntent(int position) {
        CardPresenter cardPresenter = mCardPresenters.get(position);
        PendingIntent pendingIntent = cardPresenter.getPendingIntent();
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                Log.w(TAG, e.getMessage());
            }
        } else {
            Log.w(TAG, "No PendingIntent attached to card!");
        }
    }
}
