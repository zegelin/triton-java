package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account extends Login<Account> {
    Account(@JsonProperty("login") final String login) {
        super(login);
    }
}
