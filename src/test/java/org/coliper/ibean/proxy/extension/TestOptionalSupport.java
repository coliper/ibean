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

package org.coliper.ibean.proxy.extension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.coliper.ibean.extension.OptionalSupport;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class TestOptionalSupport {

    public static interface BeanType extends OptionalSupport {
      //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);

        void setDouble(Optional<Double> d);
        Optional<Double> getDouble();
      //@formatter:on
    }

    @Test
    public void test() throws Exception {
        BeanType bean = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build()
                .create(BeanType.class);
        assertThat(bean.getString()).isNull();
        assertThat(bean.getDouble()).isNotNull();
        assertThat(bean.getDouble().isPresent()).isFalse();

        bean.setDouble(null);
        assertThat(bean.getString()).isNull();
        assertThat(bean.getDouble()).isNotNull();
        assertThat(bean.getDouble().isPresent()).isFalse();

        bean.setDouble(Optional.of(Double.valueOf(3.33)));
        assertThat(bean.getString()).isNull();
        assertThat(bean.getDouble()).isNotNull();
        assertThat(bean.getDouble().get()).isEqualTo(3.33);
    }
}
