package examples;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;

public class FirstTest extends CamelTestSupport {

    public void setUp() throws Exception {
        deleteDirectory("target/inbox");
        deleteDirectory("target/outbox");
        super.setUp();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new FileMoveRoute();
    }

    @Test
    public void testMoveFile() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        template.sendBodyAndHeader("file://target/inbox", "Hello World", Exchange.FILE_NAME, "hello.txt");

        assertTrue(notify.matchesMockWaitTime());

        File target = new File("target/outbox/hello.txt");
        assertTrue("File not moved", target.exists());

        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello World", content);
    }
}
