package com.bitso.bitgo.v2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * From https://www.bitgo.com/api/v2/#get-wallet-transaction
 *
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class Transfer {
    private String id;  //ID of the wallet transfer
    private String coin; //Digital currency of the wallet
    private String wallet;  //id of the wallet that sent this transaction
    private String txid; // Transfer hash
    private long height; //	Height of the block

    private Date date; //	Date of the transaction

    private String hex; //	Transfer Hex
    private BigDecimal usd;  //value of the total amount sent in USD
    private BigDecimal usdRate;  //price of the cryptocurrency being sent in USD/base_unit
    private long confirmations; //	Number of confirmations for this transaction
    private String state; //confirmed if corresponding transaction is in a block; otherwise, unconfirmed, pendingApproval, rejected or removed

    @JsonProperty("vSize")  //Jackson tries 'vsize'
    private long vSize; //virtual size of the transaction (exists only if coin supports segwit)
    @JsonProperty("nSegwitInputs")  //Jackson tries 'nsegwitInputs'
    private long nSegwitInputs; //number of inputs spending from segwit addresses (exists only if coin supports segwit)

    //    private List<Entry> entries; //	Array of consolidated address to value objects
    private List<Input> inputs;  //Array of inputs used in the associated transaction (only returned from UTXO coins)
    private List<Output> outputs;  //Array of outputs used in the associated transaction (only returned from UTXO coins)

    private Date confirmedTime;
    private Date createdTime;
    private Date unconfirmedTime;
    private Date signedTime;

    private String type; //"send" or "receive"
    private long value;
    private String valueString;
    private long payGoFee;
    private String payGoFeeString;
    private String label;

    private String feeString; //Transaction fee as a string

    private List<String> tags;

    private List<History> history;


    private String sequenceId;


}
