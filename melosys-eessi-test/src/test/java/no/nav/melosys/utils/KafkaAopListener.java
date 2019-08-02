package no.nav.melosys.utils;

import java.util.Optional;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KafkaAopListener implements Ordered {

    @Autowired
    private KafkaListenerLatchService latchService;

    @Getter
    private Optional<Throwable> exception;

    @Around("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public Object onInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            exception = Optional.empty();
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            this.exception = Optional.of(throwable);
            throw throwable;
        } finally {
            latchService.countDown();
        }
    }

    @Override
    public int getOrder() {
        // so we get outside the transactional boundry
        return -10;
    }
}