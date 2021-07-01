package examples;

import org.apache.camel.builder.RouteBuilder;

public class FileMoveRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file://target/inbox")
                .to("file://target/outbox");
    }
}
