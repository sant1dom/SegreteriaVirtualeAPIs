package com.beyondrest.SegreteriaVirtualegRPC;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.concurrent.Executor;

public class JwtCredential extends CallCredentials {

    public String token;

    JwtCredential(String token) {
        this.token = token;
    }

    @Override
    public void applyRequestMetadata(final RequestInfo requestInfo, final Executor executor,
                                     final MetadataApplier metadataApplier) {
        // Make a JWT compact serialized string.
        // This example omits setting the expiration, but a real application should do it.
        executor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                headers.put(Constant.AUTHORIZATION_METADATA_KEY,
                        String.format("%s %s", Constant.BEARER_TYPE, token));
                metadataApplier.apply(headers);
            } catch (Throwable e) {
                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
            }
        });
    }

    private static Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Constant.JWT_SIGNING_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateJwt(String subject) {
        return Jwts.builder()
                .subject(subject)
                .signWith(getSignKey())
                .compact();
    }
}
