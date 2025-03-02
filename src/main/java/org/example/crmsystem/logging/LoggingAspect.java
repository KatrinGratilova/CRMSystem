package org.example.crmsystem.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Log4j2
public class LoggingAspect {
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        long duration;
        String transactionId = ThreadContext.get("transactionId");

        try {
            Object result = joinPoint.proceed();
            duration = System.currentTimeMillis() - startTime;

            if (result instanceof ResponseEntity<?> response)
                log.info("Endpoint called: {}, Method: {}, Status: {}, Duration: {} ms, Request: {}, Response: {}, TransactionId: {}"
                        , requestURI, httpMethod, response.getStatusCode(), duration, request, response.getBody(), transactionId);
            return result;
        } catch (Exception e) {
            duration = System.currentTimeMillis() - startTime;

            log.info("Endpoint called: {}, Method: {}, Duration: {} ms, Request: {}, Error: {}, TransactionId: {}"
                    , requestURI, httpMethod, duration, request, e.getMessage(), transactionId);
            throw e;
        }
    }
}