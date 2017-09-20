package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("WeakerAccess")
public class Account extends Login<Account> {
    Account(@JsonProperty("login") final String login) {
        super(login);
    }
}
