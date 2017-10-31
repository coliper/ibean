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

import java.util.Date;

/**
 * @author alex@coliper.org
 *
 */
public class SampleBeanModernImpl implements SampleBeanModern {
    private Integer intObject;
    private boolean booleanPrimitive;
    private Date date;
    private String string;
    private SampleBeanModern self;

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#intObject(java.lang.Integer)
     */
    @Override
    public SampleBeanModern intObject(Integer i) {
        this.intObject = i;
        return this;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#intObject()
     */
    @Override
    public Integer intObject() {
        return this.intObject;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#booleanPrimitive(boolean)
     */
    @Override
    public SampleBeanModern booleanPrimitive(boolean b) {
        this.booleanPrimitive = b;
        return this;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#booleanPrimitive()
     */
    @Override
    public boolean booleanPrimitive() {
        return this.booleanPrimitive;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#date(java.util.Date)
     */
    @Override
    public SampleBeanModern date(Date d) {
        this.date = d;
        return this;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#date()
     */
    @Override
    public Date date() {
        return this.date;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#string(java.lang.String)
     */
    @Override
    public SampleBeanModern string(String s) {
        this.string = s;
        return this;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#string()
     */
    @Override
    public String string() {
        return this.string;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#self()
     */
    @Override
    public SampleBeanModern self() {
        return this.self;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.SampleBeanModern#self(org.coliper.ibean.SampleBeanModern)
     */
    @Override
    public SampleBeanModern self(SampleBeanModern s) {
        this.self = s;
        return this;
    }
    
    public SampleBeanModernImpl fillWithTestValues() {
        this.booleanPrimitive = true;
        this.date = new Date(0L);
        this.intObject = Integer.valueOf(Integer.MAX_VALUE);
        this.self = new SampleBeanModernImpl();
        this.string = "dummy 2134452";
        return this;
    }
    
    public SampleBeanModernImpl fillWithNullValues() {
        this.booleanPrimitive = false;
        this.date = null;
        this.intObject = Integer.valueOf(0);
        this.self = null;
        this.string = null;
        return this;
    }


    public boolean isEqual(SampleBeanModern other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        if (booleanPrimitive != other.booleanPrimitive())
            return false;
        if (date == null) {
            if (other.date() != null)
                return false;
        } else if (!date.equals(other.date()))
            return false;
        if (intObject == null) {
            if (other.intObject() != null)
                return false;
        } else if (!intObject.equals(other.intObject()))
            return false;
        if (self == null) {
            if (other.self() != null)
                return false;
        } else if (!self.equals(other.self()))
            return false;
        if (string == null) {
            if (other.string() != null)
                return false;
        } else if (!string.equals(other.string()))
            return false;
        return true;
    }
    

    public void copyTo(SampleBeanModern other) {
        other.booleanPrimitive(this.booleanPrimitive);
        other.intObject(this.intObject);
        other.date(this.date);
        other.self(this.self);
        other.string(this.string);
    }
}
