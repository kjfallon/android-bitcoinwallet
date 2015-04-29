package edu.syr.cis.cis444.bitcoinwallet.event;

public class BlockChainUpdateCompleteEvent {

    public final String result;

    public BlockChainUpdateCompleteEvent(String result) {
        this.result = result;
    }

}
