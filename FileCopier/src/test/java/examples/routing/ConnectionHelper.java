package examples.routing;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;

import javax.jms.ConnectionFactory;

public class ConnectionHelper {
    public static void createActiveMqConnection(CamelContext context) {
        // connect to embedded ActiveMQ JMS broker
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
    }
}
