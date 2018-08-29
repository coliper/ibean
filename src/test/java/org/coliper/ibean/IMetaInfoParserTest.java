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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class IMetaInfoParserTest {

    private static void checkFieldInfo(IBeanFieldMetaInfo meta, String fieldName, Class<?> type,
            String getterName, String setterName) {
        assertEquals(fieldName, meta.fieldName());
        assertSame(type, meta.fieldType());
        assertEquals(getterName, meta.getterMethod().getName());
        assertSame(type, meta.getterMethod().getReturnType());
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
    public IMetaInfoParserTest() {
        this.parser = new IBeanMetaInfoParser();
    }

    @Test
    public void testSampleBeanClassic() {
        IBeanTypeMetaInfo<SampleBeanClassic> meta = this.parser.parse(SampleBeanClassic.class,
                BeanStyle.CLASSIC, Collections.emptyList());
        assertNotNull(meta);
        assertSame(SampleBeanClassic.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(5, fields.size());
        checkFieldInfo(fields.get(0), "booleanPrimitive", boolean.class, "isBooleanPrimitive",
                "setBooleanPrimitive");
        checkFieldInfo(fields.get(1), "date", Date.class, "getDate", "setDate");
        checkFieldInfo(fields.get(2), "intObject", Integer.class, "getIntObject", "setIntObject");
        checkFieldInfo(fields.get(3), "self", SampleBeanClassic.class, "getSelf", "setSelf");
        checkFieldInfo(fields.get(4), "string", String.class, "getString", "setString");
        assertThat(meta.customEqualsMethod().isPresent()).isFalse();
        assertThat(meta.customHashCodeMethod().isPresent()).isFalse();
    }

    @Test
    public void testSampleBeanModern() {
        IBeanTypeMetaInfo<SampleBeanModern> meta = this.parser.parse(SampleBeanModern.class,
                BeanStyle.MODERN, Collections.emptyList());
        assertNotNull(meta);
        assertSame(SampleBeanModern.class, meta.beanType());
        assertSame(BeanStyle.MODERN, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(5, fields.size());
        checkFieldInfo(fields.get(0), "booleanPrimitive", boolean.class, "booleanPrimitive",
                "booleanPrimitive");
        checkFieldInfo(fields.get(1), "date", Date.class, "date", "date");
        checkFieldInfo(fields.get(2), "intObject", Integer.class, "intObject", "intObject");
        checkFieldInfo(fields.get(3), "self", SampleBeanModern.class, "self", "self");
        checkFieldInfo(fields.get(4), "string", String.class, "string", "string");
        assertThat(meta.customEqualsMethod().isPresent()).isFalse();
        assertThat(meta.customHashCodeMethod().isPresent()).isFalse();
    }

    @Test
    public void testPrimitivesBean() {
        IBeanTypeMetaInfo<PrimitivesBeanClassic> meta = this.parser
                .parse(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, Collections.emptyList());
        assertNotNull(meta);
        assertSame(PrimitivesBeanClassic.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(16, fields.size());
        checkFieldInfo(fields.get(1), "booleanPrimitive", boolean.class, "isBooleanPrimitive",
                "setBooleanPrimitive");
        checkFieldInfo(fields.get(0), "booleanObject", Boolean.class, "getBooleanObject",
                "setBooleanObject");
        checkFieldInfo(fields.get(3), "bytePrimitive", byte.class, "getBytePrimitive",
                "setBytePrimitive");
        checkFieldInfo(fields.get(2), "byteObject", Byte.class, "getByteObject", "setByteObject");
        checkFieldInfo(fields.get(5), "charPrimitive", char.class, "getCharPrimitive",
                "setCharPrimitive");
        checkFieldInfo(fields.get(4), "charObject", Character.class, "getCharObject",
                "setCharObject");
        checkFieldInfo(fields.get(7), "doublePrimitive", double.class, "getDoublePrimitive",
                "setDoublePrimitive");
        checkFieldInfo(fields.get(6), "doubleObject", Double.class, "getDoubleObject",
                "setDoubleObject");
        checkFieldInfo(fields.get(9), "floatPrimitive", float.class, "getFloatPrimitive",
                "setFloatPrimitive");
        checkFieldInfo(fields.get(8), "floatObject", Float.class, "getFloatObject",
                "setFloatObject");
        checkFieldInfo(fields.get(11), "intPrimitive", int.class, "getIntPrimitive",
                "setIntPrimitive");
        checkFieldInfo(fields.get(10), "intObject", Integer.class, "getIntObject", "setIntObject");
        checkFieldInfo(fields.get(13), "longPrimitive", long.class, "getLongPrimitive",
                "setLongPrimitive");
        checkFieldInfo(fields.get(12), "longObject", Long.class, "getLongObject", "setLongObject");
        checkFieldInfo(fields.get(15), "shortPrimitive", short.class, "getShortPrimitive",
                "setShortPrimitive");
        checkFieldInfo(fields.get(14), "shortObject", Short.class, "getShortObject",
                "setShortObject");
        assertThat(meta.customEqualsMethod().isPresent()).isFalse();
        assertThat(meta.customHashCodeMethod().isPresent()).isFalse();
    }

    @Test
    public void testEmptyBean() {
        IBeanTypeMetaInfo<EmptyBean> meta =
                this.parser.parse(EmptyBean.class, BeanStyle.CLASSIC, Collections.emptyList());
        assertNotNull(meta);
        assertSame(EmptyBean.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(0, fields.size());
        assertThat(meta.customEqualsMethod().isPresent()).isFalse();
        assertThat(meta.customHashCodeMethod().isPresent()).isFalse();
    }

    public static interface A {
        void setIntObject(Integer i);

        Integer getIntObject();
    }

    public static interface B extends A {
        void setBooleanPrimitive(boolean b);

        boolean isBooleanPrimitive();
    }

    public static interface C {
        void setDate(Date d);

        Date getDate();
    }

    public static interface Combined extends B, C {
        //@formatter:off 
        void setString(String s);
        String getString();

        SampleBeanClassic getSelf();
        void setSelf(SampleBeanClassic s);
      //@formatter:on 

    }

    @Test
    public void testCombinedType() {
        IBeanTypeMetaInfo<Combined> meta =
                this.parser.parse(Combined.class, BeanStyle.CLASSIC, Collections.emptyList());
        assertNotNull(meta);
        assertSame(Combined.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(5, fields.size());
        checkFieldInfo(fields.get(0), "booleanPrimitive", boolean.class, "isBooleanPrimitive",
                "setBooleanPrimitive");
        checkFieldInfo(fields.get(1), "date", Date.class, "getDate", "setDate");
        checkFieldInfo(fields.get(2), "intObject", Integer.class, "getIntObject", "setIntObject");
        checkFieldInfo(fields.get(3), "self", SampleBeanClassic.class, "getSelf", "setSelf");
        checkFieldInfo(fields.get(4), "string", String.class, "getString", "setString");
        assertThat(meta.customEqualsMethod().isPresent()).isFalse();
        assertThat(meta.customHashCodeMethod().isPresent()).isFalse();
    }

    public static interface FancyList extends List<String> {
    }

    public static interface ExtendedSampleBean
            extends FancyList, SampleBeanClassic, InvocationHandler {
    }

    @Test
    public void testIgnoredTypes() {
        IBeanTypeMetaInfo<Combined> meta = this.parser.parse(Combined.class, BeanStyle.CLASSIC,
                Arrays.asList(InvocationHandler.class, List.class));
        assertNotNull(meta);
        assertSame(Combined.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(5, fields.size());
        checkFieldInfo(fields.get(0), "booleanPrimitive", boolean.class, "isBooleanPrimitive",
                "setBooleanPrimitive");
        checkFieldInfo(fields.get(1), "date", Date.class, "getDate", "setDate");
        checkFieldInfo(fields.get(2), "intObject", Integer.class, "getIntObject", "setIntObject");
        checkFieldInfo(fields.get(3), "self", SampleBeanClassic.class, "getSelf", "setSelf");
        checkFieldInfo(fields.get(4), "string", String.class, "getString", "setString");
    }

    public static interface BeanWithDefaultAndStatic {
        String getStr();

        void setStr(String s);

        default int strLength() {
            return getStr().length();
        }

        static void someStaticMethod() {
        }
    }

    @Test
    public void testDefaultAndStaticMethods() {
        IBeanTypeMetaInfo<BeanWithDefaultAndStatic> meta =
                this.parser.parse(BeanWithDefaultAndStatic.class, BeanStyle.CLASSIC,
                        Arrays.asList(InvocationHandler.class, List.class));
        assertNotNull(meta);
        assertSame(BeanWithDefaultAndStatic.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(1, fields.size());
        checkFieldInfo(fields.get(0), "str", String.class, "getStr", "setStr");
    }

    @Test
    public void testCustomEqualsAndHashCode() {
        IBeanTypeMetaInfo<BeanTypeWithCustomEquals> meta = this.parser
                .parse(BeanTypeWithCustomEquals.class, BeanStyle.CLASSIC, Collections.emptyList());
        assertThat(meta.customEqualsMethod().get().getName()).isEqualTo("_equals");
        assertThat(meta.customHashCodeMethod().get().getName()).isEqualTo("_hashCode");
    }
}
