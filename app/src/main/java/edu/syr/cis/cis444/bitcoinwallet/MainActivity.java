package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.syr.cis.cis444.bitcoinwallet.fragment.ReceiveBtcFragment;
import edu.syr.cis.cis444.bitcoinwallet.fragment.SendBtcFragment;
import edu.syr.cis.cis444.bitcoinwallet.fragment.ViewAddressesFragment;
import edu.syr.cis.cis444.bitcoinwallet.fragment.WalletInfoFragment;
import edu.syr.cis.cis444.bitcoinwallet.fragment.WalletRecoveryFragment;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static Context context;
    public BitcoinService btcService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Log.d(TAG, "creating btcService...");
        btcService = new BitcoinService(context);
        Log.d(TAG, "completed creating btcService");
        setContentView(R.layout.activity_main);
        String fragmentName = getIntent().getStringExtra("fragmentName");
        Log.d(TAG, "specified fragment name is: " + fragmentName);

        /** Adds the placeholder fragment (containing buttons) to the activity **/
        if ( (savedInstanceState == null) && (fragmentName == null)) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        if (fragmentName != null)
        {
            if (fragmentName.equals("WalletRecoveryFragment"))
            {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new WalletRecoveryFragment())
                        .commit();
            }
        }

        Log.d(TAG, "starting to update wallet from blockchain...");
        btcService.initWalletFromNetwork();
        Log.d(TAG, "completed updating wallet from blockchain");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "registering main activity on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "registering main activity on the event bus");
        OttoEventBus.getInstance().unregister(this);
    }

    /** Called in fragments to access the btcService created above. you can use
     * ((MainActivity)this.getActivity()).getBTCService() to access object from fragments **/
    public BitcoinService getBTCService(){
        return btcService;
    }

    /** Exists as placeholder to display the main menu UI **/
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main_activity, container, false);
        }
    }

    /** Called when the user clicks the Wallet Balance button */
    public void viewWalletBalance(View view) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new WalletInfoFragment())
                .commit();
    }

    /** Called when the user clicks the View Addresses button */
    public void viewAddresses(View view) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ViewAddressesFragment())
                .commit();
    }

    /** Called when the user clicks the Send BTC button */
    public void sendBtc(View view) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SendBtcFragment())
                .commit();
    }

    /** Called when the user clicks the Recieve BTC button */
    public void receiveBtc(View view) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ReceiveBtcFragment())
                .commit();
    }

    /** Called when the user clicks the Recovery Information button */
    public void recoveryFragment(View view) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new WalletRecoveryFragment())
                .commit();
    }

    /** Called when user clicks Main Menu button in any of the fragments **/
    public void mainMenu(View view) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new PlaceholderFragment())
                .commit();
    }



}
