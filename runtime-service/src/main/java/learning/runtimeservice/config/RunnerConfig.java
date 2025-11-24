package learning.runtimeservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RunnerConfig {
    public static final String QUEUE = "run.request";
    @Bean
    public Queue codeQueue(){return new Queue(QUEUE, false);}
}
