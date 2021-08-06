/*
 * Copyright (C) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.coliper.ibean.codegen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.ResourceFinder;

import com.google.common.base.Preconditions;

/**
 * @author alex@coliper.org
 *
 */
class SourceCodeStore extends ResourceFinder {

    private static final String JAVA_FILE_NAME_EXTENSION = ".java";

    private final Map<String, String> fileNameToCodeMap = new ConcurrentHashMap<>(100);
    private final Charset streamingCharset;

    SourceCodeStore(Charset streamingCharset) {
        this.streamingCharset = streamingCharset;
    }

    void addCode(String className, String code) {
        final String fileName = this.createFileNameFromClassName(className);
        Preconditions.checkState(!this.fileNameToCodeMap.containsKey(fileName),
                "cannot add duplicate code for class %s", className);
        this.fileNameToCodeMap.put(fileName, code);
    }

    void removeCode(String className) {
        final String fileName = this.createFileNameFromClassName(className);
        Preconditions.checkState(this.fileNameToCodeMap.containsKey(fileName),
                "code for class %s does not exist", className);
        this.fileNameToCodeMap.remove(fileName);
    }

    private String createFileNameFromClassName(String className) {
        String fileName = className;
        int dollarPos = fileName.indexOf('$');
        if (dollarPos >= 0) {
            fileName = fileName.substring(0, dollarPos);
        }
        fileName = fileName.replaceAll("\\.", "/");
        fileName = fileName + JAVA_FILE_NAME_EXTENSION;
        return fileName;
    }

    @Override
    public Resource findResource(final String resourceName) {
        Objects.requireNonNull(resourceName, "resourceName");
        Preconditions.checkArgument(resourceName.endsWith(JAVA_FILE_NAME_EXTENSION),
                "illegal Java file path: %s", resourceName);
        final String code = this.fileNameToCodeMap.get(resourceName);
        if (code == null) {
            return null;
        }
        return new Resource() {
            private final long UNDETERMINED_MODIFICATION_TIME = 0;

            @Override
            public InputStream open() throws IOException {
                return new ByteArrayInputStream(
                        code.getBytes(SourceCodeStore.this.streamingCharset));
            }

            @Override
            public long lastModified() {
                return UNDETERMINED_MODIFICATION_TIME;
            }

            @Override
            public String getFileName() {
                return resourceName;
            }
        };
    }

}
