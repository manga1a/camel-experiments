package examples.routing;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CbrTest extends CamelTestSupport {

    public void setUp() throws Exception {
        deleteDirectory("target/inbox");
        deleteDirectory("target/outbox");
        super.setUp();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        ConnectionHelper.createActiveMqConnection(context);

        return context;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //load file orders into JMS queue
                from("file://target/inbox").to("jms:incomingOrders");

                //content-based router
                from("jms:incomingOrders")
                        .choice()
                        .when(header(Exchange.FILE_NAME).endsWith(".xml"))
                            .to("jms:xmlOrders")
                        .when(header(Exchange.FILE_NAME).endsWith(".csv"))
                            .to("jms:csvOrders");

                //test the route is working
                from("jms:xmlOrders")
                        .log("Received XML order: ${header.CamelFileName}")
                        .to("mock:xml");

                from("jms:csvOrders")
                        .log("Received CSV order: ${header.CamelFileName}")
                        .to("mock:csv");
            }
        };
    }

    @Test
    public void testPlacingOrders() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        String xmlBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<order name=\"motor\" amount=\"1\" customer=\"honda\"/>";
        template.sendBodyAndHeader("file://target/inbox", xmlBody, Exchange.FILE_NAME, "message1.xml");

        String csvBody = "\"name\", \"amount\", \"customer\"\n" +
                "\"brake pad\", \"2\", \"ktm\"\n";
        template.sendBodyAndHeader("file://target/inbox", csvBody, Exchange.FILE_NAME, "message2.csv");

        assertTrue(notify.matchesMockWaitTime());

        getMockEndpoint("mock:xml").expectedMessageCount(1);
        getMockEndpoint("mock:csv").expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}
