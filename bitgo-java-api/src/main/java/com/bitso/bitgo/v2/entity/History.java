package com.bitso.bitgo.v2.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author kushal256
 * Date: 8/28/18
 */
@Data
public class History {
    private Date date;
    private String action;
}
