package org.example.crmsystem.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Profile({"local", "prod"})
public class CustomMetricsAspect {
    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * org.example.crmsystem.controller..*.*(..))")
    public void applicationControllerMethods() {
    }

    @AfterReturning("applicationControllerMethods()")
    public void incrementMetric(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Counter counter = Counter.builder("api.endpoint." + methodName + ".counter")
                .description("Number of Requests for " + methodName)
                .register(meterRegistry);
        counter.increment();
    }
}