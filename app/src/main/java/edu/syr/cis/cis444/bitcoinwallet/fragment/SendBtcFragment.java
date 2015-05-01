package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;
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
    String sendToAddress = "";
    String amountToSend = "0.0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.fragment_send_btc, container, false);
        addressEdit = (EditText) view.findViewById(R.id.editTextAddress);
        addressEdit.setText(sendToAddress);

        amountEdit = (EditText) view.findViewById(R.id.editTextAmount);
        amountEdit.setText(amountToSend);

        Log.d(TAG, "displaying wallet content");
        walletView = (TextView) view.findViewById(R.id.textViewBalance);
        walletView.setText("Wallet Balance: " + btcFormat.format(((MainActivity)this.getActivity()).getBTCService().getBalanceEstimated()) );

        Button scanButton = (Button) view.findViewById(R.id.buttonScanQR);
        scanButton.setOnClickListener(this);

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
            case R.id.buttonScanQR:
                scanQr();
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

    /** Called when the user clicks the Scan QR button in the send BTC fragment*/
    public void scanQr() {
        Log.d(TAG, "sending scan QR intent");
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            Log.d(TAG, "received result from scan QR intent");
            if (resultCode == Activity.RESULT_OK) {
                BitcoinURI bitcoinUri = ((MainActivity) this.getActivity()).getBTCService().createBitcoinURI(intent.getStringExtra("SCAN_RESULT"));
                if (bitcoinUri != null) {
                    sendToAddress = bitcoinUri.getAddress().toString();
                    addressEdit.setText(sendToAddress);
                    long satoshis = bitcoinUri.getAmount().getValue();
                    double btc = ((double)satoshis/100000000L);
                    Log.d(TAG, "Requested about as a double btc: " + btc);
                    amountToSend = Double.toString(btc);
                    amountEdit.setText(amountToSend);
                }
            }
        }
    }

    /** Called when the user clicks the Send BTC button in the send BTC fragment*/
    public void sendTransaction() {

        String address = addressEdit.getText().toString();
        String amount = amountEdit.getText().toString();
        Coin coinAmount = Coin.parseCoin(amount);
        if (coinAmount.isPositive()) {
            Log.d(TAG, "updating amountEdit textview");
            amountEdit.setText("0.0");
            Log.d(TAG, "sending transaction start Toast to UI");
            CharSequence text = "Sending your transaction";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(((MainActivity) this.getActivity()).getApplicationContext(), text, duration);
            toast.show();
            // performing transaction
            Log.d(TAG, "invoking sendTransactions on btcService");
            String sendResult = ((MainActivity) this.getActivity()).getBTCService().sendTransaction(address, amount);
            // updating UI
            Log.d(TAG, "displaying wallet content");
            walletView = (TextView) view.findViewById(R.id.textViewBalance);
            walletView.setText("Wallet Balance: " + btcFormat.format(((MainActivity) this.getActivity()).getBTCService().getBalanceEstimated()));
            Log.d(TAG, "sending update complete Toast to UI");
            text = "Sending transaction complete";
            toast = Toast.makeText(((MainActivity) this.getActivity()).getApplicationContext(), text, duration);
            toast.show();
            Log.d(TAG, sendResult);
        }
        else {
            Log.d(TAG, "non positive amount specified to send ");
            CharSequence text = "amount must be positive";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(((MainActivity) this.getActivity()).getApplicationContext(), text, duration);
            toast.show();
        }
    }
}
