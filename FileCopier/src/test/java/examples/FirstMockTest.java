package examples;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class FirstMockTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("stub:jms:topic:quote").to("mock:quote");
            }
        };
    }

    @Test
    public void testQuote() throws Exception {
        //Arrange
        final String body1 = "Camel Rocks!!!";
        final String body2 = "Hello Camel";

        MockEndpoint quote = getMockEndpoint("mock:quote");
        quote.expectedMessageCount(2);
        //quote.expectedBodiesReceived(body1, body2);
        quote.expectedBodiesReceivedInAnyOrder(body2, body1);

        //Act
        template.sendBody("stub:jms:topic:quote", body1);
        template.sendBody("stub:jms:topic:quote", body2);

        //Assert
        quote.assertIsSatisfied();
    }
}
