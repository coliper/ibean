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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * Meta description of a field belonging to an IBean, more precisely does it
 * provide name, type, getter method and setter method of the field.
 * <p>
 * This class is created via constructor and is then immutable.
 * 
 * @author alex@coliper.org
 */
public class IBeanFieldMetaInfo {
    private final String fieldName;
    private final Class<?> fieldType;
    private final Method getterMethod;
    private final Method setterMethod;
    private final int ordinal;

    /**
     * Creates a new {@code IBeanFieldMetaInfo} by providing all required meta
     * information.
     * 
     * @param fieldName
     *            the case-sensitive name of the field
     * @param fieldType
     *            the type of the field
     * @param getterMethod
     *            the related getter method in the bean type
     * @param setterMethod
     *            the related setter method in the bean type
     * @param ordinal
     *            the sorting index of the field within its
     *            {@link IBeanTypeMetaInfo}
     */
    public IBeanFieldMetaInfo(String fieldName, Class<?> fieldType, Method getterMethod,
            Method setterMethod, int ordinal) {
        requireNonNull(fieldName, "fieldName");
        requireNonNull(fieldType, "fieldType");
        requireNonNull(getterMethod, "getterMethod");
        requireNonNull(setterMethod, "setterMethod");
        checkArgument(
                ReflectionUtil.areClassesRelated(getterMethod.getDeclaringClass(),
                        setterMethod.getDeclaringClass()),
                "getterMethod and setterMethod must belong to the same class or class hierarchy");
        checkArgument(ordinal >= 0, "ordinal must be a positive integer");

        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.getterMethod = getterMethod;
        this.setterMethod = setterMethod;
        this.ordinal = ordinal;
    }

    /**
     * Provides the name of the field belonging to the IBean type.
     * 
     * @return some label that complies to the rules for field names defined in
     *         the bean style; usually some camel case string
     */
    public String fieldName() {
        return fieldName;
    }

    /**
     * Provides the getter method in the bean type related to the field.
     * 
     * @return a {@code Method} contained in the bean interface
     */
    public Method getterMethod() {
        return getterMethod;
    }

    /**
     * Provides the setter method in the bean type related to the field.
     * 
     * @return a {@code Method} contained in the bean interface
     */
    public Method setterMethod() {
        return setterMethod;
    }

    /**
     * Provides the type of the field.
     * 
     * @return any allowed primitive or object type that complies to the bean
     *         style. Usually only {@code void} is forbidden.
     */
    public Class<?> fieldType() {
        return this.fieldType;
    }

    /**
     * Provides the sorting index of the field within its
     * {@link IBeanTypeMetaInfo}.
     * 
     * @return some zero based positive integer
     */
    public int ordinal() {
        return this.ordinal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IBeanFieldMetaInfo [fieldName=" + fieldName + ", ordinal=" + ordinal
                + ", fieldType=" + fieldType + ", getterMethod=" + getterMethod + ", setterMethod="
                + setterMethod + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ordinal;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((fieldType == null) ? 0 : fieldType.getName().hashCode());
        return result;
    }

    /**
     * Compares this instance with any other object, preferably with another
     * {@code IBeanFieldMetaInfo}. Two {@code IBeanFieldMetaInfo} are considered
     * equal if field name, field type and ordinal are equal.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "getterMethod", "setterMethod");
    }

}
