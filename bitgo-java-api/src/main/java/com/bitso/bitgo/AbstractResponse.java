package com.bitso.bitgo;

import lombok.Data;

/**
 * An abstract response object from BitGo.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 4:48 PM
 */
@Data
public abstract class AbstractResponse {

    private String status;
    private String error;
    private int responseCode;
}
