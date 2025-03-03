package org.example.crmsystem.logging;

import jakarta.servlet.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
public class TransactionLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        org.apache.logging.log4j.ThreadContext.put("transactionId", transactionId);

        log.info("Transaction ID in filter: {}", transactionId);

        try {
            chain.doFilter(request, response);
        } finally {
            org.apache.logging.log4j.ThreadContext.clearAll();
        }
    }
}
