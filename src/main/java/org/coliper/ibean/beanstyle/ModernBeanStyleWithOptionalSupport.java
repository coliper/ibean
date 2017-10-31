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
import java.util.Optional;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.extension.OptionalSupport;

/**
 * A {@link BeanStyle} implementation that is identical to the
 * {@link ModernBeanStyle} but has also {@link Optional} support. The difference
 * to the {@link ModernBeanStyle} is that for a property of type <code>T</code>
 * it allows a getter method that either returns <code>T</code> or
 * <code>Optional&lt;T&gt;</code>.
 * <p>
 * This bean style is only allowed for bean types that implement extension
 * interface {@link OptionalSupport}. See {@link OptionalSupport} for more
 * information.
 * 
 * @author alex@coliper.org
 */
public class ModernBeanStyleWithOptionalSupport extends ModernBeanStyle {

    public static final ModernBeanStyleWithOptionalSupport INSTANCE =
            new ModernBeanStyleWithOptionalSupport();

    /**
     * {@link #INSTANCE} should be the only instance.
     */
    protected ModernBeanStyleWithOptionalSupport() {
    }

    @Override
    public boolean isSetterForGetter(Class<?> beanType, Method getterMethod, Method setterMethod) {
        requireNonNull(beanType, "beanType");
        requireNonNull(getterMethod, "getterMethod");
        requireNonNull(setterMethod, "setterMethod");
        checkArgument(this.isGetterMethod(beanType, getterMethod), "not a getter: " + getterMethod);
        checkArgument(this.isSetterMethod(beanType, setterMethod), "not a setter: " + setterMethod);

        String fieldNameFromGetter = this.convertGetterNameToFieldName(getterMethod.getName());
        String fieldNameFromSetter = this.convertSetterNameToFieldName(setterMethod.getName());
        Class<?> typeFromSetter = setterMethod.getParameterTypes()[0];
        Class<?> typeFromGetter = getterMethod.getReturnType();
        final boolean getterAndSetterTypesCompatible = typeFromGetter == typeFromSetter
                || isAllowedOptionalReturnTypeForGetter(beanType, typeFromGetter);
        return fieldNameFromGetter.equals(fieldNameFromSetter) && getterAndSetterTypesCompatible;
    }

    @Override
    public Class<?> determineFieldTypeFromGetterAndSetter(Class<?> beanType, Method getterMethod,
            Method setterMethod) throws IllegalArgumentException {
        requireNonNull(getterMethod, "getterMethod");
        requireNonNull(setterMethod, "setterMethod");
        Class<?>[] argTypes = setterMethod.getParameterTypes();
        checkArgument(argTypes.length == 1, "unexpected no of arguments in setter " + setterMethod);
        final Class<?> getterRetType = getterMethod.getReturnType();
        final Class<?> setterArgType = argTypes[0];
        checkArgument(
                setterArgType == getterRetType
                        || isAllowedOptionalReturnTypeForGetter(beanType, getterRetType),
                "incompatible types of getter " + getterMethod + "with setter " + setterMethod);
        return setterArgType;
    }

    /**
     * @param beanType
     * @param typeFromGetter
     * @return
     */
    private boolean isAllowedOptionalReturnTypeForGetter(Class<?> beanType,
            Class<?> typeFromGetter) {
        return OptionalSupport.class.isAssignableFrom(beanType) && typeFromGetter == Optional.class;
    }

    @Override
    protected boolean hasGetterMethodSignature(Method method) {
        return super.hasGetterMethodSignature(method) || method.getReturnType() == Optional.class;
    }

}
