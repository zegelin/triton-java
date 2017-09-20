package com.zegelin.joyent.filter;

import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.zegelin.joyent.Account;
import com.zegelin.joyent.Login;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

@Priority(Priorities.AUTHENTICATION)
@Provider
public class AuthenticationFilter implements ClientRequestFilter {
    private static final RuntimeDelegate.HeaderDelegate<Date> DATE_HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);

    private final KeyPair keyPair;
    private final String keyId;

    @Inject
    public AuthenticationFilter(final Login<Account> accountLogin, final KeyPair keyPair) throws IOException, NoSuchAlgorithmException {
        this.keyPair = keyPair;

        final String keyFingerprint = Fingerprint.forKeyPair(keyPair);

        this.keyId = String.format("/%s/keys/%s", accountLogin.username, keyFingerprint);
    }

    public void filter(final ClientRequestContext clientRequestContext) throws IOException {
        final MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();

        final Date dateHeader = (Date) headers.getFirst(HttpHeaders.DATE);
        final String encodedDataHeader = DATE_HEADER_DELEGATE.toString(dateHeader);

        final String encodedSignature;
        try {
            final Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());

            signature.update(encodedDataHeader.getBytes(StandardCharsets.UTF_8));
            encodedSignature = Base64.getEncoder().encodeToString(signature.sign());

        } catch (final SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IOException(e);
        }

        headers.add(HttpHeaders.AUTHORIZATION, String.format("Signature keyId=\"%s\",algorithm=\"rsa-sha256\" %s", keyId, encodedSignature));
    }

    static final class Fingerprint {
        static final BaseEncoding HEX_ENCODING = BaseEncoding.base16().lowerCase().withSeparator(":", 2);

        static String forKeyPair(final KeyPair keyPair) throws NoSuchAlgorithmException, IOException {
            Preconditions.checkArgument(keyPair.getPublic() instanceof RSAPublicKey);

            final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

            final byte[] bytes;
            {
                final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

                writeWithLength(dataOutput, "ssh-rsa".getBytes());
                writeWithLength(dataOutput, publicKey.getPublicExponent().toByteArray());
                writeWithLength(dataOutput, publicKey.getModulus().toByteArray());

                bytes = dataOutput.toByteArray();
            }

            final byte[] digest = MessageDigest.getInstance("MD5").digest(bytes);

            return HEX_ENCODING.encode(digest);
        }

        private static void writeWithLength(final DataOutput dataOutput, final byte[] bytes) throws IOException {
            dataOutput.writeInt(bytes.length);
            dataOutput.write(bytes);
        }

        private Fingerprint() {}
    }
}
