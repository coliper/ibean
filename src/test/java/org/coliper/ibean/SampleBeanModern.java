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
public interface SampleBeanModern {

//@formatter:off 
    SampleBeanModern intObject(Integer i);
    Integer intObject();

    SampleBeanModern booleanPrimitive(boolean b);
    boolean booleanPrimitive();

    SampleBeanModern date(Date d);
    Date date();

    SampleBeanModern string(String s);
    String string();

    SampleBeanModern self();
    SampleBeanModern self(SampleBeanModern s);
//@formatter:on    

    default SampleBeanModern fillWithTestValues() {
        this.booleanPrimitive(true);
        this.date(new Date(0L));
        this.intObject(Integer.valueOf(Integer.MAX_VALUE));
        this.self(new SampleBeanModernImpl());
        this.string("dummy 2134452");
        return this;
    }

    default SampleBeanModern fillWithNullValues() {
        this.booleanPrimitive(false);
        this.date(null);
        this.intObject(Integer.valueOf(0));
        this.self(null);
        this.string(null);
        return this;
    }

    default boolean isEqual(SampleBeanModern other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        if (booleanPrimitive() != other.booleanPrimitive())
            return false;
        if (date() == null) {
            if (other.date() != null)
                return false;
        } else if (!date().equals(other.date()))
            return false;
        if (intObject() == null) {
            if (other.intObject() != null)
                return false;
        } else if (!intObject().equals(other.intObject()))
            return false;
        if (self() == null) {
            if (other.self() != null)
                return false;
        } else if (!self().equals(other.self()))
            return false;
        if (string() == null) {
            if (other.string() != null)
                return false;
        } else if (!string().equals(other.string()))
            return false;
        return true;
    }

    default void assertEqual(SampleBeanModern other) {
        BeanTestUtil.assertEqualsBean(SampleBeanModern.class, BeanStyle.MODERN, this, other);
    }

    default void assertEqual(SampleBeanModern other, boolean requireNestedObjectsSame) {
        BeanTestUtil.assertEqualsBean(SampleBeanModern.class, BeanStyle.MODERN, this, other,
                requireNestedObjectsSame);
    }

    default void copyTo(SampleBeanModern other) {
        other.booleanPrimitive(this.booleanPrimitive());
        other.intObject(this.intObject());
        other.date(this.date());
        other.self(this.self());
        other.string(this.string());
    }
}
