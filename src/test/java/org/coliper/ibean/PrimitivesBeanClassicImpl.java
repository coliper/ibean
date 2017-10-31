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
public class PrimitivesBeanClassicImpl implements PrimitivesBeanClassic {
    private byte byte_;
    private short short_;
    private int int_;
    private long long_;
    private float float_;
    private double double_;
    private boolean boolean_;
    private char char_;
    private Byte byteObject;
    private Short shortObject;
    private Integer intObject;
    private Long longObject;
    private Float floatObject;

    /**
     * @return the floatObject
     */
    public Float getFloatObject() {
        return floatObject;
    }

    /**
     * @param floatObject
     *            the floatObject to set
     */
    public void setFloatObject(Float floatObject) {
        this.floatObject = floatObject;
    }

    private Double doubleObject;
    private Boolean booleanObject;
    private Character charObject;

    /**
     * @return the byte_
     */
    public byte getByte() {
        return byte_;
    }

    /**
     * @param byte_
     *            the byte_ to set
     */
    public void setByte(byte byte_) {
        this.byte_ = byte_;
    }

    /**
     * @return the short_
     */
    public short getShort() {
        return short_;
    }

    /**
     * @param short_
     *            the short_ to set
     */
    public void setShort(short short_) {
        this.short_ = short_;
    }

    /**
     * @return the int_
     */
    public int getInt() {
        return int_;
    }

    /**
     * @param int_
     *            the int_ to set
     */
    public void setInt(int int_) {
        this.int_ = int_;
    }

    /**
     * @return the long_
     */
    public long getLong() {
        return long_;
    }

    /**
     * @param long_
     *            the long_ to set
     */
    public void setLong(long long_) {
        this.long_ = long_;
    }

    /**
     * @return the float_
     */
    public float getFloat() {
        return float_;
    }

    /**
     * @param float_
     *            the float_ to set
     */
    public void setFloat(float float_) {
        this.float_ = float_;
    }

    /**
     * @return the double_
     */
    public double getDouble() {
        return double_;
    }

    /**
     * @param double_
     *            the double_ to set
     */
    public void setDouble(double double_) {
        this.double_ = double_;
    }

    /**
     * @return the boolean_
     */
    public boolean isBoolean() {
        return boolean_;
    }

    /**
     * @param boolean_
     *            the boolean_ to set
     */
    public void setBoolean(boolean boolean_) {
        this.boolean_ = boolean_;
    }

    /**
     * @return the char_
     */
    public char getChar() {
        return char_;
    }

    /**
     * @param char_
     *            the char_ to set
     */
    public void setChar(char char_) {
        this.char_ = char_;
    }

    /**
     * @return the byteObject
     */
    public Byte getByteObject() {
        return byteObject;
    }

    /**
     * @param byteObject
     *            the byteObject to set
     */
    public void setByteObject(Byte byteObject) {
        this.byteObject = byteObject;
    }

    /**
     * @return the shortObject
     */
    public Short getShortObject() {
        return shortObject;
    }

    /**
     * @param shortObject
     *            the shortObject to set
     */
    public void setShortObject(Short shortObject) {
        this.shortObject = shortObject;
    }

    /**
     * @return the intObject
     */
    public Integer getIntObject() {
        return intObject;
    }

    /**
     * @param intObject
     *            the intObject to set
     */
    public void setIntObject(Integer intObject) {
        this.intObject = intObject;
    }

    /**
     * @return the longObject
     */
    public Long getLongObject() {
        return longObject;
    }

    /**
     * @param longObject
     *            the longObject to set
     */
    public void setLongObject(Long longObject) {
        this.longObject = longObject;
    }

    /**
     * @return the doubleObject
     */
    public Double getDoubleObject() {
        return doubleObject;
    }

    /**
     * @param doubleObject
     *            the doubleObject to set
     */
    public void setDoubleObject(Double doubleObject) {
        this.doubleObject = doubleObject;
    }

    /**
     * @return the booleanObject
     */
    public Boolean getBooleanObject() {
        return booleanObject;
    }

    /**
     * @param booleanObject
     *            the booleanObject to set
     */
    public void setBooleanObject(Boolean booleanObject) {
        this.booleanObject = booleanObject;
    }

    /**
     * @return the charObject
     */
    public Character getCharObject() {
        return charObject;
    }

    /**
     * @param charObject
     *            the charObject to set
     */
    public void setCharObject(Character charObject) {
        this.charObject = charObject;
    }

    public void copyTo(PrimitivesBeanClassic other)
            throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(other, this);
    }

    public PrimitivesBeanClassicImpl fillWithTestValues() {
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

    public PrimitivesBeanClassicImpl fillWithNullValues() {
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
}
