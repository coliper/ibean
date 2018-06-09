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

import org.apache.commons.lang3.ClassUtils;
import org.coliper.ibean.IBean;
import org.coliper.ibean.IBeanFactory;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.Deserializers;

/**
 * Jackson2 configuration {@link Module} for IBean support. Needs to be
 * configured in a Jackson2 {@link ObjectMapper}s to enable JSON serialization
 * and deserialization of IBeans. Three prerequisites need to be fulfilled so
 * that an IBean can be converted to and from JSON with Jackson2:
 * <ul>
 * <li>Jackson2 version 2.6 or higher must be found on the classpath.</li>
 * <li>IBean must implement extension interface {@link Jackson2Support}.</li>
 * <li>ObjectMapper used for convertion needs to have
 * {@link Jackson2ModuleForIBeans} added to its configuration.</li>
 * </ul>
 * <p>
 * Following code snippets show an example usage:<br>
 * <code><pre>
 *     IBeanFactory factory = ...;
 *     // Interface BeanType needs to extend Jackson2Support
 *     BeanType someBean = factory.create(BeanType.class);
 *     ...
 *     
 *     // Configure Jackson ObjectMapper
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.registerModule(new Jackson2ModuleForIBeans(factory));
 *     
 *     // Serialize to JSON and back
 *     String json = mapper.writeValueAsString(someBean);
 *     BeanType deserializedBean = mapper.readValue(json, BeanType.class);
 * </pre></code>
 * 
 * @author alex@coliper.org
 */
public class Jackson2ModuleForIBeans extends Module {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private static class IBeanDeserializers extends Deserializers.Base {

        private final IBeanFactory iBeanFactory;

        IBeanDeserializers(IBeanFactory iBeanFactory) {
            this.iBeanFactory = iBeanFactory;
        }

        @SuppressWarnings("unchecked")
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
                BeanDescription beanDesc) throws JsonMappingException {
            if (Jackson2Support.class.isAssignableFrom(type.getRawClass())) {
                return new Jackson2DeserializerForIBeans(this.iBeanFactory,
                        (Class<? extends Jackson2Support>) type.getRawClass());
            }
            return null;
        }

    }

    private final IBeanFactory iBeanFactory;

    /**
     * Creates a {@code Jackson2ModuleForIBeans} without having a
     * {@link IBeanFactory} provided. In this case the deserializer will use
     * default factory in {@link IBean} when creating new IBeans.
     */
    public Jackson2ModuleForIBeans() {
        this(null);
    }

    /**
     * Creates a {@code Jackson2ModuleForIBeans} for a given
     * {@link IBeanFactory}. In this case the JSON deserializer will use the
     * provides factory in {@link IBean} when creating new IBeans.
     * 
     * @param iBeanFactory
     *            the factory used by the deserializer; if <code>null</code> the
     *            deserializer will use the default factory in {@link IBean} for
     *            IBean creation.
     */
    public Jackson2ModuleForIBeans(IBeanFactory iBeanFactory) {
        this.iBeanFactory = iBeanFactory;
    }

    /**
     * Returns &quot;Jackson2ModuleForIBeans&quot; as name of the Jackson2
     * {@link Module}.
     * 
     * @see Module#getModuleName()
     */
    @Override
    public String getModuleName() {
        return ClassUtils.getShortClassName(this.getClass());
    }

    /**
     * Returns {@code Version.unknownVersion()}.
     * 
     * @see Module#version()
     */
    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    /**
     * Registers a deserializer factory ({@link Deserializers}) that supports
     * any {@link Jackson2Support} sub-interface.
     * 
     * @see Module#setupModule(SetupContext)
     */
    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new IBeanDeserializers(iBeanFactory));
    }

}
