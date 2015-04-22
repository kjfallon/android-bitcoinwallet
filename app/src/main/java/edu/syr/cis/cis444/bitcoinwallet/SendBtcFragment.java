package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SendBtcFragment extends Fragment {

    private static final String TAG = SendBtcFragment.class.getSimpleName();
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_send_btc, container, false);
        return view;
    }

}
