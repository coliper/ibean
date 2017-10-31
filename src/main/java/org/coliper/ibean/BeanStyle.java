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

import org.coliper.ibean.beanstyle.ClassicBeanStyle;
import org.coliper.ibean.beanstyle.ClassicBeanStyleWithOptionalSupport;
import org.coliper.ibean.beanstyle.ModernBeanStyle;
import org.coliper.ibean.beanstyle.ModernBeanStyleWithOptionalSupport;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * @author alex@coliper.org
 *
 */
public abstract class BeanStyle {

    public static final BeanStyle CLASSIC = ClassicBeanStyle.INSTANCE;
    public static final BeanStyle MODERN = ModernBeanStyle.INSTANCE;
    public static final BeanStyle CLASSIC_WITH_OPTIONAL =
            ClassicBeanStyleWithOptionalSupport.INSTANCE;
    public static final BeanStyle MODERN_WITH_OPTIONAL =
            ModernBeanStyleWithOptionalSupport.INSTANCE;

    public abstract boolean isGetterMethod(Class<?> beanType, Method method);

    public abstract boolean isSetterMethod(Class<?> beanType, Method method);

    public abstract String convertGetterNameToFieldName(String getterName);

    public abstract String convertSetterNameToFieldName(String setterName);

    protected static int getNoOfMethodParameters(Method method) {
        requireNonNull(method, "method");
        return method.getParameterTypes().length;
    }

    protected static void assertMethodBelongsToType(Method method, Class<?> type) {
        requireNonNull(method, "method");
        requireNonNull(type, "type");
        if (!ReflectionUtil.doesMethodBelongToType(method, type)) {
            throw new IllegalArgumentException(
                    "method '" + method.getName() + "' does not belong to class " + type);
        }
    }

    protected boolean hasSetterMethodSignature(Method method) {
        return getNoOfMethodParameters(method) == 1;
    }

    protected boolean hasGetterMethodSignature(Method method) {
        return getNoOfMethodParameters(method) == 0 && method.getReturnType() != void.class;
    }

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
        return fieldNameFromGetter.equals(fieldNameFromSetter) && typeFromGetter == typeFromSetter;
    }

    public Class<?> determineFieldTypeFromGetterAndSetter(Class<?> beanType, Method getterMethod,
            Method setterMethod) throws IllegalArgumentException {
        requireNonNull(getterMethod, "getterMethod");
        requireNonNull(setterMethod, "setterMethod");
        Class<?>[] argTypes = setterMethod.getParameterTypes();
        checkArgument(argTypes.length == 1, "unexpected no of arguments in setter " + setterMethod);
        checkArgument(argTypes[0] == getterMethod.getReturnType(),
                "incompatible types of getter " + getterMethod + "with setter " + setterMethod);
        return argTypes[0];
    }

    public Object createReturnValueForSetterCall(Object instance, Method setterMethod,
            Object newValue) {
        return null;
    }

    /**
     * As stateless we treat all instances of one {@link BeanStyle} class as
     * equal.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass() == obj.getClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

}
