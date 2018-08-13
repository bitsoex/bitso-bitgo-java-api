package com.bitso.bitgo.entity;

/**
 * From https://www.bitgo.com/api/v2/#get-wallet-transaction
 *
 * @author kushalkhan
 * Date: 8/13/18
 */
public class Transaction {
    private String id; //	Transaction hash
    private String normalizedTxHash; //	Normalized transaction hash
    private long date; //	Date of the transaction
    private String hex; //	Transaction Hex
    private String blockHash; //	Block hash the transaction is apart of
    private long blockHeight; //	Height of the block
    private long blockPosition; //	Position of the block
    private long confirmations; //	Number of confirmations for this transaction
    private long fee; //	Transaction fee
    private String feeString; //Transaction fee as a string
    private int size; //	Size of the transaction in bytes
    private String[] inputIds; //	Array of the input ids
    private Input[] inputs; //	Array of source objects containing id, address, and value
    private Output[] outputs; //	Array of destination objects containing address and value
    private Entry[] entries; //	Array of consolidated address to value objects
    private String fromWallet; //	id of the wallet that sent this transaction

}
