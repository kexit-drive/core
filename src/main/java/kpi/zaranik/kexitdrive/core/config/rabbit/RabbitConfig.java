package kpi.zaranik.kexitdrive.core.config.rabbit;

import kpi.zaranik.kexitdrive.core.misc.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean("importFiles")
    public Queue importFiles() {
        return new Queue(Constants.IMPORT_FILES_QUEUE, false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter rabbitJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
