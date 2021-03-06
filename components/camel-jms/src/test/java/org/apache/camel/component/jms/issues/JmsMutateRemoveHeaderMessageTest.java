/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jms.issues;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.CamelTestSupport;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

/**
 * @version $Revision$
 */
public class JmsMutateRemoveHeaderMessageTest extends CamelTestSupport {

    private String uri = "activemq:queue:hello";

    public void testMuateMessage() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).body().isEqualTo("Hello World");
        mock.message(0).header("HEADER_1").isNull();

        template.sendBodyAndHeader(uri, "Hello World", "HEADER_1", "VALUE_1");

        assertMockEndpointsSatisfied();
    }

    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        camelContext.addComponent("activemq", activeMQComponent("vm://localhost?broker.persistent=false&broker.useJmx=false"));
        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(uri)
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            // do not mutate it
                            JmsMessage msg = assertIsInstanceOf(JmsMessage.class, exchange.getIn());
                            assertNotNull("javax.jms.Message should not be null", msg.getJmsMessage());

                            // get header should not mutate it
                            assertEquals("VALUE_1", exchange.getIn().getHeader("HEADER_1"));
                        }
                    })
                    // removing a header should mutate it
                    .removeHeader("HEADER_1")
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            // it should have been mutated
                            JmsMessage msg = assertIsInstanceOf(JmsMessage.class, exchange.getIn());
                            assertNotNull("javax.jms.Message should not be null", msg.getJmsMessage());

                            // get header should not mutate it
                            assertNull("Header should have been removed", exchange.getIn().getHeader("HEADER_1"));
                        }
                    })
                    .to("mock:result");
            }
        };
    }

}