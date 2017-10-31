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

import org.coliper.ibean.util.ReflectionUtil;

/**
 * @author alex@coliper.org
 *
 */
public class IBeanFieldMetaInfo {
    private final String fieldName;
    private final Class<?> fieldType;
    private final Method getterMethod;
    private final Method setterMethod;
    private final int ordinal;

    /**
     * @param fieldName
     * @param getterMethod
     * @param setterMethod
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
     * @return the fieldName
     */
    public String fieldName() {
        return fieldName;
    }

    /**
     * @return the getterMethod
     */
    public Method getterMethod() {
        return getterMethod;
    }

    /**
     * @return the setterMethod
     */
    public Method setterMethod() {
        return setterMethod;
    }

    public Class<?> fieldType() {
        return this.fieldType;
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IBeanFieldMetaInfo other = (IBeanFieldMetaInfo) obj;
        if (other.ordinal != this.ordinal) {
            return false;
        }
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        if (fieldType == null) {
            if (other.fieldType != null)
                return false;
        } else if (!fieldType.equals(other.fieldType))
            return false;
        return true;
    }

}
