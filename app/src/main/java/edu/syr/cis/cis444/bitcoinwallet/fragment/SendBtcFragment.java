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
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.utils.BtcFormat;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;

public class SendBtcFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = SendBtcFragment.class.getSimpleName();
    public static Context context;
    EditText addressEdit;
    EditText amountEdit;
    TextView walletView;
    BtcFormat btcFormat = BtcFormat.getInstance();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.fragment_send_btc, container, false);
        addressEdit = (EditText) view.findViewById(R.id.editTextAddress);
        addressEdit.setText("n3hRBqByW5CapsquwDGgkPnSdLH3iUmWBG");

        amountEdit = (EditText) view.findViewById(R.id.editTextAmount);
        amountEdit.setText("0.0");

        Log.d(TAG, "displaying wallet content");
        walletView = (TextView) view.findViewById(R.id.textViewBalance);
        walletView.setText("Wallet Balance: " + btcFormat.format(((MainActivity)this.getActivity()).getBTCService().getBalanceEstimated()) );

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

        Log.d(TAG, "updating amountEdit textview");
        amountEdit.setText("0.0");
        Log.d(TAG, "sending transaction start Toast to UI");
        CharSequence text = "Sending your transaction";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(((MainActivity)this.getActivity()).getApplicationContext(), text, duration);
        toast.show();
        // performing transaction
        Log.d(TAG, "invoking sendTransactions on btcService");
        String sendResult = ((MainActivity)this.getActivity()).getBTCService().sendTransaction(address, amount);
        // updating UI
        Log.d(TAG, "displaying wallet content");
        walletView = (TextView) view.findViewById(R.id.textViewBalance);
        walletView.setText("Wallet Balance: " + btcFormat.format(((MainActivity)this.getActivity()).getBTCService().getBalanceEstimated()) );
        Log.d(TAG, "sending update complete Toast to UI");
        text = "Sending transaction complete";
        toast = Toast.makeText(((MainActivity)this.getActivity()).getApplicationContext(), text, duration);
        toast.show();
        Log.d(TAG, sendResult);
    }
}
