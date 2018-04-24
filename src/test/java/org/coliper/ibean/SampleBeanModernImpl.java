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

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#intObject(java.lang.Integer)
     */
    @Override
    public SampleBeanModern intObject(Integer i) {
        this.intObject = i;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#intObject()
     */
    @Override
    public Integer intObject() {
        return this.intObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#booleanPrimitive(boolean)
     */
    @Override
    public SampleBeanModern booleanPrimitive(boolean b) {
        this.booleanPrimitive = b;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#booleanPrimitive()
     */
    @Override
    public boolean booleanPrimitive() {
        return this.booleanPrimitive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#date(java.util.Date)
     */
    @Override
    public SampleBeanModern date(Date d) {
        this.date = d;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#date()
     */
    @Override
    public Date date() {
        return this.date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#string(java.lang.String)
     */
    @Override
    public SampleBeanModern string(String s) {
        this.string = s;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#string()
     */
    @Override
    public String string() {
        return this.string;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#self()
     */
    @Override
    public SampleBeanModern self() {
        return this.self;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.SampleBeanModern#self(org.coliper.ibean.
     * SampleBeanModern)
     */
    @Override
    public SampleBeanModern self(SampleBeanModern s) {
        this.self = s;
        return this;
    }

}
