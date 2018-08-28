package com.bitso.bitgo.entity;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author kushal256
 * Date: 8/28/18
 */
@Data
public class History {
    private ZonedDateTime date;
    private String action;
}
