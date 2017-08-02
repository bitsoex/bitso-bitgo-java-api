package com.bitso.bitgo;

import lombok.Getter;
import lombok.Setter;

/**
 * An abstract response object from BitGo.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 4:48 PM
 */
public abstract class AbstractResponse {

    @Setter @Getter
    private String status;
    @Setter @Getter
    private String error;
}
