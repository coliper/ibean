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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author alex@coliper.org
 *
 */
public interface PrimitivesBeanClassic {
//@formatter:off 
    void setBytePrimitive(byte b);
    byte getBytePrimitive();
    
    void setShortPrimitive(short s);
    short getShortPrimitive();
    
    void setIntPrimitive(int i);
    int getIntPrimitive();
    
    void setLongPrimitive(long l);
    long getLongPrimitive();
    
    void setFloatPrimitive(float f);
    float getFloatPrimitive();
    
    void setDoublePrimitive(double d);
    double getDoublePrimitive();
    
    void setBooleanPrimitive(boolean b);
    boolean isBooleanPrimitive();
    
    void setCharPrimitive(char c);
    char getCharPrimitive();

    void setByteObject(Byte b);
    Byte getByteObject();
    
    void setShortObject(Short s);
    Short getShortObject();
    
    void setIntObject(Integer i);
    Integer getIntObject();
    
    void setLongObject(Long l);
    Long getLongObject();
    
    void setFloatObject(Float f);
    Float getFloatObject();
    
    void setDoubleObject(Double d);
    Double getDoubleObject();
    
    void setBooleanObject(Boolean b);
    Boolean getBooleanObject();
    
    void setCharObject(Character c);
    Character getCharObject();
//@formatter:on    

    default PrimitivesBeanClassic fillWithTestValues() {
        this.setBooleanPrimitive(true);
        this.setBooleanObject(Boolean.TRUE);
        this.setBytePrimitive(Byte.MIN_VALUE);
        this.setByteObject(Byte.valueOf(Byte.MAX_VALUE));
        this.setCharPrimitive('x');
        this.setCharObject(Character.valueOf('@'));
        this.setDoublePrimitive(Double.MIN_NORMAL);
        this.setDoubleObject(Double.valueOf(Double.MAX_VALUE));
        this.setFloatPrimitive(Float.MAX_VALUE);
        this.setFloatObject(Float.valueOf(Float.MIN_VALUE));
        this.setIntPrimitive(Integer.MIN_VALUE);
        this.setIntObject(Integer.valueOf(-1));
        this.setLongPrimitive(Long.MAX_VALUE);
        this.setLongObject(Long.valueOf(Long.MIN_VALUE));
        this.setShortPrimitive(Short.MAX_VALUE);
        this.setShortObject(Short.valueOf(Short.MIN_VALUE));
        return this;
    }

    default PrimitivesBeanClassic fillWithNullValues() {
        this.setBooleanPrimitive(false);
        this.setBooleanObject(null);
        this.setBytePrimitive((byte) 0);
        this.setByteObject(null);
        this.setCharPrimitive('\u0000');
        this.setCharObject(null);
        this.setDoublePrimitive(0.0);
        this.setDoubleObject(null);
        this.setFloatPrimitive((float) 0.0);
        this.setFloatObject(null);
        this.setIntPrimitive(0);
        this.setIntObject(null);
        this.setLongPrimitive(0L);
        this.setLongObject(null);
        this.setShortPrimitive((short) 0);
        this.setShortObject(null);
        return this;
    }

    default void copyTo(PrimitivesBeanClassic other)
            throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(other, this);
    }

    default void assertEqual(PrimitivesBeanClassic other) {
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, this, other);
    }

    default void assertEqual(PrimitivesBeanClassic other, boolean requireNestedObjectsSame) {
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, this, other,
                requireNestedObjectsSame);
    }
}
