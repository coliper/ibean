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
import java.util.Optional;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.InvalidIBeanTypeException;

/**
 * A {@link BeanStyle} implementation that is identical to the
 * {@link ClassicBeanStyle} but has also {@link Optional} support. The
 * difference to the {@link ClassicBeanStyle} is that for a property of type
 * <code>T</code> it allows a getter method that either returns <code>T</code>
 * or <code>Optional&lt;T&gt;</code>.
 * 
 * @author alex@coliper.org
 */
public class ClassicBeanStyleWithOptionalSupport extends ClassicBeanStyle {

    public static final ClassicBeanStyleWithOptionalSupport INSTANCE =
            new ClassicBeanStyleWithOptionalSupport();

    /**
     * {@link #INSTANCE} should be the only instance.
     */
    protected ClassicBeanStyleWithOptionalSupport() {
    }

    @Override
    public Class<?> determineFieldTypeFromGetterAndSetter(Class<?> beanType, Method getterMethod,
            Method setterMethod) throws InvalidIBeanTypeException {
        requireNonNull(getterMethod, "getterMethod");
        requireNonNull(setterMethod, "setterMethod");
        Class<?>[] argTypes = setterMethod.getParameterTypes();
        assertForBeanType(beanType, argTypes.length == 1,
                "unexpected no of arguments in setter " + setterMethod);
        final Class<?> getterRetType = getterMethod.getReturnType();
        final Class<?> setterArgType = argTypes[0];
        assertForBeanType(beanType,
                setterArgType == getterRetType || getterRetType == Optional.class,
                "incompatible types of getter " + getterMethod + "with setter " + setterMethod);
        return setterArgType;
    }

}
