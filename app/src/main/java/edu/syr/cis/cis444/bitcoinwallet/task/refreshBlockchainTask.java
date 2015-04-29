package edu.syr.cis.cis444.bitcoinwallet.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.event.BlockChainUpdateCompleteEvent;

public class RefreshBlockchainTask extends AsyncTask<Context, Void, String> {

    @Override protected String doInBackground(Context... params) {

        ((MainActivity)params[0]).getBTCService().updateWalletFromNetwork();
        return "complete";
    }

    @Override protected void onPostExecute(String result) {
        if (!isCancelled() && result != null) {
            OttoEventBus.getInstance().post(new BlockChainUpdateCompleteEvent(result));
        }
    }
}
