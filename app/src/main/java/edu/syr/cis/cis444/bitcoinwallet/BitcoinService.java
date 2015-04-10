package edu.syr.cis.cis444.bitcoinwallet;

import android.util.Log;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.utils.BriefLogFormatter;

import java.io.File;
import java.io.IOException;

/**
 * Created by kelly on 4/9/15.
 */
public class BitcoinService {

    private static final String TAG = BitcoinService.class.getSimpleName();
    private String btcNetwork = "test";
    NetworkParameters btcNetParams;
    final File walletFile = new File("androidApp.wallet");
    Wallet wallet = null;

    // Default constructor
    public BitcoinService() {
        if (btcNetwork.equalsIgnoreCase("test"))
            btcNetParams = TestNet3Params.get();
        else if (btcNetwork.equalsIgnoreCase("prod"))
            btcNetParams = MainNetParams.get();
        initWallet();
    }

    // load wallet from file if it exists
    // otherwise create and save a new wallet
    public void initWallet() {
        int numberOfKeysInWallet = 2;
        this.wallet = new Wallet(btcNetParams);
        Boolean loadedWalletFromFile = false;
        if (this.walletFile.exists()) {
            try {
                this.wallet.loadFromFile(this.walletFile);
                loadedWalletFromFile = true;
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }
        }
        if (!loadedWalletFromFile) {
            for (int i = 0; i < numberOfKeysInWallet; i += 1) {
                this.wallet.importKey(createKey());
            }
            try {
                this.wallet.saveToFile(this.walletFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < this.wallet.getKeychainSize(); i += 1) {
            Log.d(TAG, "wallet key " + i + " " + this.wallet.getImportedKeys().get(i));
            Log.d(TAG, "wallet key " + i + " address on " + btcNetwork + " blockchain is"
                    + this.wallet.getImportedKeys().get(i).toAddress(btcNetParams));
        }
        Log.d(TAG, "wallet contents: " + this.wallet);

    }

    public ECKey createKey() {
        ECKey key = new ECKey();
        Log.d(TAG, "created key: " + key);

        return key;
    }

    public void printKey(ECKey key) {
        Log.d(TAG, "key: " + key);
    }

    public void printKeyAddress(ECKey key) {
        Address address = key.toAddress(btcNetParams);
        Log.d(TAG, "key address on " + btcNetwork + " blockchain is " + address);
    }

}
