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

package org.coliper.ibean.codegen;

import org.coliper.ibean.IBeanFieldMetaInfo;

/**
 * Interface provided to {@link ExtensionHandler}s to gain access to the field
 * values of their corresponding proxy bean. Only used in
 * {@link ExtensionHandler} context, for example in
 * {@link ExtensionHandler#handleExtendedInterfaceCall(IBeanContext, IBeanFieldAccess, Object, java.lang.reflect.Method, Object[])}
 * 
 * @author alex@coliper.org
 */
public interface IBeanFieldAccess {

    /**
     * Changes the value of a field to the specified value.
     * 
     * @param fieldName
     *            specifies the field to change; must match to an existing field
     *            name
     * @param newValue
     *            the new value of the field; may be <code>null</code>. The type
     *            of the given value must match the field type; types that can
     *            be converted by Java implicit type conversion are allowed as
     *            well, for example <code>int</code> instead of
     *            <code>long</code>.
     * @throws IllegalArgumentException
     *             if the fieldName does not exist
     * @throws ClassCastException
     *             if the value type does not match the field type
     */
    void setFieldValue(String fieldName, Object newValue);

    /**
     * Retrieves the value of a bean field.
     * 
     * @param fieldName
     *            specifies the field to retrieve; must be an existing field
     *            name
     * @return the value having expected field type. Prititive types are
     *         converted to their object types. Value may be <code>null</code>.
     * @throws IllegalArgumentException
     *             if the fieldName does not exist
     */
    Object getFieldValue(String fieldName);

    /**
     * Changes the value of a field to the specified value.
     * 
     * @param fieldMeta
     *            specifies the field to change; must match to the respective
     *            bean type
     * @param newValue
     *            the new value of the field; may be <code>null</code>. The type
     *            of the given value must match the field type; types that can
     *            be converted by Java implicit type conversion are allowed as
     *            well, for example <code>int</code> instead of
     *            <code>long</code>.
     * @throws IllegalArgumentException
     *             if <code>fieldMeta</code> does not belong to the bean class
     * @throws ClassCastException
     *             if the value type does not match the field type
     */
    void setFieldValue(IBeanFieldMetaInfo fieldMeta, Object newValue);

    /**
     * Retrieves the value of a bean field.
     * 
     * @param fieldMeta
     *            specifies the field to retrieve; must match to the respective
     *            bean type
     * @return the value having expected field type. Prititive types are
     *         converted to their object types. Value may be <code>null</code>.
     * @throws IllegalArgumentException
     *             if the fieldName does not exist
     */
    Object getFieldValue(IBeanFieldMetaInfo fieldMeta);

}
