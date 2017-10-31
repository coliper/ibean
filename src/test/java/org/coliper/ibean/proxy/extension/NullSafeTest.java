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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;

import org.coliper.ibean.extension.BeanFrozenException;
import org.coliper.ibean.extension.BeanIncompleteException;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.extension.NullSafetyException;
import org.coliper.ibean.extension.OptionalSupport;
import org.coliper.ibean.extension.TempFreezable;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class NullSafeTest {

    public static interface NullSafeBean extends NullSafe {
      //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);
      //@formatter:on
    }
    
    @Test()
    public void testNullSafetyExceptionAfterCreation() throws Exception {
        NullSafeBean bean = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build()
                .create(NullSafeBean.class);
        bean.getInt();
        assertThatExceptionOfType(NullSafetyException.class).isThrownBy(() -> bean.getString());
    }
    
    @Test()
    public void testNullSafetyExceptionFieldSetBackToNull() throws Exception {
        NullSafeBean bean = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build()
                .create(NullSafeBean.class);
        bean.getInt();
        bean.setString("3242342");
        bean.getString();
        bean.setString(null);
        assertThatExceptionOfType(NullSafetyException.class).isThrownBy(() -> bean.getString());
    }
}
