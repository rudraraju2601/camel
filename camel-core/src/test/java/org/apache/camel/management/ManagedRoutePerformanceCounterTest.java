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
package org.apache.camel.management;

import java.util.Date;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;

/**
 * @version $Revision$
 */
public class ManagedRoutePerformanceCounterTest extends ContextTestSupport {

    @Override
    protected boolean useJmx() {
        return true;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        DefaultManagementNamingStrategy naming = (DefaultManagementNamingStrategy) context.getManagementStrategy().getManagementNamingStrategy();
        naming.setHostName("localhost");
        naming.setDomainName("org.apache.camel");
        return context;
    }

    public void testPerformanceCounterStats() throws Exception {
        // get the stats for the route
        MBeanServer mbeanServer = context.getManagementStrategy().getManagementAgent().getMBeanServer();
        ObjectName on = ObjectName.getInstance("org.apache.camel:context=localhost/camel-1,type=routes,name=\"route1\"");

        getMockEndpoint("mock:result").expectedMessageCount(1);

        template.asyncSendBody("direct:start", "Hello World");

        // cater for slow boxes
        Integer inFlight = null;
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            inFlight = (Integer) mbeanServer.getAttribute(on, "InflightExchanges");
            if (inFlight.longValue() == 1) {
                break;
            }
        }
        assertNotNull("too slow server", inFlight);
        assertEquals(1, inFlight.longValue());

        assertMockEndpointsSatisfied();

        Thread.sleep(3000);

        Long completed = (Long) mbeanServer.getAttribute(on, "ExchangesCompleted");
        assertEquals(1, completed.longValue());

        Long last = (Long) mbeanServer.getAttribute(on, "LastProcessingTime");
        Long total = (Long) mbeanServer.getAttribute(on, "TotalProcessingTime");

        assertTrue("Should take around 3 sec: was " + last, last > 2900);
        assertTrue("Should take around 3 sec: was " + total, total > 2900);

        // send in another message
        template.sendBody("direct:start", "Bye World");

        completed = (Long) mbeanServer.getAttribute(on, "ExchangesCompleted");
        assertEquals(2, completed.longValue());
        last = (Long) mbeanServer.getAttribute(on, "LastProcessingTime");
        total = (Long) mbeanServer.getAttribute(on, "TotalProcessingTime");

        assertTrue("Should take around 3 sec: was " + last, last > 2900);
        assertTrue("Should be around 5 sec now: was " + total, total > 4900);

        Date lastFailed = (Date) mbeanServer.getAttribute(on, "LastExchangeFailureTimestamp");
        Date firstFailed = (Date) mbeanServer.getAttribute(on, "FirstExchangeFailureTimestamp");
        assertNull(lastFailed);
        assertNull(firstFailed);

        inFlight = (Integer) mbeanServer.getAttribute(on, "InflightExchanges");
        assertEquals(0, inFlight.longValue());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("log:foo").delay(3000).to("mock:result");
            }
        };
    }

}
