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

import java.lang.reflect.Type;

import org.coliper.ibean.IBean;
import org.coliper.ibean.IBeanFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author alex@coliper.org
 *
 */
public class GsonSerializerDeserializerForIBeans
        implements JsonSerializer<GsonSupport>, JsonDeserializer<GsonSupport> {

    private final IBeanFactory iBeanFactory;

    /**
     * 
     */
    public GsonSerializerDeserializerForIBeans() {
        this(null);
    }

    /**
     * @param iBeanFactory
     */
    public GsonSerializerDeserializerForIBeans(IBeanFactory iBeanFactory) {
        this.iBeanFactory = iBeanFactory;
    }

    private <T> T createBean(Class<T> beanType) {
        if (this.iBeanFactory != null) {
            return this.iBeanFactory.create(beanType);
        }
        return IBean.newOf(beanType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public GsonSupport deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        @SuppressWarnings("unchecked")
        GsonSupport bean = this.createBean((Class<GsonSupport>) typeOfT);
        bean.readFromJsonObject(json.getAsJsonObject(), context);
        return bean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
     * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
     */
    @Override
    public JsonElement serialize(GsonSupport src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        src.writeToJsonObject(jsonObject, context);
        return jsonObject;
    }

}
