package com.thoughtworks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.spring.jms.AxonJMSMessageSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.EnableJms;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.IS_GETTER;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@EntityScan
@EnableJms
public class Application
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManagerProvider entityManagerProvider;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionManager transactionManager;

    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder() {
            @Override
            public void configure(ObjectMapper objectMapper) {
                super.configure(objectMapper);
                objectMapper.setVisibility(FIELD, ANY)
                        .setVisibility(GETTER, NONE)
                        .setVisibility(IS_GETTER, NONE);
            }
        }.findModulesViaServiceLoader(true);
    }

    @Bean
    Serializer serializer() {
        return new JacksonSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnClass(AxonJMSMessageSource.class)
    AxonJMSMessageSource jmsMessageSource() {
        return new AxonJMSMessageSource();
    }
    public static void main( String[] args )
    {
        SpringApplication.run(Application.class, args);
    }
}
