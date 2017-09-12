package com.zegelin.joyent.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Date;

@Priority(Priorities.AUTHENTICATION - 1)
public class RequestDateFilter implements ClientRequestFilter {
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.DATE, new Date());
    }
}
