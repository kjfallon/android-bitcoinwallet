package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ViewAddressesFragment extends Fragment {

    private static final String TAG = ViewAddressesFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_view_addresses, container, false);
        return view;
    }

}
