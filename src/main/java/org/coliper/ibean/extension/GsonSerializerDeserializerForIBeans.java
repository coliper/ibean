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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * JSON serializer/deserializer used with {@link Gson} to convert IBeans with
 * extension interface {@link GsonSupport} from and to JSON.
 * 
 * Three prerequisites need to be fulfilled so that an IBean can be converted to
 * and from JSON with Gson:
 * <ul>
 * <li>Gson version 2.4 or higher must be found on the classpath.</li>
 * <li>IBean must implement extension interface {@link GsonSupport}.</li>
 * <li>Gson used for convertion needs to have
 * {@link GsonSerializerDeserializerForIBeans} added to its configuration.</li>
 * </ul>
 * <p>
 * Following code snippets show an example usage:<br>
 * 
 * <pre>
 * <code>
 *    IBeanFactory factory = null;
 *
 *    // Create a Gson JSON converter with IBean serializer/deserializer configured
 *    final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GsonSupport.class,
 *            new GsonSerializerDeserializerForIBeans(factory)).create();
 *    
 *    // BeanType must extend interface GsonSupport
 *    BeanType someBean = factory.create(BeanType.class);
 *    
 *    // Serialize and deserialize IBean
 *    String json = gson.toJson(someBean);
 *    BeanType deserializedBean = gson.fromJson(json, BeanType.class);
 * </code>
 * </pre>
 * 
 * @author alex@coliper.org
 *
 */
public class GsonSerializerDeserializerForIBeans
        implements JsonSerializer<GsonSupport>, JsonDeserializer<GsonSupport> {

    private final IBeanFactory iBeanFactory;

    /**
     * Creates a new {@code GsonSerializerDeserializerForIBeans} with no
     * {@link IBeanFactory} preset. In this case default factory in
     * {@link IBean} will be used for creation of new IBeans during
     * deserializaion.
     */
    public GsonSerializerDeserializerForIBeans() {
        this(null);
    }

    /**
     * Creates a new {@code GsonSerializerDeserializerForIBeans} with a given
     * {@link IBeanFactory} preset. In this case the provided factory will be
     * used for creation of new IBeans during deserializaion.
     * 
     * @param iBeanFactory
     *            the factory to use by the deserializer to create new IBeans.
     *            If <code>null</code> default factory in {@link IBean} will be
     *            used for creation of new IBeans during deserializaion.
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
