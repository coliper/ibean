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
 * A {@link org.coliper.ibean.BeanStyle} implementation that reflects beans of traditional 
 * Java bean style as defined in the 
 * <a href="http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">
 * Java Bean Specification</a>.
 * <p/>
 * For each property for a bean you basically have<ul>
 * <li>a no-argument getter method starting with "get" and returning the property type</li>
 * <li>a setter method starting with "set", returning void and having one parameter with type of the
 *     property</li>
 * </ul>
 * After the "set" respectively "get" prefix both methods would have the capitalized property name in
 * their name.<br/>
 * For example a property with name "zipCode" and type String would have following setter and getter:
 * <code>
 * void setZipCode(String c);
 * String getZipCode();
 * </code>
 * 
 *<p/>
 * This bean style also exists with <code>Optional</code> support in 
 * {@link ClassicBeanStyleWithOptionalSupport}.
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
        return ReflectionUtil.areClassesRelated(
                method.getDeclaringClass(),method.getReturnType());
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

    /* (non-Javadoc)
     * @see org.coliper.ibean.BeanStyle#createReturnValueForSetterCall(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object createReturnValueForSetterCall(Object instance, Method setterMethod,
            Object newValue) {
        return instance;
    }
    
    
}
