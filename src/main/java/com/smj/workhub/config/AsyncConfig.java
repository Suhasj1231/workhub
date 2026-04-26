package com.smj.workhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);      // minimum threads
        executor.setMaxPoolSize(10);      // maximum threads
        executor.setQueueCapacity(50);    // queue size before spawning new threads
        executor.setThreadNamePrefix("Async-");

        executor.initialize();
        return executor;
    }
}


// todo : explation of the above with an example scenario