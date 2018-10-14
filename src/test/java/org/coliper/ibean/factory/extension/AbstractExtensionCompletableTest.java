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

package org.coliper.ibean.factory.extension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.extension.BeanIncompleteException;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public abstract class AbstractExtensionCompletableTest {

    public static interface BeanType extends Completable<BeanType> {
      //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);

        void setDouble(Optional<Double> d);
        Optional<Double> getDouble();
      //@formatter:on
    }

    protected abstract ProxyIBeanFactory createBeanFactory(final BeanStyle beanStyle);

    @Test
    public void testIsCompleteWithNoOptionalSupportStyle() throws Exception {
        BeanType bean = this.createBeanFactory(BeanStyle.CLASSIC).create(BeanType.class);
        assertThat(bean.isComplete()).isFalse();
        assertThatExceptionOfType(BeanIncompleteException.class)
                .isThrownBy(() -> bean.assertComplete());

        bean.setString("xx");
        assertThat(bean.isComplete()).isFalse();
        assertThatExceptionOfType(BeanIncompleteException.class)
                .isThrownBy(() -> bean.assertComplete());

        bean.setDouble(Optional.empty());
        assertThat(bean.isComplete()).isTrue();
        assertThat(bean.assertComplete()).isSameAs(bean);

        bean.setInt(0);
        assertThat(bean.isComplete()).isTrue();
        assertThat(bean.assertComplete()).isSameAs(bean);

        bean.setDouble(null);
        assertThat(bean.isComplete()).isFalse();
        assertThatExceptionOfType(BeanIncompleteException.class)
                .isThrownBy(() -> bean.assertComplete());

        bean.setInt(0);
        assertThat(bean.isComplete()).isFalse();
        assertThatExceptionOfType(BeanIncompleteException.class)
                .isThrownBy(() -> bean.assertComplete());

        bean.setDouble(Optional.of(Double.valueOf(3.4)));
        assertThat(bean.isComplete()).isTrue();
        assertThat(bean.assertComplete()).isSameAs(bean);
    }

    public static interface BeanTypeWithOptionalSupport
            extends Completable<BeanTypeWithOptionalSupport> {
      //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);

        void setDouble(Double d);
        Optional<Double> getDouble();
      //@formatter:on
    }

    @Test
    public void testIsCompleteWithOptionalSupportStyle() throws Exception {
        final BeanStyle beanStyle = BeanStyle.CLASSIC_WITH_OPTIONAL;
        BeanTypeWithOptionalSupport bean =
                createBeanFactory(beanStyle).create(BeanTypeWithOptionalSupport.class);
        assertThat(bean.isComplete()).isFalse();
        assertThatExceptionOfType(BeanIncompleteException.class)
                .isThrownBy(() -> bean.assertComplete());

        bean.setString("xx");
        assertThat(bean.isComplete()).isTrue();
        assertThat(bean.assertComplete()).isSameAs(bean);

        bean.setDouble(Double.valueOf(7.77));
        assertThat(bean.isComplete()).isTrue();
        assertThat(bean.assertComplete()).isSameAs(bean);

        bean.setInt(0);
        assertThat(bean.isComplete()).isTrue();
        assertThat(bean.assertComplete()).isSameAs(bean);

        bean.setDouble(null);
        assertThat(bean.isComplete()).isTrue();

        bean.setString(null);
        assertThat(bean.isComplete()).isFalse();
        assertThatExceptionOfType(BeanIncompleteException.class)
                .isThrownBy(() -> bean.assertComplete());
    }
}
