package com.gailo22.spring.hellospringsse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class HelloController {

    private static final Logger log = getLogger(HelloController.class);

    private Map<String, SseEmitter> sses = new ConcurrentHashMap<>();

    @Autowired
    private MusicService musicService;

    @Autowired
    private HelloService helloService;

    @GetMapping("/api/hello/{name}")
    public SseEmitter hello(@PathVariable String name) {
        SseEmitter sse = new SseEmitter(60 * 1000L);
        sses.put(name, sse);
        return sse;
    }

    @GetMapping("/api/say/{message}")
    public void say(@PathVariable String message) throws IOException {
        sses.forEach((k, v) -> {
            try {
                v.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @GetMapping("/api/hello-async/{name}")
    public String helloAsync(@PathVariable String name) {
        log.debug("before call getAsync");

        List<CompletableFuture<String>> asyncList =
            Arrays.asList(helloService.getAsync("1"), helloService.getAsync("2"));

        sequence(asyncList).thenAccept(System.out::println).join();

        log.debug("after call getAsync");
        return "done";
    }

    @GetMapping("/api/music/{instrument}")
    public String playMusic(@PathVariable String instrument) {
        return musicService.play(instrument);
    }


    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[0]))
            .thenApply(v -> com.stream()
                .map(CompletableFuture::join)
                .collect(toList())
            );
    }
}
