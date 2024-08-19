package com.jy.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@Slf4j
public class GlobalTimer {

    private static final HashedWheelTimer timer = new HashedWheelTimer();

    public void submit(Consumer<Timeout> consumer, long delay, TimeUnit timeUnit) {
        log.info("submit task to timer, delay: {}, timeUnit: {}", delay, timeUnit);
        timer.newTimeout(consumer::accept, delay, timeUnit);
    }

    @PostConstruct
    public void init() {
        timer.start();
        log.info("GlobalTimer started");
    }

}
