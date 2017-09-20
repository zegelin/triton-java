package com.zegelin.joyent;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Login<T> {
    public final String username;

    Login(final String username) {
        this.username = username;
    }

    static <T> Login<T> forUsername(final String username) {
        return new Login<T>(username);
    }
}
