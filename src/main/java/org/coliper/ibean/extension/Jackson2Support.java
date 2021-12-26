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

package org.coliper.ibean.extension;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extension interface that makes IBeans serializable and deserializable with
 * Jackson2. If an IBean interface extends {@code Jackson2Support} you are able
 * to convert any IBean instance from and to JSON. For this to work you need
 * another two prerequisites:
 * <ul>
 * <li>jackson-core and jackson-databind with version 2.6 or higher need to be
 * in the classpath.</li>
 * <li>{@link Jackson2ModuleForIBeans} needs to be registered in the used
 * Jackson {@link ObjectMapper}. See {@link Jackson2ModuleForIBeans} for
 * details.</li>
 * </ul>
 * 
 * @see ObjectMapper
 * @author alex@coliper.org
 */
public interface Jackson2Support extends JsonSerializable {

    /**
     * Reads the field values from a {@link JsonParser} and sets the fields of
     * this IBean to the retrieved values.
     * <p>
     * You normally do not use this method directly. It is mainly called from
     * {@link Jackson2ModuleForIBeans} during JSON deserialization.
     * 
     * @param p
     *            the {@link JsonParser} reading some JSON representation of
     *            this IBean
     * @param ctxt
     *            the Jackson2 deserialization context
     */
    void readPropertyValueFromJsonParser(String propertyName, JsonParser p,
            DeserializationContext ctxt) throws IOException;

}
