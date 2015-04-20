package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.bitcoinj.core.Wallet;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Log.d(TAG, "creating btcService...");
        BitcoinService btcService = new BitcoinService(context);
        Log.d(TAG, "completed creating btcService");
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /** Called when the user clicks the Wallet Balance button */
    public void viewWalletBalance(View view) {
        Intent intent = new Intent(this, WalletBalanceActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the View Addresses button */
    public void viewAddresses(View view) {
        Intent intent = new Intent(this, ViewAddressesActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Send BTC button */
    public void sendBtc(View view) {
        Intent intent = new Intent(this, SendBtcActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Recieve BTC button */
    public void receiveBtc(View view) {
        Intent intent = new Intent(this, ReceiveBtcActivity.class);
        startActivity(intent);
    }

}
