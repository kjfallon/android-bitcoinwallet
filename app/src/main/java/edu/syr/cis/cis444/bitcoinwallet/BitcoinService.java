package edu.syr.cis.cis444.bitcoinwallet;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ListenableFuture;

import org.bitcoinj.core.AbstractPeerEventListener;
import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.DeterministicSeed;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BitcoinService {

    private static final String TAG = BitcoinService.class.getSimpleName();
    private String btcNetwork = "test";
    NetworkParameters btcNetParams;

    File walletFile = null;
    File spvBlockChainFile = null;
    Wallet wallet = null;
    PeerGroup peerGroup = null;
    Context context = null;

    public BitcoinService(Context context) {
        this.context = context;
        if (btcNetwork.equalsIgnoreCase("test")) {
            btcNetParams = TestNet3Params.get();
            walletFile = new File(context.getFilesDir(), "TestNet3.wallet");
            spvBlockChainFile = new File(this.context.getFilesDir(), "TestNet3SpvBlockChainFile.dat");
        }
        else if (btcNetwork.equalsIgnoreCase("prod")) {
            btcNetParams = MainNetParams.get();
            walletFile = new File(context.getFilesDir(), "ProdBitcoin.wallet");
            spvBlockChainFile = new File(this.context.getFilesDir(), "ProdBitcoinSpvBlockChainFile.dat");
        }

        initWallet();
    }

    // load wallet from file if it exists
    // otherwise create and save a new wallet
    public void initWallet() {

        Boolean successfullyLoadedWalletFromDisk = false;
        this.wallet = new Wallet(btcNetParams);
        if (this.walletFile.exists()) {
            try {
                this.wallet.loadFromFile(this.walletFile);
                successfullyLoadedWalletFromDisk = true;
                Log.d(TAG, "Loaded wallet file from disk");
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }
        }
        if (!successfullyLoadedWalletFromDisk)  {
            try {
                this.wallet.saveToFile(this.walletFile);
                Log.d(TAG, "Created new wallet");
            } catch (IOException e) {
                e.printStackTrace();
            }
            DeterministicSeed seed = this.wallet.getKeyChainSeed();
            Log.d(TAG, "Recovery Seed words are: " + Joiner.on(" ").join(seed.getMnemonicCode()));
            Log.d(TAG, "Recovery Seed birthday is: " + seed.getCreationTimeSeconds());
        }

        // specify this wallet should be autosaved as needed
        this.wallet.autosaveToFile(this.walletFile, 200, TimeUnit.MILLISECONDS, null);
        Log.d(TAG, "wallet file name: " + this.walletFile.getName());
        Log.d(TAG, "Number of keys in wallet: " + this.wallet.getKeychainSize());
        Log.d(TAG, "wallet contents: " + this.wallet);

    }

    public void updateWalletFromNetwork() {

        BlockStore blockStore = null;
        BlockChain chain = null;

        if (spvBlockChainFile.exists()) {
            Log.d(TAG, "Found existing blockchain file of size " + spvBlockChainFile.length() + "bytes");
        }
        else {
            Log.d(TAG, "No existing blockchain data found it may take a while to scan the blockchain ledger");
        }
        try {
            blockStore = new SPVBlockStore(btcNetParams, spvBlockChainFile);
            chain = new BlockChain(btcNetParams, this.wallet, blockStore);
            Log.d(TAG, "Known blockchain height: " + chain.getBestChainHeight());
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }

        peerGroup = new PeerGroup(btcNetParams, chain);

        peerGroup.addEventListener(new AbstractPeerEventListener() {
            @Override
        public synchronized void onChainDownloadStarted(Peer peer, int blocksLeft) {
                Log.d(TAG, "PeerGroup chain download started of " + blocksLeft
                        + " blocks from " + peer.getAddress());
            }
        });
/*        peerGroup.addEventListener(new AbstractPeerEventListener() {
            @Override
            public synchronized void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
                Log.d(TAG, "Received block from " + peer.getAddress());
            }
        });*/
        peerGroup.addEventListener(new AbstractPeerEventListener() {
            @Override
            public synchronized void onPeerConnected(Peer peer, int peerCount) {
                Log.d(TAG, peerCount + " peers, new peer: " + peer.getAddress());
            }
        });
        peerGroup.addEventListener(new AbstractPeerEventListener() {
            @Override
            public synchronized void onPeerDisconnected(Peer peer, int peerCount) {
                Log.d(TAG, peerCount + " peers, lost peer: " + peer.getAddress());
            }
        });

        peerGroup.addPeerDiscovery(new DnsDiscovery(btcNetParams));
        peerGroup.startAsync();



        this.wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public synchronized void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                Log.d(TAG, "Wallet Event: onCoinsReceived");
                Log.d(TAG, "Wallet received tx " + tx.getHashAsString());
                Log.d(TAG, "Wallet previous balance " + prevBalance);
                Log.d(TAG, "Wallet new balance " + newBalance);
                Log.d(TAG, tx.toString());
            }
        });
        this.wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public synchronized void onCoinsSent(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                Log.d(TAG, "Wallet Event: onCoinsSent");
                Log.d(TAG, "Wallet Sent tx " + tx.getHashAsString());
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

    public String sendTransaction(String address, String amount) {

        String resultDescription = "Unable to send transaction";
        Address targetAddress = null;

        Log.d(TAG, "Preparing to Send " + amount + "btc to " + address);
        try {
            targetAddress = new Address(btcNetParams, address);
        }
        catch (AddressFormatException e) {
            Log.e(TAG, "invalid address specified");
            e.printStackTrace();
            return resultDescription;
        }
        Coin value = Coin.parseCoin(amount);
        Wallet.SendRequest request = Wallet.SendRequest.to(targetAddress, value);
        try {
            wallet.completeTx(request);
            Log.d(TAG, "created transaction request");
        }
        catch (InsufficientMoneyException e) {
                Log.e(TAG, "wallet does not contain enough to send this amount");
                e.printStackTrace();
                return resultDescription;
        }
        wallet.commitTx(request.tx);
        Log.d(TAG, "committed transaction request to wallet");
        try {
            wallet.saveToFile(this.walletFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Sending transaction to the network");
        ListenableFuture<Transaction> future = peerGroup.broadcastTransaction(request.tx);
        try {
            future.get();
            resultDescription = "Transaction successfully sent";
            Log.d(TAG, resultDescription);
        }
        catch (InterruptedException e) {
            Log.e(TAG, resultDescription);
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            Log.e(TAG, resultDescription);
            e.printStackTrace();
        }

        return resultDescription;
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
    public Address currentReceiveAddress() {
        return this.wallet.currentReceiveAddress();
    }
}
