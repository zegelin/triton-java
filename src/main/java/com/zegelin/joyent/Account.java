package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class Account extends Login<Account> {
    private final UUID id;

    Account(@JsonProperty("id") final UUID id,
            @JsonProperty("login") final String login) {
        super(login);

        this.id = id;
    }
}
