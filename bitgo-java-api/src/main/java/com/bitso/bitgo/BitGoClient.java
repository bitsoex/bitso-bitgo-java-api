package com.bitso.bitgo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This interface defines the behavior of the BitGo client.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 4:46 PM
 */
public interface BitGoClient {

    Optional<String> login(String email, String password, String otp, boolean extensible)
            throws IOException;

    /** Get all wallets for the user. */
    List<Wallet> getWallets() throws IOException;

    /** Get the wallet with specified ID. */
    Optional<Wallet> getWallet(String wid) throws IOException;

    /** Invokes the sendmany method.
     * @param walletId The ID of the source wallet.
     * @param walletPass The wallet passphrase.
     * @param recipients A map with the recipients' addresses as keys and the corresponding
     * amounts as values. Amounts are in BTC.
     * @param sequenceId A unique identifier for this transaction (optional).
     * @param message Notes about the transaction (optional).
     * @param fee Fee (in BTC), leave null for autodetect. Do not specify unless you are sure it is sufficient.
     * @param feeTxConfirmTarget Calculate fees per kilobyte, targeting transaction confirmation in this number of blocks. Default: 2, Minimum: 2, Maximum: 20
     * @param minConfirms only choose unspent inputs with a certain number of confirmations. We recommend setting this to 1 and using enforceMinConfirmsForChange
     * @param enforceMinConfirmsForChange Defaults to false. When constructing a transaction, minConfirms will only be enforced for unspents not originating from the wallet
     * @return A SendCoinsResponse, or empty if there was a problem (although more likely in case of a problem it will throw).
     */
    Optional<SendCoinsResponse> sendMany(String walletId, String walletPass,
                                         Map<String,BigDecimal> recipients,
                                         String sequenceId, String message,
                                         BigDecimal fee, BigDecimal feeTxConfirmTarget,
                                         int minConfirms, boolean enforceMinConfirmsForChange)
            throws IOException;

}
