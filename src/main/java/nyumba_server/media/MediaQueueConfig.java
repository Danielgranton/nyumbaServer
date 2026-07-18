package nyumba_server.media;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaQueueConfig {

    @Value("${media.queue.name}")
    private String jobsQueue;

    @Value("${media.callback.queue}")
    private String callbackQueue;

    @Bean
    public Queue mediaJobsQueue() {
        return QueueBuilder.durable(jobsQueue).build();
    }

    @Bean
    public Queue mediaCallbackQueue() {
        return QueueBuilder.durable(callbackQueue).build();
    }

    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonConverter());
        return template;
    }
}