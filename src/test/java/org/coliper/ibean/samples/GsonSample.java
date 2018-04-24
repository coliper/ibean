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

package org.coliper.ibean.samples;

import static org.assertj.core.api.Assertions.assertThat;

import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.extension.GsonSerializerDeserializerForIBeans;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author alex@coliper.org
 *
 */
public class GsonSample {

    public static interface SampleBeanType extends GsonSupport {
        String getString();

        void setString(String s);
    };

    @Test
    public void testJsonReadWrite() {
        final IBeanFactory beanFactory =
                ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
        final GsonSerializerDeserializerForIBeans typeAdapter =
                new GsonSerializerDeserializerForIBeans(beanFactory);
        final Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(GsonSupport.class, typeAdapter).create();
        SampleBeanType bean = beanFactory.create(SampleBeanType.class);
        bean.setString("ABC");
        String json = gson.toJson(bean);
        assertThat(json).isEqualTo("{\"string\":\"ABC\"}");
        SampleBeanType bean2 = gson.fromJson(json, SampleBeanType.class);
        assertThat(bean2.getString()).isEqualTo(bean.getString()); // "ABC"
    }
}
