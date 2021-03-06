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
package org.apache.camel.component.file.remote;

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileMessage;
import org.apache.camel.util.FileUtil;

/**
 * Represents a remote file of some sort of backing object
 *
 * @param <T> the type of file that these remote endpoints provide
 */
public class RemoteFile<T> extends GenericFile<T> implements Cloneable {

    private String hostname;

    /**
     * Populates the {@link GenericFileMessage} relevant headers
     *
     * @param message the message to populate with headers
     */
    public void populateHeaders(GenericFileMessage<T> message) {
        if (message != null) {
            super.populateHeaders(message);
            message.setHeader("CamelFileHost", getHostname());
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public char getFileSeparator() {
        // always use / as separator for FTP
        return '/';
    }
    
    @Override
    protected boolean isAbsolute(String name) {
        return name.startsWith("" + getFileSeparator());
    }
    
    @Override
    protected String normalizePath(String name) {        
        return name;
    }

    @Override
    public void copyFromPopulateAdditional(GenericFile source, GenericFile result) {
        RemoteFile remoteSource = (RemoteFile) source;
        RemoteFile remoteResult = (RemoteFile) result;

        remoteResult.setHostname(remoteSource.getHostname());
    }

    protected String normalizePathToProtocol(String path) {
        path = super.normalizePathToProtocol(path);        
        // strip leading / for FTP protocol to avoid files with absolute paths
        return FileUtil.stripLeadingSeparator(path);
    }
}
