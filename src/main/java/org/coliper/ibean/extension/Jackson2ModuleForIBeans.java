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
import org.coliper.ibean.IBeanFactory;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;

/**
 * @author alex@coliper.org
 *
 */
public class Jackson2ModuleForIBeans extends Module {
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

    public Jackson2ModuleForIBeans() {
        this(null);
    }

    public Jackson2ModuleForIBeans(IBeanFactory iBeanFactory) {
        this.iBeanFactory = iBeanFactory;
    }

    @Override
    public String getModuleName() {
        return ClassUtils.getShortClassName(this.getClass());
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new IBeanDeserializers(iBeanFactory));
    }

}
