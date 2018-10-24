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

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.util.Optional;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.beanstyle.ClassicBeanStyle;
import org.coliper.ibean.beanstyle.ClassicBeanStyleWithOptionalSupport;
import org.coliper.ibean.beanstyle.ModernBeanStyle;
import org.coliper.ibean.proxy.ProxyIBeanFactory.Builder;

/**
 * {@code BeanStyleHandler}s are used by the {@link ProxyIBeanFactory} to deal
 * with {@link BeanStyle}s that differ from the classic bean style in their
 * runtime behavior.
 * <p>
 * {@code BeanStyleHandlers}s are necessary in two cases:
 * <ul>
 * <li>Setter methods of a bean style do not return {@code void}.</li>
 * <li>Getter methods of a bean style return a type that does not match to the
 * type of the corresponding field.</li>
 * </ul>
 * <p>
 * If you want to provide your own bean style and at least one of the two
 * conditions above match to your custom bean style then you need to also
 * provide a {@code BeanStyleHandler}. Style and its matching handler are
 * configured when assembling a {@link ProxyIBeanFactory} using method
 * {@link Builder#withBeanStyle(BeanStyle, BeanStyleHandler)}.
 * 
 * @author alex@coliper.org
 */
public interface BeanStyleHandler {

    /**
     * <code>BeanStyleHandler</code> for {@link ClassicBeanStyle}.
     */
    BeanStyleHandler DEFAULT_HANDLER = new BeanStyleHandler() {
    };

    /**
     * <code>BeanStyleHandler</code> for
     * {@link ClassicBeanStyleWithOptionalSupport}.
     */
    BeanStyleHandler CLASSIC_WITH_OPTIONAL_SUPPORT_HANDLER = new BeanStyleHandler() {

        @Override
        public Object convertReturnValueOfGetterCall(Class<?> expectedReturnType,
                Object returnValueWithWrongType) {
            checkArgument(Optional.class == expectedReturnType,
                    "unexpected return type %s for getter", expectedReturnType);
            if (returnValueWithWrongType != null) {
                return Optional.of(returnValueWithWrongType);
            } else {
                return Optional.empty();
            }
        }

    };

    /**
     * <code>BeanStyleHandler</code> for {@link ModernBeanStyle}.
     */
    BeanStyleHandler MODERN_HANDLER = new BeanStyleHandler() {

        @Override
        public Object convertReturnValueOfGetterCall(Class<?> expectedReturnType,
                Object returnValueWithWrongType) {
            checkArgument(Optional.class == expectedReturnType,
                    "unexpected return type %s for getter", expectedReturnType);
            if (returnValueWithWrongType != null) {
                return Optional.of(returnValueWithWrongType);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public Object createReturnValueForSetterCall(Object instance, Method setterMethod,
                Object newValue) {
            return instance;
        }

    };

    /**
     * This method is called during runtime of a bean, more precisely, always
     * when setters of a bean are called to assemble the return value of the
     * setter method.<br>
     * This method is only called for bean styles that allow setter methods that
     * do not return {@code void}. Therefore this method only needs to be
     * implemented for those type of bean styles. This default implementation
     * throws {@code IllegalStateException}.
     * <p>
     * See {@link ModernBeanStyle} for a concrete implementation of this method.
     * 
     * @param instance
     *            the IBean instance
     * @param setterMethod
     *            the setter method in whose return value should be created
     * @param newValue
     *            the new value of the field, given as a parameter to the setter
     * @return the value that will then be returned from the setter
     */
    default Object createReturnValueForSetterCall(Object instance, Method setterMethod,
            Object newValue) {
        throw new IllegalStateException("createReturnValueForSetterCall() must not be called"
                + "for bean style " + this.getClass().getName());
    }

    /**
     * This method is called during runtime of a bean, more precisely, always
     * when getters of a bean are called to adjust the type of the return value
     * of the getter method.<br>
     * This method is only called for bean styles that allow getter methods that
     * do not match to the type of the field. Therefore this method only needs
     * to be implemented for those type of bean styles. This default
     * implementation throws {@code IllegalStateException}.
     * <p>
     * See {@link ModernBeanStyle} for a concrete implementation of this method.
     * 
     * @param expectedReturnType
     *            the type the field value needs to be converted to
     * @param returnValueWithWrongType
     *            the field value that needs to be converted
     * @return the value that will then be returned from the getter
     */
    default Object convertReturnValueOfGetterCall(Class<?> expectedReturnType,
            Object returnValueWithWrongType) {
        throw new IllegalStateException("unexpected call of convertReturnValueOfGetterCall()"
                + "for bean style " + this.getClass().getName());
    }

}
