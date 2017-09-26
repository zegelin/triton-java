package com.zegelin.joyent;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("WeakerAccess")
public class Endpoint {
    public static final class Joyent {
        public static Endpoint US_EAST_1 = new Endpoint(URI.create("https://us-east-1.api.joyent.com"));
        public static Endpoint US_EAST_2 = new Endpoint(URI.create("https://us-east-2.api.joyent.com"));
        public static Endpoint US_EAST_3 = new Endpoint(URI.create("https://us-east-3.api.joyent.com"));
        public static Endpoint US_EAST_3b = new Endpoint(URI.create("https://us-east-3b.api.joyent.com"));
        public static Endpoint US_SW_1 = new Endpoint(URI.create("https://us-sw-1.api.joyent.com"));
        public static Endpoint US_WEST_1 = new Endpoint(URI.create("https://us-west-1.api.joyent.com"));
        public static Endpoint EU_AMS_1 = new Endpoint(URI.create("https://eu-ams-1.api.joyent.com"));

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
