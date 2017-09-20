package com.zegelin.joyent;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("WeakerAccess")
public class Endpoint {
    public static final class Joyent {
        public static Endpoint US_EAST_1 = new Endpoint(URI.create("https://us-east-1.api.joyent.com"));

        private Joyent() {}
    }

    public final URI uri;

    public Endpoint(final URI uri) {
        this.uri = uri;
    }

    public Endpoint(final String uri) throws URISyntaxException {
        this.uri = new URI(uri);
    }
}
