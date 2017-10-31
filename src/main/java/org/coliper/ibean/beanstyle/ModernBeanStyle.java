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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * A {@link org.coliper.ibean.BeanStyle} implementation that has getters and setters named equal
 * to the property and that return "this" from setters to allow setter chaining.
 * <p>
 * In many modern libraries you see a different way of defining setter and getter methods taken 
 * from other programming languages like C++. This bean style reflects the most common one.
 * <p>
 * For each property for a bean you basically have a getter and a setter that are equally named
 * to the property. 
 * <ul>
 * <li>The getter has no arguments and returns the property type.</li>
 * <li>The setter has one argument with property type and returns the <em>bean class</em>.
 *     The returned object is always the bean itself. By this you can chain setter calls easily.
 *     </li>
 * </ul>
 * For example a property with name "zipCode" and type String in a bean class named Address would
 * have following setter and getter: <code>
 * Adress zipCode(String c);
 * String zipCode();
 * </code>
 * 
 * <p>
 * This bean style also exists with <code>Optional</code> support in
 * {@link ModernBeanStyleWithOptionalSupport}.
 * 
 * @author alex@coliper.org
 *
 */
public class ModernBeanStyle extends BeanStyle {

    public static final ModernBeanStyle INSTANCE = new ModernBeanStyle();

    /**
     * 
     */
    protected ModernBeanStyle() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.BeanStyle#isGetterMethod(java.lang.Class,
     * java.lang.reflect.Method)
     */
    @Override
    public boolean isGetterMethod(Class<?> beanType, Method method) {
        requireNonNull(beanType, "beanType");
        requireNonNull(method, "method");
        assertMethodBelongsToType(method, beanType);
        return hasGetterMethodSignature(method);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.BeanStyle#isSetterMethod(java.lang.Class,
     * java.lang.reflect.Method)
     */
    @Override
    public boolean isSetterMethod(Class<?> beanType, Method method) {
        requireNonNull(beanType, "beanType");
        requireNonNull(method, "method");
        assertMethodBelongsToType(method, beanType);
        return hasSetterMethodSignature(method);
    }

    @Override
    protected boolean hasSetterMethodSignature(Method method) {
        return super.hasSetterMethodSignature(method) && this.methodReturnsDeclaringType(method);
    }

    /**
     * @param method
     * @return
     */
    private boolean methodReturnsDeclaringType(Method method) {
        return ReflectionUtil.areClassesRelated(method.getDeclaringClass(), method.getReturnType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.BeanStyle#convertGetterNameToFieldName(java.lang.
     * String)
     */
    @Override
    public String convertGetterNameToFieldName(String getterName) {
        requireNonNull(getterName, "getterName");
        return getterName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.BeanStyle#convertSetterNameToFieldName(java.lang.
     * String)
     */
    @Override
    public String convertSetterNameToFieldName(String setterName) {
        requireNonNull(setterName, "setterName");
        return setterName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.coliper.ibean.BeanStyle#createReturnValueForSetterCall(java.lang.
     * Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object createReturnValueForSetterCall(Object instance, Method setterMethod,
            Object newValue) {
        return instance;
    }

}
