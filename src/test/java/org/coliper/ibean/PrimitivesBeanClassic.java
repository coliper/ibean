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
    void setByte(byte b);
    byte getByte();
    
    void setShort(short s);
    short getShort();
    
    void setInt(int i);
    int getInt();
    
    void setLong(long l);
    long getLong();
    
    void setFloat(float f);
    float getFloat();
    
    void setDouble(double d);
    double getDouble();
    
    void setBoolean(boolean b);
    boolean isBoolean();
    
    void setChar(char c);
    char getChar();

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
        this.setBoolean(true);
        this.setBooleanObject(Boolean.TRUE);
        this.setByte(Byte.MIN_VALUE);
        this.setByteObject(Byte.valueOf(Byte.MAX_VALUE));
        this.setChar('x');
        this.setCharObject(Character.valueOf('@'));
        this.setDouble(Double.MIN_NORMAL);
        this.setDoubleObject(Double.valueOf(Double.MAX_VALUE));
        this.setFloat(Float.NEGATIVE_INFINITY);
        this.setFloatObject(Float.valueOf(Float.NaN));
        this.setInt(Integer.MIN_VALUE);
        this.setIntObject(Integer.valueOf(-1));
        this.setLong(Long.MAX_VALUE);
        this.setLongObject(Long.valueOf(Long.MIN_VALUE));
        this.setShort(Short.MAX_VALUE);
        this.setShortObject(Short.valueOf(Short.MIN_VALUE));
        return this;
    }

    default PrimitivesBeanClassic fillWithNullValues() {
        this.setBoolean(false);
        this.setBooleanObject(null);
        this.setByte((byte) 0);
        this.setByteObject(null);
        this.setChar('\u0000');
        this.setCharObject(null);
        this.setDouble(0.0);
        this.setDoubleObject(null);
        this.setFloat((float) 0.0);
        this.setFloatObject(null);
        this.setInt(0);
        this.setIntObject(null);
        this.setLong(0L);
        this.setLongObject(null);
        this.setShort((short) 0);
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
}
