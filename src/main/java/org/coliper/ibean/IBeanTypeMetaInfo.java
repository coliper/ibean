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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

/**
 * @author alex@coliper.org
 *
 */
public class IBeanTypeMetaInfo<T> {
    private final Class<T> beanType;
    private final BeanStyle beanStyle;
    private final List<IBeanFieldMetaInfo> fieldMetaInfos;

    /**
     * @param beanType
     * @param beanStyle
     * @param fieldMetaInfos
     */
    public IBeanTypeMetaInfo(Class<T> beanType, BeanStyle beanStyle,
            List<IBeanFieldMetaInfo> fieldMetaInfos) {
        requireNonNull(beanType, "beanType");
        requireNonNull(beanStyle, "beanStyle");
        requireNonNull(fieldMetaInfos);
        this.beanType = beanType;
        this.beanStyle = beanStyle;
        this.fieldMetaInfos = ImmutableList.copyOf(fieldMetaInfos);
    }

    /**
     * @return the beanType
     */
    public Class<T> beanType() {
        return beanType;
    }

    /**
     * @return the beanStyle
     */
    public BeanStyle beanStyle() {
        return beanStyle;
    }

    /**
     * @return the fieldMetaInfos
     */
    public List<IBeanFieldMetaInfo> fieldMetaInfos() {
        return fieldMetaInfos;
    }

    public int noOfFields() {
        return this.fieldMetaInfos.size();
    }

    public Optional<IBeanFieldMetaInfo> findFieldMetaWithMethod(Method method) {
        requireNonNull(method, "method");
        for (IBeanFieldMetaInfo fieldMeta : fieldMetaInfos) {
            if (fieldMeta.setterMethod().equals(method)
                    || fieldMeta.getterMethod().equals(method)) {
                return Optional.of(fieldMeta);
            }
        }
        return Optional.empty();
    }

    public Optional<IBeanFieldMetaInfo> findFieldMetaWithFieldName(String fieldName) {
        requireNonNull(fieldName, "fieldName");
        for (IBeanFieldMetaInfo fieldMeta : fieldMetaInfos) {
            if (fieldMeta.fieldName().equals(fieldName)) {
                return Optional.of(fieldMeta);
            }
        }
        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IBeanTypeMetaInfo [beanType=" + beanType + ", beanStyle=" + beanStyle
                + ", fieldMetaInfos=" + fieldMetaInfos + "]";
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
        result = prime * result + ((beanType == null) ? 0 : beanType.getName().hashCode());
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
        IBeanTypeMetaInfo<?> other = (IBeanTypeMetaInfo<?>) obj;
        if (beanType == null) {
            if (other.beanType != null)
                return false;
        } else if (!beanType.equals(other.beanType))
            return false;
        return true;
    }

}
