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


public class WalletRecoveryFragment extends Fragment {

    private static final String TAG = WalletRecoveryFragment.class.getSimpleName();
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_wallet_recovery, container, false);
        TextView mnemonicTextView = (TextView) view.findViewById(R.id.textViewRecoveryMnemonic);
        Log.d(TAG, "displaying wallet mnemonic");
        mnemonicTextView.setText( ((MainActivity)this.getActivity()).getBTCService().getMnemonic() );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "registering wallet recovery fragment on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "unregistering wallet recovery fragment on the event bus");
        OttoEventBus.getInstance().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
