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

package org.coliper.ibean.beanstyle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.extension.OptionalSupport;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class BeanStyleWithOptionalSupportTest {

    public static interface ClassicBeanWithOptional extends OptionalSupport {

      //@formatter:off 
          void setIntObject(Integer i);
          Optional<Integer> getIntObject();

          void setBooleanPrimitive(boolean b);
          boolean isBooleanPrimitive();

          void setDate(Date d);
          Date getDate();

          void setString(String s);
          String getString();

          Optional<ClassicBeanWithOptional> getSelf();
          void setSelf(ClassicBeanWithOptional s);
      //@formatter:on    
    }

    public static interface ModernBeanWithOptional extends OptionalSupport {

      //@formatter:off 
          ModernBeanWithOptional intObject(Integer i);
          Optional<Integer> intObject();

          ModernBeanWithOptional booleanPrimitive(boolean b);
          boolean booleanPrimitive();

          ModernBeanWithOptional date(Date d);
          Date date();

          ModernBeanWithOptional string(String s);
          String string();

          Optional<ModernBeanWithOptional> self();
          ModernBeanWithOptional self(ModernBeanWithOptional s);
      //@formatter:on    
    }

    private static void checkFieldInfo(IBeanFieldMetaInfo meta, String fieldName, Class<?> type,
            String getterName, String setterName) {
        assertEquals(fieldName, meta.fieldName());
        assertSame(type, meta.fieldType());
        assertEquals(getterName, meta.getterMethod().getName());
        assertEquals(0, meta.getterMethod().getParameterTypes().length);
        assertEquals(setterName, meta.setterMethod().getName());
        if (getterName.equals(setterName)) {
            // we "guess" modern style
            assertSame(meta.getterMethod().getDeclaringClass(),
                    meta.setterMethod().getReturnType());
        } else {
            // classic style
            assertSame(void.class, meta.setterMethod().getReturnType());
        }
        assertEquals(1, meta.setterMethod().getParameterTypes().length);
        assertSame(type, meta.setterMethod().getParameterTypes()[0]);
    }

    private final IBeanMetaInfoParser parser;

    /**
     * @param parser
     */
    public BeanStyleWithOptionalSupportTest() {
        this.parser = new IBeanMetaInfoParser();
    }

    @Test
    @Ignore
    public void test() {
        assertThat("generate tests for all bean styles").isEmpty();
    }

    @Test
    public void testClassicBeanWithOptional() {
        IBeanTypeMetaInfo<ClassicBeanWithOptional> meta =
                this.parser.parse(ClassicBeanWithOptional.class, BeanStyle.CLASSIC_WITH_OPTIONAL,
                        Collections.emptyList());
        assertNotNull(meta);
        assertSame(ClassicBeanWithOptional.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC_WITH_OPTIONAL, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(5, fields.size());
        checkFieldInfo(fields.get(0), "booleanPrimitive", boolean.class, "isBooleanPrimitive",
                "setBooleanPrimitive");
        checkFieldInfo(fields.get(1), "date", Date.class, "getDate", "setDate");
        checkFieldInfo(fields.get(2), "intObject", Integer.class, "getIntObject", "setIntObject");
        checkFieldInfo(fields.get(3), "self", ClassicBeanWithOptional.class, "getSelf", "setSelf");
        checkFieldInfo(fields.get(4), "string", String.class, "getString", "setString");
    }

    @Test
    public void testModernBeanWithOptional() {
        IBeanTypeMetaInfo<ModernBeanWithOptional> meta =
                this.parser.parse(ModernBeanWithOptional.class, BeanStyle.MODERN_WITH_OPTIONAL,
                        Collections.emptyList());
        assertNotNull(meta);
        assertSame(ModernBeanWithOptional.class, meta.beanType());
        assertSame(BeanStyle.MODERN_WITH_OPTIONAL, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(5, fields.size());
        checkFieldInfo(fields.get(0), "booleanPrimitive", boolean.class, "booleanPrimitive",
                "booleanPrimitive");
        checkFieldInfo(fields.get(1), "date", Date.class, "date", "date");
        checkFieldInfo(fields.get(2), "intObject", Integer.class, "intObject", "intObject");
        checkFieldInfo(fields.get(3), "self", ModernBeanWithOptional.class, "self", "self");
        checkFieldInfo(fields.get(4), "string", String.class, "string", "string");
    }
}
