package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;


public class WalletBalanceFragment extends Fragment {

    private static final String TAG = WalletBalanceFragment.class.getSimpleName();
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_wallet_balance, container, false);
        TextView walletView = (TextView) view.findViewById(R.id.textViewWalletBalance);
        Log.d(TAG, "displaying wallet content");
        walletView.setText("Wallet Balance: " + ((MainActivity)this.getActivity()).getBTCService().getBalance() + " btc");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "registering wallet balance fragment on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "unregistering wallet balance fragment on the event bus");
        OttoEventBus.getInstance().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
