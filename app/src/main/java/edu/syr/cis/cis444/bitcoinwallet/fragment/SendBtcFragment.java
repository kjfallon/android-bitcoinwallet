package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bitcoinj.core.Address;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;

public class SendBtcFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = SendBtcFragment.class.getSimpleName();
    public static Context context;
    EditText addressEdit;
    EditText amountEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_send_btc, container, false);
        addressEdit   = (EditText) view.findViewById(R.id.editTextAddress);
        addressEdit.setText(((MainActivity) this.getActivity()).getBTCService().currentReceiveAddress().toString());
        amountEdit   = (EditText) view.findViewById(R.id.editTextAmount);
        amountEdit.setText("0.05");
        TextView walletView = (TextView) view.findViewById(R.id.textViewBalance);
        Log.d(TAG, "displaying wallet content");
        walletView.setText("Wallet Balance: " + ((MainActivity)this.getActivity()).getBTCService().getBalance() + " btc");

        Button sendButton = (Button) view.findViewById(R.id.buttonSendTransaction);
        sendButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSendTransaction:
                sendTransaction();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "registering send fragment on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "registering send fragment on the event bus");
        OttoEventBus.getInstance().unregister(this);
    }

    /** Called when the user clicks the Send BTC button in the send BTC fragment*/
    public void sendTransaction() {
        String address = addressEdit.getText().toString();
        String amount = amountEdit.getText().toString();
        String sendResult = ((MainActivity)this.getActivity()).getBTCService().sendTransaction(address, amount);
        Log.d(TAG, sendResult);
    }
}
