package com.beyondrest.SegreteriaVirtualegRPC;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.List;


public class JwtServerInterceptor implements ServerInterceptor {

    private final JwtParser parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constant.JWT_SIGNING_KEY))).build();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        List<String> protectedMethods = List.of("SegreteriaVirtualeService/GetLibretto",
                "SegreteriaVirtualeService/RegistraAppello");

        if (!protectedMethods.contains(serverCall.getMethodDescriptor().getFullMethodName())) {
            return serverCallHandler.startCall(serverCall, metadata);
        }
        String value = metadata.get(Constant.AUTHORIZATION_METADATA_KEY);

        Status status = Status.OK;
        if (value == null) {
            status = Status.UNAUTHENTICATED.withDescription("Authorization token is missing");
        } else if (!value.startsWith(Constant.BEARER_TYPE)) {
            status = Status.UNAUTHENTICATED.withDescription("Unknown authorization type");
        } else {
            Jws<Claims> claims = null;
            String token = value.substring(Constant.BEARER_TYPE.length()).trim();
            try {
                claims = parser.parseSignedClaims(token);
            } catch (JwtException e) {
                status = Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e);
            }
            if (claims != null) {
                Context ctx = Context.current()
                        .withValue(Constant.CLIENT_ID_CONTEXT_KEY, claims.getPayload().getSubject());
                System.out.println("Client id: " + claims.getPayload().getSubject());
                return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
            }
        }

        serverCall.close(status, new Metadata());
        return new ServerCall.Listener<ReqT>() {
            // noop
        };
    }

}