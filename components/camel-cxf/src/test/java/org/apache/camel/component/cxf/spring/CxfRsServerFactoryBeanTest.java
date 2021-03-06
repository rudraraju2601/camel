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
package org.apache.camel.component.cxf.spring;

import java.util.List;

import org.apache.camel.component.cxf.jaxrs.testbean.CustomerService;
import org.apache.camel.component.cxf.spring.CxfRsServerFactoryBeanDefinitionParser.SpringJAXRSServerFactoryBean;
import org.junit.Test;

public class CxfRsServerFactoryBeanTest extends AbstractSpringBeanTestSupport {

    @Override
    protected String[] getApplicationContextFiles() {        
        return new String[]{"org/apache/camel/component/cxf/spring/CxfRsServerFactoryBeans.xml"};
    }
    
    @Test
    public void testCxfRsServerFactoryBean() {
        SpringJAXRSServerFactoryBean sfb1 = (SpringJAXRSServerFactoryBean) ctx.getBean("rsServer1");
        assertEquals("Get a wrong address", sfb1.getAddress(), "http://localhost:9000/server1");        
        List<Class<?>> resource1Classes = sfb1.getResourceClasses();
        assertEquals("Get a wrong size of resouceClasses", resource1Classes.size(), 1);
        assertEquals("Get a wrong resource class", resource1Classes.get(0), CustomerService.class);
        
        SpringJAXRSServerFactoryBean sfb2 = (SpringJAXRSServerFactoryBean) ctx.getBean("rsServer2");
        assertEquals("Get a wrong address", sfb2.getAddress(), "http://localhost:9000/server2");
        sfb2.getResourceClasses();
        List<Class<?>> resource2Classes = sfb2.getResourceClasses();
        assertEquals("Get a wrong size of resouceClasses", resource2Classes.size(), 1);
        assertEquals("Get a wrong resource class", resource2Classes.get(0), CustomerService.class);        
    }

}
