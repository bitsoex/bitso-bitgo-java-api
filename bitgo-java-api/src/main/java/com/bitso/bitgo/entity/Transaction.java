package com.bitso.bitgo.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * From https://www.bitgo.com/api/v2/#get-wallet-transaction
 *
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class Transaction {
    private String id; //	Transaction hash
    private String normalizedTxHash; //	Normalized transaction hash
    private String date; //	Date of the transaction
    private String hex; //	Transaction Hex
    private String blockHash; //	Block hash the transaction is apart of
    private long blockHeight; //	Height of the block
    private long blockPosition; //	Position of the block
    private long confirmations; //	Number of confirmations for this transaction
    private long fee; //	Transaction fee
    private String feeString; //Transaction fee as a string
    private int size; //	Size of the transaction in bytes
    private List<String> inputIds; //	Array of the input ids
    private List<Entry> entries; //	Array of consolidated address to value objects
    private String fromWallet; //	id of the wallet that sent this transaction


    private List<Input> inputs; //	Array of source objects containing id, address, and value
    private List<Output> outputs; //	Array of destination objects containing address and value

    private Map<String, List<Input>> inputMap; // key is txid
    private Map<String, List<Output>> outputMap; // key is txid

    /**
     * Done post serializaiton to build our maps for quick lookups
     */
    public void convertInputAndOutputToMap() {
        inputMap = new HashMap<>();
        for (Input input : inputs) {
            List<Input> list = inputMap.get(input.getAddress());
            if (list == null){
                list = new ArrayList<>();
                inputMap.put(input.getAddress(), list);
            }
            list.add(input);
        }

        outputMap = new HashMap<>();
        for (Output output : outputs) {
            List<Output> list = outputMap.get(output.getAddress());
            if (list == null){
                list = new ArrayList<>();
                outputMap.put(output.getAddress(), list);
            }
            list.add(output);
        }

        inputs = null;
        outputs = null;
    }

}
