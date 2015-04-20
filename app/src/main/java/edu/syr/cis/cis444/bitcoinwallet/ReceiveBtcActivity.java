package edu.syr.cis.cis444.bitcoinwallet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bitcoinj.core.Address;


public class ReceiveBtcActivity extends Activity {

    private static final String TAG = ReceiveBtcActivity.class.getSimpleName();
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Log.d(TAG, "creating btcService...");
        BitcoinService btcService = new BitcoinService(context);
        setContentView(R.layout.activity_receive_btc);

        TextView receiveAddrView = (TextView) findViewById(R.id.textViewFreshReceiveAddress);
        Log.d(TAG, "displaying fresh address as text");
        Address freshAddress = btcService.wallet.freshReceiveAddress();
        String freshAddressString = freshAddress.toString();
        receiveAddrView.setText("Send BTC to: " + freshAddressString);

        Log.d(TAG, "displaying fresh address as QR code");
        ImageView qrView = (ImageView) findViewById(R.id.imageViewReceiveQR);
        Bitmap qrCode = createQR(freshAddressString,"");
        qrView.setImageBitmap(qrCode);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receive_btc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /** Called when the user clicks the Main Menu button */
    public void mainMenu(View view) {
        finish();
    }

}
