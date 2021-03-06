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

package org.coliper.ibean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.coliper.ibean.extension.CloneableBean;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.extension.Jackson2Support;
import org.coliper.ibean.extension.ModificationAwareExt;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.extension.TempFreezable;

import com.google.common.collect.ImmutableList;

/**
 * @author alex@coliper.org
 *
 */
public class BeanTestUtil {
    private static final List<Class<?>> EXTENSION_INTERFACES = ImmutableList.of(CloneableBean.class,
            Completable.class, TempFreezable.class, ModificationAwareExt.class, NullSafe.class,
            GsonSupport.class, Jackson2Support.class);

    public static <T> void assertEqualsBean(Class<T> beanType, BeanStyle beanStyle, T bean1,
            T bean2) {
        final boolean requireNestedObjectsSame = true;
        assertEqualsBean(beanType, beanStyle, bean1, bean2, requireNestedObjectsSame);
    }

    public static <T> void assertEqualsBean(Class<T> beanType, BeanStyle beanStyle, T bean1,
            T bean2, boolean requireNestedObjectsSame) {
        assertNotNull(bean1);
        assertNotNull(bean2);
        IBeanTypeMetaInfo<T> meta =
                new IBeanMetaInfoParser().parse(beanType, beanStyle, EXTENSION_INTERFACES);
        for (IBeanFieldMetaInfo fieldMeta : meta.fieldMetaInfos()) {
            Object val1;
            Object val2;
            try {
                val1 = fieldMeta.getterMethod().invoke(bean1);
                val2 = fieldMeta.getterMethod().invoke(bean2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (fieldMeta.fieldType().isPrimitive() || !requireNestedObjectsSame) {
                assertEquals(fieldMeta.fieldName(), val1, val2);
            } else {
                assertSame(fieldMeta.fieldName(), val1, val2);
            }
        }
    }

    private BeanTestUtil() {
        // no instances
    }

}
