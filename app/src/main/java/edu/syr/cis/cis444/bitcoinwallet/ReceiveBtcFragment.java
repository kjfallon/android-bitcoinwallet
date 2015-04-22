package edu.syr.cis.cis444.bitcoinwallet;

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
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bitcoinj.core.Address;


public class ReceiveBtcFragment extends Fragment {

    private static final String TAG = ReceiveBtcFragment.class.getSimpleName();
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_receive_btc, container, false);

        TextView receiveAddrView = (TextView) view.findViewById(R.id.textViewFreshReceiveAddress);
        Log.d(TAG, "displaying fresh address as text");
        Address freshAddress = ((MainActivity)this.getActivity()).getBTCService().wallet.freshReceiveAddress();
        String freshAddressString = freshAddress.toString();
        receiveAddrView.setText("Send BTC to: " + freshAddressString);

        Log.d(TAG, "displaying fresh address as QR code");
        ImageView qrView = (ImageView) view.findViewById(R.id.imageViewReceiveQR);
        Bitmap qrCode = createQR(freshAddressString,"");
        qrView.setImageBitmap(qrCode);

        return view;
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
