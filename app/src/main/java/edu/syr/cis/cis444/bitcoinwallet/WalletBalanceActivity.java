package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class WalletBalanceActivity extends Activity {

    private static final String TAG = WalletBalanceActivity.class.getSimpleName();
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Log.d(TAG, "creating btcService...");
        BitcoinService btcService = new BitcoinService(context);
        setContentView(R.layout.activity_wallet_balance);
        TextView walletView = (TextView) findViewById(R.id.textViewWalletBalance);
        Log.d(TAG, "displaying wallet content");
        walletView.setText("wallet" + btcService.wallet);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallet_balance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Main Menu button */
    public void mainMenu(View view) {
        finish();
    }

}
