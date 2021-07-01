package examples;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

public class ExpressionsMockTest extends CamelTestSupport {

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
    public void testIsCamelMessage() throws Exception {

        //Arrange
        MockEndpoint mock = getMockEndpoint("mock:quote");
        mock.expectedMessageCount(2);
        mock.allMessages().body().contains("Camel");

        //Act
        template.sendBody("stub:jms:topic:quote", "Hello Camel");
        template.sendBody("stub:jms:topic:quote", "Camel rocks");

        //Assert
        assertMockEndpointsSatisfied();

//        List<Exchange> list = mock.getReceivedExchanges();
//        String body1 = list.get(0).getIn().getBody(String.class);
//        String body2 = list.get(1).getIn().getBody(String.class);
//
//        assertTrue(body1.contains("Camel"));
//        assertTrue(body2.contains("Camel"));
    }
}
