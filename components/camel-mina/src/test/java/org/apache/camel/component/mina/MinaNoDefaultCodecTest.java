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
package org.apache.camel.component.mina;

import java.util.List;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.mina.common.IoFilterChain.Entry;

/**
 * For unit testing the <tt>noDefaultCodec</tt> option.
 */
public class MinaNoDefaultCodecTest extends ContextTestSupport {

    public void testFilter() throws Exception {
        final String uri1 = "mina:tcp://localhost:6321?allowDefaultCodec=false";
        final String uri2 = "mina:tcp://localhost:6322";
        context.addRoutes(new RouteBuilder() {

            public void configure() throws Exception {
                from(uri1).to("mock:result");
                from(uri2).to("mock:result");
            }
        });

        MinaEndpoint endpoint1 = (MinaEndpoint)context.getEndpoint(uri1);
        MinaEndpoint endpoint2 = (MinaEndpoint)context.getEndpoint(uri2);
        List<Entry> filters1 = endpoint1.getAcceptorConfig().getFilterChain().getAll();
        List<Entry> filters2 = endpoint2.getAcceptorConfig().getFilterChain().getAll();
        assertTrue(filters1.size() < filters2.size());
    }

}

