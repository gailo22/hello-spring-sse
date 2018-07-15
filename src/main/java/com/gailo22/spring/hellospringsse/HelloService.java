package com.gailo22.spring.hellospringsse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class HelloService {

    private static final Logger log = getLogger(HelloService.class);

    @Autowired
    private Executor executorService;

    @Autowired
    private Tracer tracer;


    public CompletableFuture<String> getAsync(String no) {
        Span createItemSpan = tracer.createSpan("getAsync");
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignore) {
                }
                log.debug("in getAsync...: " + no);
                return "async world";
            }, executorService);
        } finally {
            tracer.close(createItemSpan);
        }
    }
}
