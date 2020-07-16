package net.kemitix.gucoca.camel.jms;

import lombok.extern.log4j.Log4j2;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.PropertyInject;
import org.apache.camel.component.activemq.ActiveMQComponent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;

@Log4j2
@ApplicationScoped
public class JMSConfiguration {

    @PropertyInject("gucoca.jms.brokerurl")
    String brokerUrl;

    @Produces
    @ApplicationScoped
    ActiveMQComponent jmsComponent(ConnectionFactory connectionsFactory) {
        ActiveMQComponent component = new ActiveMQComponent();
        component.setConnectionFactory(connectionsFactory);
        return component;
    }

    @Produces
    @ApplicationScoped
    ConnectionFactory connectionFactory() {
        var connectionFactory = new ActiveMQConnectionFactory();
        log.info("JMS Broker: {}", brokerUrl);
        connectionFactory.setBrokerURL(brokerUrl);
        return connectionFactory;
    }

}
