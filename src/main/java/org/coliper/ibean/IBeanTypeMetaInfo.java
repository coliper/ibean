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
 * Class that holds all IBean relevant meta information about a specific bean
 * type, mainly a list of all contained fields.
 * <p>
 * This class is created once via constructor and is then immutable afterwards.
 * 
 * @author alex@coliper.org
 */
public class IBeanTypeMetaInfo<T> {
    private final Class<T> beanType;
    private final BeanStyle beanStyle;
    private final List<IBeanFieldMetaInfo> fieldMetaInfos;

    /**
     * Creates a new {@code IBeanTypeMetaInfo} with all contained information.
     * 
     * @param beanType
     *            the IBean type the meta information is related to
     * @param beanStyle
     *            the bean style that was used for parsing the bean type
     * @param fieldMetaInfos
     *            meta information about all fields contained in the bean type;
     *            may not be <code>null</code> but may be an empty list
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
     * Gives the related bean type.
     * 
     * @return the bean class this meta information is related to
     */
    public Class<T> beanType() {
        return beanType;
    }

    /**
     * Gives the bean style the bean type complies to.
     * 
     * @return the {@link BeanStyle} that was used for parsing the bean type
     */
    public BeanStyle beanStyle() {
        return beanStyle;
    }

    /**
     * Provides {@link IBeanFieldMetaInfo} for all fields of the bean type.
     * 
     * @return the meta information about all contained fields of the bean type
     */
    public List<IBeanFieldMetaInfo> fieldMetaInfos() {
        return fieldMetaInfos;
    }

    /**
     * Convenience method returning the number of fields contained in the bean
     * type.
     * 
     * @return same as {@code this.fieldMetaInfos.size()}
     */
    public int noOfFields() {
        return this.fieldMetaInfos.size();
    }

    /**
     * Iterates over all contained {@code IBeanFieldMetaInfo} and returns the
     * field meta info that contains the given method either as setter or as
     * getter.
     * 
     * @param method
     *            the getter or setter to search for
     * @return the field meta info or an empty {@code Optional} if no field info
     *         matches
     */
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

    /**
     * Iterates over all contained {@code IBeanFieldMetaInfo} and returns the
     * field meta info that belongs to a field with a given name.
     * 
     * @param fieldName
     *            the name of a field to search for
     * @return the field meta info or an empty {@code Optional} if no field name
     *         matches
     */
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

    /**
     * Compares this instance with any other object preferably with another
     * {@code IBeanTypeMetaInfo}. Two {@code IBeanTypeMetaInfo} are equal if
     * they have the same bean type and the same bean style.
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
        if (!beanType.equals(other.beanType)) {
            return false;
        }
        if (!beanStyle.equals(other.beanStyle)) {
            return false;
        }
        return true;
    }

}
