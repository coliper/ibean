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

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

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
            assertSame(meta.getterMethod().getDeclaringClass(), meta.setterMethod().getReturnType());
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
        checkFieldInfo(fields.get(0), "boolean", boolean.class, "isBoolean", "setBoolean");
        checkFieldInfo(fields.get(1), "booleanObject", Boolean.class, "getBooleanObject",
                "setBooleanObject");
        checkFieldInfo(fields.get(2), "byte", byte.class, "getByte", "setByte");
        checkFieldInfo(fields.get(3), "byteObject", Byte.class, "getByteObject", "setByteObject");
        checkFieldInfo(fields.get(4), "char", char.class, "getChar", "setChar");
        checkFieldInfo(fields.get(5), "charObject", Character.class, "getCharObject",
                "setCharObject");
        checkFieldInfo(fields.get(6), "double", double.class, "getDouble", "setDouble");
        checkFieldInfo(fields.get(7), "doubleObject", Double.class, "getDoubleObject",
                "setDoubleObject");
        checkFieldInfo(fields.get(8), "float", float.class, "getFloat", "setFloat");
        checkFieldInfo(fields.get(9), "floatObject", Float.class, "getFloatObject",
                "setFloatObject");
        checkFieldInfo(fields.get(10), "int", int.class, "getInt", "setInt");
        checkFieldInfo(fields.get(11), "intObject", Integer.class, "getIntObject", "setIntObject");
        checkFieldInfo(fields.get(12), "long", long.class, "getLong", "setLong");
        checkFieldInfo(fields.get(13), "longObject", Long.class, "getLongObject", "setLongObject");
        checkFieldInfo(fields.get(14), "short", short.class, "getShort", "setShort");
        checkFieldInfo(fields.get(15), "shortObject", Short.class, "getShortObject",
                "setShortObject");
    }

    @Test
    public void testEmptyBean() {
        IBeanTypeMetaInfo<EmptyBean> meta = this.parser.parse(EmptyBean.class, BeanStyle.CLASSIC,
                Collections.emptyList());
        assertNotNull(meta);
        assertSame(EmptyBean.class, meta.beanType());
        assertSame(BeanStyle.CLASSIC, meta.beanStyle());
        List<IBeanFieldMetaInfo> fields = meta.fieldMetaInfos();
        assertEquals(0, fields.size());
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
        IBeanTypeMetaInfo<Combined> meta = this.parser.parse(Combined.class, BeanStyle.CLASSIC,
                Collections.emptyList());
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
}
