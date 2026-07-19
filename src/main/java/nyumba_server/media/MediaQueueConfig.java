package nyumba_server.media;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
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
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
                                         MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }
}
