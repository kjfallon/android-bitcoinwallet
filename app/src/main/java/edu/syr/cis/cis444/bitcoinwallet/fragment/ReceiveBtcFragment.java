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


public class ReceiveBtcFragment extends Fragment {

    private static final String TAG = ReceiveBtcFragment.class.getSimpleName();
    public static Context context;
    private static CreateQrTask createQrTask;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.fragment_receive_btc, container, false);

        TextView receiveAddrView = (TextView) view.findViewById(R.id.textViewFreshReceiveAddress);
        Log.d(TAG, "displaying fresh address as text");
        Address freshAddress = ((MainActivity)this.getActivity()).getBTCService().freshReceiveAddress();
        String freshAddressString = freshAddress.toString();
        receiveAddrView.setText("Send BTC to: " + freshAddressString);

        Log.d(TAG, "displaying QR for fresh address " + freshAddressString );
        // submit QR code creation task to bus
        Log.d(TAG, "submitting CreateQrTask to bus");
        createQrTask = new CreateQrTask();
        createQrTask.execute(freshAddressString,"");

        return view;
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

    private String createBtcProtocolUri(String address, String btcAmount ) {

        String uri = "bitcoin:" + address;
        if (!TextUtils.isEmpty(btcAmount)) {
            uri += "?amount=" + btcAmount;
        }
        return uri;
    }

    private Bitmap createQR(String freshAddress, String btcAmount) {

        //IntentIntegrator integrator = new IntentIntegrator(ReceiveBtcActivity.this);
        //integrator.shareText(createBtcProtocolUri(freshAddress, btcAmount));

        // usage of xzing to generate a qr code follows combination of examples given at:
        // http://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application
        QRCodeWriter writer = new QRCodeWriter();
        Bitmap bmp = null;
        try {
            int width = 512;
            int height = 512;
            BitMatrix bitMatrix = writer.encode(createBtcProtocolUri(freshAddress, btcAmount), BarcodeFormat.QR_CODE, width, height);
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            Log.e("QR creation error", ""+e);
        }

        return bmp;
    }

}
