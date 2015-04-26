package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.utils.BtcFormat;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;


public class WalletInfoFragment extends Fragment {

    private static final String TAG = WalletInfoFragment.class.getSimpleName();
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        CharSequence text = "Refreshing wallet from network";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(((MainActivity)this.getActivity()).getApplicationContext(), text, duration);
        toast.show();
        ((MainActivity)this.getActivity()).getBTCService().updateWalletFromNetwork();

        View view = inflater.inflate(R.layout.fragment_wallet_info, container, false);
        BtcFormat f = BtcFormat.getInstance();

        Log.d(TAG, "displaying wallet description");
        TextView walletDescription = (TextView) view.findViewById(R.id.textViewWalletDescription);
        walletDescription.setText("Wallet name: " + ((MainActivity)this.getActivity()).getBTCService().getDescription() );

        Log.d(TAG, "displaying wallet version");
        TextView walletVersion = (TextView) view.findViewById(R.id.textViewWalletVersion);
        walletVersion.setText("Wallet version: " + ((MainActivity)this.getActivity()).getBTCService().getVersion() );

        Log.d(TAG, "displaying wallet balance");
        TextView walletBalance = (TextView) view.findViewById(R.id.textViewWalletBalance);
        walletBalance.setText("Wallet Balance: " + f.format(((MainActivity)this.getActivity()).getBTCService().getBalance()) );

        Log.d(TAG, "displaying estimated wallet balance");
        TextView walletBalanceEstimated = (TextView) view.findViewById(R.id.textViewWalletBalanceEstimated);
        walletBalanceEstimated.setText("Wallet estimated Balance: " + f.format(((MainActivity)this.getActivity()).getBTCService().getBalanceEstimated()) );

        Log.d(TAG, "displaying watched wallet balance");
        TextView walletBalanceWatched = (TextView) view.findViewById(R.id.textViewWalletBalanceWatched);
        walletBalanceWatched.setText("Wallet watched Balance: " + f.format(((MainActivity)this.getActivity()).getBTCService().getWatchedBalance()) );

        Log.d(TAG, "displaying wallet transaction count");
        TextView walletTxCount = (TextView) view.findViewById(R.id.textViewWalletTransactionCount);
        walletTxCount.setText("Wallet tx count: " + ((MainActivity)this.getActivity()).getBTCService().getTransactions(true).size() );

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
