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

import org.coliper.ibean.IBean;
import org.coliper.ibean.IBeanFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author alex@coliper.org
 *
 */
class Jackson2DeserializerForIBeans extends JsonDeserializer<Jackson2Support> {

    private final IBeanFactory iBeanFactory;
    private final Class<? extends Jackson2Support> beanType;

    /**
     * @param factory
     */
    public Jackson2DeserializerForIBeans(IBeanFactory factory,
            Class<? extends Jackson2Support> beanType) {
        this.iBeanFactory = factory;
        this.beanType = beanType;
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
     * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
     * .jackson.core.JsonParser,
     * com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public Jackson2Support deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        final Jackson2Support bean = this.createBean(this.beanType);
        bean.readFromJsonParser(p, ctxt);
        return bean;
    }

}
