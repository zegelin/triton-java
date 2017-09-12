package com.zegelin.joyent.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@Priority(Priorities.HEADER_DECORATOR)
public class APIVersionFilter implements ClientRequestFilter {
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("Accept-Version", "~8");
    }
}
