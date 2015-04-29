package edu.syr.cis.cis444.bitcoinwallet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.BtcFormat;

import java.util.List;
import java.util.Set;

import edu.syr.cis.cis444.bitcoinwallet.MainActivity;
import edu.syr.cis.cis444.bitcoinwallet.OttoEventBus;
import edu.syr.cis.cis444.bitcoinwallet.R;
import edu.syr.cis.cis444.bitcoinwallet.event.BlockChainUpdateCompleteEvent;
import edu.syr.cis.cis444.bitcoinwallet.task.RefreshBlockchainTask;

import static android.widget.ImageView.ScaleType.CENTER_INSIDE;


public class WalletInfoFragment extends Fragment {

    private static final String TAG = WalletInfoFragment.class.getSimpleName();
    public static Context context;
    private static RefreshBlockchainTask refreshBlockchainTask;
    BtcFormat btcFormat = BtcFormat.getInstance();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        // submit update from global blockchain task to bus
        CharSequence text = "Refreshing wallet from network";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(((MainActivity)this.getActivity()).getApplicationContext(), text, duration);
        toast.show();
        Log.d(TAG, "submitting RefreshBlockchainTask to bus");
        refreshBlockchainTask = new RefreshBlockchainTask( );
        refreshBlockchainTask.execute( ((MainActivity)this.getActivity()) );

        view = inflater.inflate(R.layout.fragment_wallet_info, container, false);

        Log.d(TAG, "displaying wallet description");
        TextView walletDescription = (TextView) view.findViewById(R.id.textViewWalletDescription);
        walletDescription.setText("Wallet name: " + ((MainActivity)this.getActivity()).getBTCService().getDescription() );

        Log.d(TAG, "displaying wallet version");
        TextView walletVersion = (TextView) view.findViewById(R.id.textViewWalletVersion);
        walletVersion.setText("Wallet version: " + ((MainActivity)this.getActivity()).getBTCService().getVersion() );

        Log.d(TAG, "displaying wallet balance");
        TextView walletBalance = (TextView) view.findViewById(R.id.textViewWalletBalance);
        walletBalance.setText("Wallet Balance: " + btcFormat.format(((MainActivity)this.getActivity()).getBTCService().getBalanceEstimated()) );

        Log.d(TAG, "displaying wallet transaction count");
        TextView walletTxCount = (TextView) view.findViewById(R.id.textViewWalletTransactionCount);
        Set<Transaction> transactions = ((MainActivity)this.getActivity()).getBTCService().getTransactions(true);
        walletTxCount.setText("Wallet tx count: " + transactions.size() );

        List<Transaction> recentTransactions = ((MainActivity)this.getActivity()).getBTCService().getRecentTransactions();
        String transactionList = "Recent transactions:\n";
        for (Transaction transaction : recentTransactions ) {
            transactionList += transaction.getHashAsString() + "\n";
        }
        Log.d(TAG, "displaying wallet transactions");
        TextView walletTx = (TextView) view.findViewById(R.id.textViewWalletTransactions);
        walletTx.setText(transactionList);

        return view;
    }

    @Subscribe
    public void onBlockChainUpdateComplete(BlockChainUpdateCompleteEvent event) {
        Log.d(TAG, "completed updating from the blockchain");
        TextView walletBalance = (TextView) view.findViewById(R.id.textViewWalletBalance);
        walletBalance.setText("Wallet Balance: " + btcFormat.format(((MainActivity) this.getActivity()).getBTCService().getBalance()));
        CharSequence text = "Wallet update complete";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(((MainActivity)this.getActivity()).getApplicationContext(), text, duration);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "registering wallet balance fragment on the event bus");
        OttoEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "unregistering wallet balance fragment on the event bus");
        OttoEventBus.getInstance().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
