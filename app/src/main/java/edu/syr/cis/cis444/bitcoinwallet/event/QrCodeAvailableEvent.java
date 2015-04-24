package edu.syr.cis.cis444.bitcoinwallet.event;

import android.graphics.Bitmap;

public class QrCodeAvailableEvent {

    public final Bitmap bmp;

    public QrCodeAvailableEvent(Bitmap bmp) {
        this.bmp = bmp;
    }

}
