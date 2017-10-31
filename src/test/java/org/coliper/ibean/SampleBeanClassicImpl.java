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
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author alex@coliper.org
 *
 */
public class SampleBeanClassicImpl implements SampleBeanClassic {
    private Integer intObject;
    private boolean booleanPrimitive;
    private Date date;
    private String string;
    private SampleBeanClassic self;

    /**
     * @return the intPrimitive
     */
    public Integer getIntObject() {
        return intObject;
    }

    /**
     * @param intPrimitive
     *            the intPrimitive to set
     */
    public void setIntObject(Integer intObject) {
        this.intObject = intObject;
    }

    /**
     * @return the booleanObject
     */
    public boolean isBooleanPrimitive() {
        return booleanPrimitive;
    }

    /**
     * @param booleanObject
     *            the booleanObject to set
     */
    public void setBooleanPrimitive(boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * @param string
     *            the string to set
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * @return the self
     */
    public SampleBeanClassic getSelf() {
        return self;
    }

    /**
     * @param self
     *            the self to set
     */
    public void setSelf(SampleBeanClassic self) {
        this.self = self;
    }

    public void copyTo(SampleBeanClassic other)
            throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(other, this);
    }

    public SampleBeanClassicImpl fillWithTestValues() {
        this.setBooleanPrimitive(true);
        this.setDate(new Date(0L));
        this.setIntObject(Integer.valueOf(Integer.MAX_VALUE));
        this.setSelf(new SampleBeanClassicImpl());
        this.setString("dummy 2134452");
        return this;
    }

    public SampleBeanClassicImpl fillWithNullValues() {
        this.setBooleanPrimitive(false);
        this.setDate(null);
        this.setIntObject(Integer.valueOf(0));
        this.setSelf(null);
        this.setString(null);
        return this;
    }

}
