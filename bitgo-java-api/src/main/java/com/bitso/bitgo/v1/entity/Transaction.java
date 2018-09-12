package com.bitso.bitgo.v1.entity;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author kushal256
 * Date: 9/12/18
 */
@Data
public class Transaction {
    private String blockhash;
    private long confirmations;
    private Date date;//   "date": "2015-01-16T01:12:52.000Z",
    //  private    List<Entry> entries;
    private long fee, height;
    private String id;
    private List<Input> inputs;
    private List<Output> outputs;
    private boolean pending, instant;
    private String instantId;
    private String sequenceId;
    private String comment;
    private List<String> replayProtection;
}
