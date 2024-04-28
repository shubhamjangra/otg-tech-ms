package com.otg.tech.logging.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@SuppressWarnings("unused")
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)"
            + " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("within(com.au..*) || within(*.controller..*) || within(*.service..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }


    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getDeclaringTypeName();
        Object entryArgs = joinPoint.getArgs();
        if (name.contains("controller"))
            log.info("Enter : {} in method {}() with argument[s] = {}", name, joinPoint.getSignature().getName(),
                    convertResultToJson(entryArgs));
        else
            log.info("Enter : {} in method {}()", name, joinPoint.getSignature().getName());

        try {
            Object exitArgs = joinPoint.proceed();

            if (name.contains("controller"))
                log.info("Exit  : {} from method {}() with result = {}", name, joinPoint.getSignature().getName(),
                        convertResultToJson(exitArgs));
            else
                log.info("Exit : {} in method {}()", name, joinPoint.getSignature().getName());

            return exitArgs;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }

    private String convertResultToJson(Object result) {
        try {
            if (result != null) {
                return objectMapper.writeValueAsString(result);
            } else {
                return "No args.";
            }
        } catch (Exception e) {
            return "Failed to convert result to JSON: " + e.getMessage();
        }
    }
}
