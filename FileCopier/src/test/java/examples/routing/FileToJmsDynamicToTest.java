package examples.routing;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.jms.ConnectionFactory;

public class FileToJmsDynamicToTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context =  super.createCamelContext();

        ConnectionHelper.createActiveMqConnection(context);

        return context;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // load file orders from src/data into the JMS queue
                from("file:data/inbox?noop=true")
                        .setHeader("myDest", constant("incomingOrders"))
                        .toD("jms:${header.myDest}");

                // test that our route is working
                from("jms:incomingOrders")
                        .to("mock:incomingOrders");
            }
        };
    }

    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:incomingOrders").expectedMessageCount(1);
        assertMockEndpointsSatisfied();
    }
}
