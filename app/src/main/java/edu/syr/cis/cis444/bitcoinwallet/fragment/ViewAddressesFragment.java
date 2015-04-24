package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;


public class ViewAddressesFragment extends Fragment {

    private static final String TAG = ViewAddressesFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_view_addresses, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "registering view addresses fragment on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "unregistering view addresses fragment on the event bus");
        OttoEventBus.getInstance().unregister(this);
    }

}
