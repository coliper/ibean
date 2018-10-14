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

import org.coliper.ibean.extension.BeanFrozenException;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.TempFreezable;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public abstract class AbstractFreezableTest {

    public static interface FreezableBean extends Freezable<FreezableBean> {
      //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);
      //@formatter:on
    }

    public static interface FreezableBeanTemp extends TempFreezable<FreezableBeanTemp> {
        //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);
      //@formatter:on
    }

    protected abstract ProxyIBeanFactory createBeanFactory();

    @Test
    public void testIsFrozen() throws Exception {
        FreezableBean bean = this.createBeanFactory().create(FreezableBean.class);
        assertThat(bean.isFrozen()).isFalse();

        bean.setString("xx");
        bean.getInt();
        assertThat(bean.isFrozen()).isFalse();

        bean.setInt(2093);
        bean.getString();
        assertThat(bean.isFrozen()).isFalse();

        assertThat(bean.freeze()).isSameAs(bean);
        assertThat(bean.isFrozen()).isTrue();
        assertThatExceptionOfType(BeanFrozenException.class)
                .isThrownBy(() -> bean.setString("slls"));

        assertThat(bean.getInt()).isEqualTo(2093);
        assertThat(bean.getString()).isEqualTo("xx");
    }

    @Test
    public void testIsFrozenTemp() throws Exception {
        FreezableBeanTemp bean = this.createBeanFactory().create(FreezableBeanTemp.class);
        assertThat(bean.isFrozen()).isFalse();

        bean.setString("xx");
        bean.getInt();
        assertThat(bean.isFrozen()).isFalse();

        bean.setInt(2093);
        bean.getString();
        assertThat(bean.isFrozen()).isFalse();

        assertThat(bean.freeze()).isSameAs(bean);
        assertThat(bean.isFrozen()).isTrue();
        assertThatExceptionOfType(BeanFrozenException.class)
                .isThrownBy(() -> bean.setString("slls"));

        assertThat(bean.getInt()).isEqualTo(2093);
        assertThat(bean.getString()).isEqualTo("xx");

        assertThat(bean.unfreeze()).isSameAs(bean);
        assertThat(bean.isFrozen()).isFalse();

        bean.setInt(233);
        bean.getString();
        assertThat(bean.isFrozen()).isFalse();

        assertThat(bean.freeze()).isSameAs(bean);
        assertThat(bean.isFrozen()).isTrue();
        assertThatExceptionOfType(BeanFrozenException.class)
                .isThrownBy(() -> bean.setString("slls"));
    }
}
