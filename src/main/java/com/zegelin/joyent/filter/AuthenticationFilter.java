package com.zegelin.joyent.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Date;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ClientRequestFilter {
    private final KeyPair keyPair;
    private final String keyId;

    public AuthenticationFilter(final KeyPair keyPair, final String keyId) {
        this.keyPair = keyPair;
        this.keyId = keyId;
    }

    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        final Signature signature;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());

        } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IOException(e);
        }

        final MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();

        final Date date = (Date) headers.getFirst(HttpHeaders.DATE);


        final RuntimeDelegate.HeaderDelegate<Date> headerDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);

        final String dateHeader = headerDelegate.toString(date);


        final String s;

        try {
            signature.update(dateHeader.getBytes(StandardCharsets.UTF_8));
            s = Base64.getEncoder().encodeToString(signature.sign());

        } catch (final SignatureException e) {
            throw new IOException(e);
        }

        headers.add(HttpHeaders.AUTHORIZATION, String.format("Signature keyId=\"%s\",algorithm=\"rsa-sha256\" %s", "/ic_joyent_dev/keys/" + keyId, s));
    }
}
