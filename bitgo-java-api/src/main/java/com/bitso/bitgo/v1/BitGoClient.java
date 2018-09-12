package com.bitso.bitgo.v1;

import com.bitso.bitgo.v1.entity.WalletTransactionResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * This interface defines the behavior of the BitGo client.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 4:46 PM
 */
public interface BitGoClient {


    Optional<Map<String, Object>> getCurrentUserProfile() throws IOException;


    WalletTransactionResponse listWalletTransctions(String walletId, int skip, int limit) throws IOException;

}
