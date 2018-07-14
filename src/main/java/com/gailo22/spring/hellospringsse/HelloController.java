package com.gailo22.spring.hellospringsse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class HelloController {

    private Map<String, SseEmitter> sses = new ConcurrentHashMap<>();

    @Autowired
    private MusicService musicService;

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

    @GetMapping("/api/music/{instrument}")
    public String playMusic(@PathVariable String instrument) {
        return musicService.play(instrument);
    }
}
