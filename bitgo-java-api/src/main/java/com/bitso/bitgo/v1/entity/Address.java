package com.bitso.bitgo.v1.entity;

import lombok.Data;

/**
 * @author kushal256
 * Date: 9/18/18
 */
@Data
public class Address {
    private int chain;
    private int index;
    private String path;
    private String address;
}
