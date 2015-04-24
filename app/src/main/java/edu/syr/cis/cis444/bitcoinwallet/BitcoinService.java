package edu.syr.cis.cis444.bitcoinwallet;

import android.content.Context;
import android.util.Log;

import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BitcoinService {

    private static final String TAG = BitcoinService.class.getSimpleName();
    private String btcNetwork = "test";
    NetworkParameters btcNetParams;

    File walletFile = null;
    File spvBlockChainFile = null;
    Wallet wallet = null;
    Context context = null;

    // Default constructor
    public BitcoinService(Context context) {
        this.context = context;
        if (btcNetwork.equalsIgnoreCase("test"))
            btcNetParams = TestNet3Params.get();
        else if (btcNetwork.equalsIgnoreCase("prod"))
            btcNetParams = MainNetParams.get();
        walletFile = new File(context.getFilesDir(), "BtcWalletApp.wallet");
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
                Log.d(TAG, "Loaded wallet file from disk");
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }
        }
        if (!loadedWalletFromFile) {
            for (int i = 0; i < numberOfKeysInWallet; i += 1) {
                this.wallet.importKey(createKey());
                Log.d(TAG, "Created key");
            }
            try {
                this.wallet.saveToFile(this.walletFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // specify this wallet should be autosaved as needed
        this.wallet.autosaveToFile(this.walletFile, 200, TimeUnit.MILLISECONDS, null);

        Log.d(TAG, "Listing keys in wallet");
        Log.d(TAG, "Number of keys in wallet: " + this.wallet.getKeychainSize());
        //for (int i = 0; i < this.wallet.getKeychainSize(); i += 1) {
        //    Log.d(TAG, "wallet key " + i + " " + this.wallet.getImportedKeys().get(i));
        //    Log.d(TAG, "wallet key " + i + " address on " + btcNetwork + " blockchain is"
        //            + this.wallet.getImportedKeys().get(i).toAddress(btcNetParams));
        //}
        Log.d(TAG, "wallet contents: " + this.wallet);

    }

    public void updateWalletFromNetwork() {

        spvBlockChainFile = new File(context.getFilesDir(), "spvBlockChainFile.dat");
        BlockStore blockStore = null;
        BlockChain chain = null;
        try {
            blockStore = new SPVBlockStore(btcNetParams, spvBlockChainFile);
            chain = new BlockChain(btcNetParams, this.wallet, blockStore);
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }

        final PeerGroup peerGroup = new PeerGroup(btcNetParams, chain);
        peerGroup.startAsync();
        this.wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public synchronized void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                Log.d(TAG, "Wallet received tx " + tx.getHashAsString());
                Log.d(TAG, "Wallet previous balance " + prevBalance);
                Log.d(TAG, "Wallet new balance " + newBalance);
                Log.d(TAG, tx.toString());
            }
        });

        // start network activity of collecting relevent blocks from the blockchain
        Log.d(TAG, "Starting download of SPV blocks from blockcain...");
        peerGroup.downloadBlockChain();
        Log.d(TAG, "Completed SPV block download.");
        peerGroup.stopAsync();

        try {
            this.wallet.saveToFile(this.walletFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Coin getBalance() {
        return this.wallet.getBalance();
    }

    public Address freshReceiveAddress() {
        return this.wallet.freshReceiveAddress();
    }
}
