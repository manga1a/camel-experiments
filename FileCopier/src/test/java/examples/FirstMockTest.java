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
        MockEndpoint quote = getMockEndpoint("mock:quote");
        quote.expectedMessageCount(1);

        //Act
        template.sendBody("stub:jms:topic:quote", "Camel rocks!!!");

        //Assert
        quote.assertIsSatisfied();
    }
}
