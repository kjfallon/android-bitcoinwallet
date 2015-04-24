package edu.syr.cis.cis444.bitcoinwallet.task;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.event.QrCodeAvailableEvent;

public class CreateQrTask extends AsyncTask<String, Void, Bitmap> {

    private String createBtcProtocolUri(String address, String btcAmount ) {

        String uri = "bitcoin:" + address;
        if (!TextUtils.isEmpty(btcAmount)) {
            uri += "?amount=" + btcAmount;
        }
        return uri;
    }

    @Override protected Bitmap doInBackground(String... params) {
        // usage of xzing to generate a qr code follows combination of examples given at:
        // http://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application
        QRCodeWriter writer = new QRCodeWriter();
        Bitmap bmp = null;
        try {
            int width = 512;
            int height = 512;
            BitMatrix bitMatrix = writer.encode(createBtcProtocolUri(params[0], params[1]), BarcodeFormat.QR_CODE, width, height);
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

    @Override protected void onPostExecute(Bitmap bmp) {
        if (!isCancelled() && bmp != null) {
            OttoEventBus.getInstance().post(new QrCodeAvailableEvent(bmp));
        }
    }

}
