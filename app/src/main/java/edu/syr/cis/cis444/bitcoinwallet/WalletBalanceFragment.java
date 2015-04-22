package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class WalletBalanceFragment extends Fragment {

    private static final String TAG = WalletBalanceFragment.class.getSimpleName();
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_wallet_balance, container, false);
        TextView walletView = (TextView) view.findViewById(R.id.textViewWalletBalance);
        Log.d(TAG, "displaying wallet content");
        walletView.setText("Wallet Ballance: " + ((MainActivity)this.getActivity()).getBTCService().wallet.getBalance());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
