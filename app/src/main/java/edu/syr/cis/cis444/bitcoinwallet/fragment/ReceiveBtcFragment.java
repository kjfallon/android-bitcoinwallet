package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import static android.widget.ImageView.ScaleType.CENTER_INSIDE;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.squareup.otto.Subscribe;

import org.bitcoinj.core.Address;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;
import edu.syr.cis.cis444.bitcoinwallet.event.QrCodeAvailableEvent;
import edu.syr.cis.cis444.bitcoinwallet.task.CreateQrTask;


public class ReceiveBtcFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ReceiveBtcFragment.class.getSimpleName();
    public static Context context;
    private static CreateQrTask createQrTask;
    private View view;
    EditText amountEdit;
    String freshAddressString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.fragment_receive_btc, container, false);

        TextView receiveAddrView = (TextView) view.findViewById(R.id.textViewFreshReceiveAddress);
        Log.d(TAG, "displaying fresh address as text");
        Address freshAddress = ((MainActivity)this.getActivity()).getBTCService().freshReceiveAddress();
        freshAddressString = freshAddress.toString();
        receiveAddrView.setText("Send BTC to: " + freshAddressString);
        amountEdit = (EditText) view.findViewById(R.id.editTextReceiveAmount);
        amountEdit.setText("0.05");

        Button sendButton = (Button) view.findViewById(R.id.buttonUpdateReceiveQR);
        sendButton.setOnClickListener(this);

        Log.d(TAG, "displaying QR for fresh address " + freshAddressString );
        // submit QR code creation task to bus
        Log.d(TAG, "submitting CreateQrTask to bus");
        createQrTask = new CreateQrTask();
        createQrTask.execute(freshAddressString,"");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUpdateReceiveQR:
                receiveAmount();
                break;
        }
    }

    /** Called when the user clicks the Update QR in the reveive BTC fragment*/
    public void receiveAmount() {
        String amount = amountEdit.getText().toString();
        Log.d(TAG, "displaying QR for " + amount + " to " + freshAddressString );
        // submit QR code creation task to bus
        Log.d(TAG, "submitting CreateQrTask to bus");
        createQrTask = new CreateQrTask();
        createQrTask.execute(freshAddressString, amount);
    }

    // when a QR code availibile event is posted to the bus then display it
    @Subscribe
    public void onImageAvailable(QrCodeAvailableEvent event) {
        Log.d(TAG, "received QrCodeAvailableEvent from bus");
        ImageView qrView = (ImageView) view.findViewById(R.id.imageViewReceiveQR);
        qrView.setScaleType(CENTER_INSIDE);
        if (qrView != null) {
            qrView.setImageBitmap(event.bmp);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "registering receive fragment on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "unregistering receive fragment on the event bus");
        OttoEventBus.getInstance().unregister(this);
        // if still creating a QR while paused then cancel the task.
        if (createQrTask != null) {
            Log.d(TAG, "stopping creation of QR code");
            createQrTask.cancel(true);
            createQrTask = null;
        }
    }

}
