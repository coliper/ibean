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

import org.coliper.ibean.extension.ModificationAware;
import org.coliper.ibean.extension.ModificationAwareExt;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class ExtensionModificationAwareTest {

    public static interface BeanType extends ModificationAware {
      //@formatter:off
        String getString();
        void setString(String s);

        int getInt();
        void setInt(int i);

        void setDouble(Double d);
        Double getDouble();
      //@formatter:on
    }
    
    public static interface BeanTypeExt extends BeanType, ModificationAwareExt {
    }

    @Test
    public void test() throws Exception {
        BeanType bean = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build()
                .create(BeanType.class);
        assertThat(bean.isModified()).isFalse();

        bean.resetModified();
        assertThat(bean.isModified()).isFalse();

        bean.getInt();
        assertThat(bean.isModified()).isFalse();

        bean.setInt(209384);
        assertThat(bean.isModified()).isTrue();

        bean.getInt();
        assertThat(bean.isModified()).isTrue();

        bean.setInt(928);
        assertThat(bean.isModified()).isTrue();

        bean.setString("sldfj");
        assertThat(bean.isModified()).isTrue();

        bean.setDouble(new Double(23980754));
        assertThat(bean.isModified()).isTrue();

        bean.setInt(92834);
        assertThat(bean.isModified()).isTrue();

        bean.resetModified();
        assertThat(bean.isModified()).isFalse();

        bean.setString("dkh");
        assertThat(bean.isModified()).isTrue();
    }

    @Test
    public void testExt() throws Exception {
        BeanTypeExt bean = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build()
                .create(BeanTypeExt.class);
        assertThat(bean.isModified()).isFalse();
        assertThat(bean.getModifiedFieldNames()).isEmpty();
        assertThat(bean.allFieldsModified()).isFalse();

        bean.resetModified();
        assertThat(bean.isModified()).isFalse();
        assertThat(bean.getModifiedFieldNames()).isEmpty();
        assertThat(bean.allFieldsModified()).isFalse();

        bean.getInt();
        assertThat(bean.isModified()).isFalse();
        assertThat(bean.getModifiedFieldNames()).isEmpty();
        assertThat(bean.allFieldsModified()).isFalse();

        bean.setInt(209384);
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames()).containsExactlyInAnyOrder(new String[] { "int" });
        assertThat(bean.allFieldsModified()).isFalse();

        bean.getInt();
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames()).containsExactlyInAnyOrder(new String[] { "int" });
        assertThat(bean.allFieldsModified()).isFalse();

        bean.setInt(928);
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames()).containsExactlyInAnyOrder(new String[] { "int" });
        assertThat(bean.allFieldsModified()).isFalse();

        bean.setString("sldfj");
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames())
                .containsExactlyInAnyOrder(new String[] { "int", "string" });
        assertThat(bean.allFieldsModified()).isFalse();

        bean.setDouble(new Double(23980754));
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames())
                .containsExactlyInAnyOrder(new String[] { "int", "string", "double" });
        assertThat(bean.allFieldsModified()).isTrue();

        bean.setInt(92834);
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames())
                .containsExactlyInAnyOrder(new String[] { "int", "string", "double" });
        assertThat(bean.allFieldsModified());

        bean.resetModified();
        assertThat(bean.isModified()).isFalse();
        assertThat(bean.getModifiedFieldNames()).isEmpty();
        assertThat(bean.allFieldsModified()).isFalse();

        bean.setString("dkh");
        assertThat(bean.isModified()).isTrue();
        assertThat(bean.getModifiedFieldNames())
                .containsExactlyInAnyOrder(new String[] { "string" });
        assertThat(bean.allFieldsModified()).isFalse();
    }
}
