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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.JsonAdapter;

/**
 * Extension interface that makes IBeans serializable and deserializable with
 * Gson. If an IBean interface extends {@code GsonSupport} you are able to
 * convert any IBean instance from and to JSON. For this to work you need
 * another two prerequisites:
 * <ul>
 * <li>Gson with version 2.4 or higher need to be in the classpath.</li>
 * <li>{@link GsonSerializerDeserializerForIBeans} needs to be registered in the
 * used {@link Gson}. See {@link GsonSerializerDeserializerForIBeans} for
 * details.</li>
 * </ul>
 * 
 * @see Gson
 * @author alex@coliper.org
 */
@JsonAdapter(GsonSerializerDeserializerForIBeans.class)
public interface GsonSupport {

    /**
     * Reads the field values from a {@link JsonObject} and sets the fields of
     * this IBean to the retrieved values.
     * <p>
     * You normally do not use this method directly. It is mainly called from
     * {@link GsonSerializerDeserializerForIBeans} during JSON deserialization.
     * 
     * @param jsonObject
     *            the {@link JsonObject} reading some JSON representation of
     *            this IBean
     * @param context
     *            the Gson deserialization context
     */
    void readFromJsonObject(JsonObject jsonObject, JsonDeserializationContext context);

    /**
     * Writes the field values of this IBean out into a JsonObject.
     * <p>
     * You normally do not use this method directly. It is mainly called from
     * {@link GsonSerializerDeserializerForIBeans} during JSON serialization.
     * 
     * @param jsonObject
     *            the target where to write the field value to
     * @param context
     *            the Gson serialization context
     */
    void writeToJsonObject(JsonObject jsonObject, JsonSerializationContext context);

}
