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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

/**
 * @author alex@coliper.org
 *
 */
public class StringJavaFileObject extends SimpleJavaFileObject {

    final String className;
    final String code;

    /**
     * @param uri
     * @param kind
     * @throws URISyntaxException
     */
    protected StringJavaFileObject(String className, String code) throws URISyntaxException {
        super(new URI("string:///" + className), Kind.SOURCE);
        this.className = className;
        this.code = code;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }

}
