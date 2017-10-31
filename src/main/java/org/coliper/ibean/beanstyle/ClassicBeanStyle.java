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

package org.coliper.ibean.beanstyle;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;

import org.coliper.ibean.BeanStyle;

/**
 * A {@link org.coliper.ibean.BeanStyle} implementation that reflects beans of
 * traditional Java bean style as defined in the <a href=
 * "http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">
 * Java Bean Specification</a>.
 * <p>
 * For each property for a bean you basically have
 * <ul>
 * <li>a no-argument getter method starting with "get" and returning the
 * property type</li>
 * <li>a setter method starting with "set", returning void and having one
 * parameter with type of the property</li>
 * </ul>
 * After the "set" respectively "get" prefix both methods would have the
 * capitalized property name in their name.<br>
 * For example a property with name "zipCode" and type String would have
 * following setter and getter: <code>
 * void setZipCode(String c);
 * String getZipCode();
 * </code>
 * 
 * <p>
 * This bean style also exists with <code>Optional</code> support in
 * {@link ClassicBeanStyleWithOptionalSupport}.
 * 
 * @author alex@coliper.org
 */
public class ClassicBeanStyle extends BeanStyle {
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_BOOL_PREFIX = "is";

    public static final ClassicBeanStyle INSTANCE = new ClassicBeanStyle();

    /**
     * 
     */
    protected ClassicBeanStyle() {
    }

    /**
     * Checks if the given method
     * <ul>
     * <li>does not return void</li>
     * <li>has no arguments</li>
     * <li>and has a name that has at least four characters and starts with
     * "get" or "is" for boolean properties</li>
     * </ul>
     * 
     * @see org.coliper.ibean.BeanStyle#isGetterMethod(java.lang.Class,
     *      java.lang.reflect.Method)
     */
    @Override
    public boolean isGetterMethod(Class<?> beanType, Method method) {
        requireNonNull(beanType, "beanType");
        requireNonNull(method, "method");
        assertMethodBelongsToType(method, beanType);
        if (hasGetterMethodSignature(method)) {
            return this.hasMethodNameRealPrefix(method, GETTER_PREFIX)
                    || isBoolGetterWithIsPrefix(method);
        }
        return false;
    }

    /**
     * @return
     */
    private boolean isBoolGetterWithIsPrefix(Method method) {
        return method.getReturnType() == boolean.class
                && this.hasMethodNameRealPrefix(method, GETTER_BOOL_PREFIX);
    }

    /**
     * Checks if the given method
     * <ul>
     * <li>returns void</li>
     * <li>has exactly one arguments</li>
     * <li>and has a name that has at least four characters and starts with
     * "set"</li>
     * </ul>
     * 
     * @see org.coliper.ibean.BeanStyle#isSetterMethod(java.lang.Class,
     *      java.lang.reflect.Method)
     */
    @Override
    public boolean isSetterMethod(Class<?> beanType, Method method) {
        requireNonNull(beanType, "beanType");
        requireNonNull(method, "method");
        assertMethodBelongsToType(method, beanType);
        return hasSetterMethodSignature(method) && hasMethodNameRealPrefix(method, SETTER_PREFIX);
    }

    private boolean hasMethodNameRealPrefix(Method method, String prefix) {
        return method.getName().startsWith(prefix) && prefix.length() < method.getName().length();
    }

    @Override
    protected boolean hasSetterMethodSignature(Method method) {
        return super.hasSetterMethodSignature(method) && method.getReturnType() == void.class;
    }

    /**
     * Cuts off "get" and decapitalizes the first character of the remaining.
     * 
     * @see org.coliper.ibean.BeanStyle#convertGetterNameToFieldName(java.lang.String)
     */
    @Override
    public String convertGetterNameToFieldName(String getterName) {
        requireNonNull(getterName, "getterName");
        if (getterName.startsWith(GETTER_PREFIX)) {
            return this.cutOffPrefixAndDecapitalize(getterName, GETTER_PREFIX);
        }
        if (getterName.startsWith(GETTER_BOOL_PREFIX)) {
            return this.cutOffPrefixAndDecapitalize(getterName, GETTER_BOOL_PREFIX);
        }
        throw new IllegalArgumentException("invalid setter method name '" + getterName + "'");
    }

    /**
     * Cuts off "set" and decapitalizes the first character of the remaining.
     * 
     * @see org.coliper.ibean.BeanStyle#convertSetterNameToFieldName(java.lang.String)
     */
    @Override
    public String convertSetterNameToFieldName(String setterName) {
        requireNonNull(setterName, "setterName");
        checkArgument(setterName.startsWith(SETTER_PREFIX), "invalid setter method name '%s'",
                setterName);
        return this.cutOffPrefixAndDecapitalize(setterName, SETTER_PREFIX);
    }

    private String cutOffPrefixAndDecapitalize(String methodName, String prefix) {
        final StringBuilder fieldName = new StringBuilder(methodName);
        fieldName.delete(0, prefix.length()); // cut off prefix
        // convert first character to lower case
        fieldName.setCharAt(0, Character.toLowerCase(fieldName.charAt(0)));
        return fieldName.toString();
    }

}
