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

/**
 * @author alex@coliper.org
 *
 */
public class PrimitivesBeanClassicImpl implements PrimitivesBeanClassic {
    private byte bytePrimitive;
    private short shortPrimitive;
    private int intPrimitive;
    private long longPrimitive;
    private float floatPrimitive;
    private double doublePrimitive;
    private boolean booleanPrimitive;
    private char charPrimitive;
    private Byte byteObject;
    private Short shortObject;
    private Integer intObject;
    private Long longObject;
    private Float floatObject;
    private Double doubleObject;
    private Boolean booleanObject;
    private Character charObject;

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

    @Override
    public PrimitivesBeanClassic clone() {
        PrimitivesBeanClassic clone = new PrimitivesBeanClassicImpl();
        try {
            this.copyTo(clone);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    /**
     * @return the bytePrimitive
     */
    public byte getBytePrimitive() {
        return bytePrimitive;
    }

    /**
     * @param bytePrimitive
     *            the bytePrimitive to set
     */
    public void setBytePrimitive(byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }

    /**
     * @return the shortPrimitive
     */
    public short getShortPrimitive() {
        return shortPrimitive;
    }

    /**
     * @param shortPrimitive
     *            the shortPrimitive to set
     */
    public void setShortPrimitive(short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }

    /**
     * @return the intPrimitive
     */
    public int getIntPrimitive() {
        return intPrimitive;
    }

    /**
     * @param intPrimitive
     *            the intPrimitive to set
     */
    public void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    /**
     * @return the longPrimitive
     */
    public long getLongPrimitive() {
        return longPrimitive;
    }

    /**
     * @param longPrimitive
     *            the longPrimitive to set
     */
    public void setLongPrimitive(long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }

    /**
     * @return the floatPrimitive
     */
    public float getFloatPrimitive() {
        return floatPrimitive;
    }

    /**
     * @param floatPrimitive
     *            the floatPrimitive to set
     */
    public void setFloatPrimitive(float floatPrimitive) {
        this.floatPrimitive = floatPrimitive;
    }

    /**
     * @return the doublePrimitive
     */
    public double getDoublePrimitive() {
        return doublePrimitive;
    }

    /**
     * @param doublePrimitive
     *            the doublePrimitive to set
     */
    public void setDoublePrimitive(double doublePrimitive) {
        this.doublePrimitive = doublePrimitive;
    }

    /**
     * @return the booleanPrimitive
     */
    public boolean isBooleanPrimitive() {
        return booleanPrimitive;
    }

    /**
     * @param booleanPrimitive
     *            the booleanPrimitive to set
     */
    public void setBooleanPrimitive(boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }

    /**
     * @return the charPrimitive
     */
    public char getCharPrimitive() {
        return charPrimitive;
    }

    /**
     * @param charPrimitive
     *            the charPrimitive to set
     */
    public void setCharPrimitive(char charPrimitive) {
        this.charPrimitive = charPrimitive;
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

}
