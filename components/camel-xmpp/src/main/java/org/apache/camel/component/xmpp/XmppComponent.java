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
package org.apache.camel.component.xmpp;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @version $Revision:520964 $
 */
public class XmppComponent extends DefaultComponent {
    private static final transient Log LOG = LogFactory.getLog(XmppComponent.class);

    //keep a cache of endpoints so they can be properly cleaned up
    Map<String, XmppEndpoint> endpointCache = new HashMap<String, XmppEndpoint>();

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        if (endpointCache.containsKey(uri)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using cached endpoint for URI " + uri);
            }
            return endpointCache.get(uri);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating new endpoint for URI " + uri);
        }
        XmppEndpoint endpoint = new XmppEndpoint(uri, this);

        URI u = new URI(uri);
        endpoint.setHost(u.getHost());
        endpoint.setPort(u.getPort());
        if (u.getUserInfo() != null) {
            endpoint.setUser(u.getUserInfo());
        }
        String remainingPath = u.getPath();
        if (remainingPath != null) {
            if (remainingPath.startsWith("/")) {
                remainingPath = remainingPath.substring(1);
            }

            // assume its a participant
            if (remainingPath.length() > 0) {
                endpoint.setParticipant(remainingPath);
            }
        }

        endpointCache.put(uri, endpoint);
        
        return endpoint;
    }

    @Override
    protected synchronized void doStop() throws Exception {
        for (Map.Entry<String, XmppEndpoint> entry : endpointCache.entrySet()) {
            entry.getValue().destroy();
        }
        endpointCache.clear();
    }
}
