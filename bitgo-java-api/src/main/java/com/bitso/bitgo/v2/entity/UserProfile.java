package com.bitso.bitgo.v2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {
 * "user": {
 * "id": "5b6c7c569a75b87f05d2ccfb51eec873",
 * "username": "asdf@gmail.com",
 * "name": {
 * "full": "first last",
 * "first": "first",
 * "last": "last"
 * },
 * "email": {
 * "email": "asdf@gmail.com",
 * "verified": true
 * },
 * "phone": {
 * "phone": "",
 * "verified": false
 * },
 * "country": "USA",
 * "identity": {
 * "civic": {
 * "state": "unverified"
 * },
 * "kyc": {
 * "failureCount": 0,
 * "overallState": "unverified",
 * "required": false,
 * "available": false,
 * "data": {
 * "state": "unverified"
 * },
 * "documents": {
 * "state": "unverified"
 * },
 * "residency": {
 * "state": "unverified"
 * }
 * },
 * "verified": false
 * },
 * "otpDevices": [
 * {
 * "id": "5b6c7cdf63e0788e053164ac11a5c1d4",
 * "type": "totp",
 * "label": "Google Authenticator",
 * "verified": true
 * }
 * ],
 * "rateLimits": {},
 * "disableReset2FA": false,
 * "currency": {
 * "currency": "USD",
 * "bitcoinUnit": "BTC"
 * },
 * "timezone": "US/Pacific",
 * "isActive": true,
 * "ecdhKeychain": "xpub661MyMwAqRbcFKEUvvjy4T37Jrv2S3wE26HTLSXLTuuQt4BQTaZwFcrVYNRSX631UZYv6YeTxmT53CyGYX8xRqjNkJca6ER6PWYF38zn98w",
 * "referrer": {},
 * "apps": {
 * "coinbase": {}
 * },
 * "forceResetPassword": false,
 * "allowedCoins": [],
 * "agreements": {
 * "termsOfUse": 1,
 * "termsOfUseAcceptanceDate": "2018-08-09T17:42:00.964Z"
 * },
 * "lastLogin": "2018-08-12T04:45:22.115Z",
 * "featureFlags": []
 * }
 * }
 */
@Data
public class UserProfile {

    private String id;

    @JsonProperty("isActive")  //Jackson tries to remove 'is'
    private boolean isActive;

    private String username;
    private Name name;

    @Data
    public static class Name {
        private String first, full, last;
    }
}
