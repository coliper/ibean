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

package org.coliper.ibean.proxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.util.RecursionCycleDetector;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * Implementation for an IBean interface using Java proxy technology. Therefore
 * it implements {@link InvocationHandler}.
 * <p>
 * One ProxyIBean instance represents one IBean instance. Instances of
 * {@link ProxyIBean} are exclusively created by {@link ProxyIBeanFactory}.
 * 
 * @author alex@coliper.org
 */
class ProxyIBean<T> implements InvocationHandler, IBeanFieldAccess {

    /*
     * An instance of this class represents one bean instance. It holds
     * following information: - meta information about the bean type in field
     * "context" - the bean values as an object array in field "beanValues"
     * 
     * See invoke method for details how method calls to the bean are handled.
     */

    private static final String METHOD_NAME_TO_STRING = "toString";
    private static final String METHOD_NAME_HASH_CODE = "hashCode";
    private static final String METHOD_NAME_EQUALS = "equals";

    // As field values may contain other IBeans Object-type methods hashCode()
    // and equals() may
    // run into endless recursion. To prevent this RecursionCycleDetectors are
    // used here.
    private static final RecursionCycleDetector<Object> RECURSION_DETECTOR_HASHCODE =
            new RecursionCycleDetector<Object>(Integer.valueOf(1));
    private static final RecursionCycleDetector<Object> RECURSION_DETECTOR_EQUALS =
            new RecursionCycleDetector<Object>(Boolean.FALSE);

    private final IBeanContext<T> context;
    private final ExtensionHandlerDispatcher extendedInterfaceHandler;
    private final Object[] beanValues;

    ProxyIBean(IBeanContext<T> context, ExtensionHandlerDispatcher handler) {
        requireNonNull(context, "context");
        requireNonNull(handler, "handler");
        this.context = context;
        this.extendedInterfaceHandler = handler;
        this.beanValues = initBeanValues(context);
    }

    private Object[] initBeanValues(IBeanContext<T> context) {
        Object[] val = new Object[context.metaInfo().noOfFields()];
        // init all "primitive" values with defaults
        for (int i = 0; i < val.length; i++) {
            IBeanFieldMetaInfo fieldMetaInfo = context.metaInfo().fieldMetaInfos().get(i);
            Class<?> fieldType = fieldMetaInfo.fieldType();
            if (fieldType.isPrimitive()) {
                val[i] = ReflectionUtil.primitiveTypeDefaultValue(fieldType);
            }
        }
        return val;
    }

    /*
     * Handles all method calls to the bean: - Getter and setter calls are
     * handled by this class (see handleGetterOrSetter() below). - Calls to
     * Object-type methods like toString() are also handled by this class (see
     * handleRootObjectTypeMethod() below). - Calls to extension interface
     * methods are dispatched to the contained "extendedInterfaceHandler".
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.isRootObjectTypeMethod(method)) {
            return this.handleRootObjectTypeMethod(proxy, method, args);
        }
        if (this.extendedInterfaceHandler.canHandleCall(method)) {
            return this.extendedInterfaceHandler.handleExtendedInterfaceCall(this.context, this,
                    proxy, method, args);
        }
        if (method.isDefault()) {
            return this.handleDefaultMethod(proxy, method, args);
        }
        return this.handleGetterOrSetter(proxy, method, args);
    }

    private Object handleDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (SystemUtils.IS_JAVA_1_8) {
            return this.handleDefaultMethodJava8(proxy, method, args);
        } else {
            return this.handleDefaultMethodJava9(proxy, method, args);
        }
    }

    private Object handleDefaultMethodJava9(Object proxy, Method method, Object[] args)
            throws Throwable {
        return MethodHandles.lookup()
                .findSpecial(method.getDeclaringClass(), method.getName(),
                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                        method.getDeclaringClass())
                .bindTo(proxy).invokeWithArguments(args);
    }

    private Object handleDefaultMethodJava8(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();
        Constructor<MethodHandles.Lookup> constructor =
                MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);
        return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    private boolean isRootObjectTypeMethod(Method method) {
        return method.getDeclaringClass() == Object.class;
    }

    private Object handleRootObjectTypeMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        switch (method.getName()) {
        case METHOD_NAME_EQUALS:
            checkState(args.length == 1);
            return this.handleEqualsMethod(proxy, method, args[0]);
        case METHOD_NAME_HASH_CODE:
            checkState(args == null || args.length == 0);
            return this.handleHashCodeMethod(proxy, method);
        case METHOD_NAME_TO_STRING:
            checkState(args == null || args.length == 0);
            return this.handleToStringMethod(proxy, method);
        default:
            throw new IllegalStateException("unexpected method call " + method);
        }
    }

    private Object handleToStringMethod(final Object proxy, final Method method) {
        // ToStringBuilder has its own cycle detection, therefore no cycle
        // detection here
        final StringBuffer buffer = new StringBuffer();
        final ToStringBuilder builder =
                new ToStringBuilder(proxy, this.context.toStringStyle(), buffer);
        this.correctClassNameInToStringBuffer(proxy, buffer);
        List<IBeanFieldMetaInfo> fieldMetas = this.context.metaInfo().fieldMetaInfos();
        for (int index = 0; index < fieldMetas.size(); index++) {
            builder.append(fieldMetas.get(index).fieldName(), this.beanValues[index]);
        }
        return builder.build();
    }

    /**
     * Replaces the proxy class name in the toString buffer with the interface
     * name. ToStringBuilder uses the real class name of an object as prefix. In
     * our case this is the cryptic name of the proxy class. Instead we want to
     * have the interface name to be displayed.
     * 
     */
    private void correctClassNameInToStringBuffer(Object proxy, StringBuffer buffer) {
        final String proxyClassName = proxy.getClass().getName();
        String interfaceName = this.context.metaInfo().beanType().getName();
        searchAndReplaceInStringBuffer(buffer, proxyClassName, interfaceName);

        final String proxyClassNameShort = ClassUtils.getShortClassName(proxyClassName);
        String interfaceNameShort =
                ClassUtils.getShortClassName(this.context.metaInfo().beanType().getName());
        searchAndReplaceInStringBuffer(buffer, proxyClassNameShort, interfaceNameShort);
    }

    private static boolean searchAndReplaceInStringBuffer(StringBuffer buffer, String searchString,
            String replaceString) {
        int index = buffer.indexOf(searchString);
        if (index >= 0) {
            buffer.replace(index, index + searchString.length(), replaceString);
            return true;
        }
        return false;
    }

    private Object handleHashCodeMethod(Object proxy, Method method) throws Throwable {
        if (this.context.metaInfo().customHashCodeMethod().isPresent()) {
            return this.invokeBeanTypeDefaultMethod(proxy,
                    this.context.metaInfo().customHashCodeMethod().get());
        }
        return RECURSION_DETECTOR_HASHCODE.executeWithCycleDetection(proxy,
                () -> this.handleHashCodeMethodWithCycleProtection(proxy, method));
    }

    private Object handleHashCodeMethodWithCycleProtection(Object proxy, Method method) {
        return Integer.valueOf(Arrays.hashCode(this.beanValues));
    }

    private Object handleEqualsMethod(Object proxy, Method method, Object other) throws Throwable {
        if (this.context.metaInfo().customEqualsMethod().isPresent()) {
            return this.invokeBeanTypeDefaultMethod(proxy,
                    this.context.metaInfo().customEqualsMethod().get(), other);
        }
        return RECURSION_DETECTOR_EQUALS.executeWithCycleDetection(proxy,
                () -> this.handleEqualsMethodWithCycleProtection(proxy, method, other));
    }

    private Object handleEqualsMethodWithCycleProtection(Object proxy, Method method,
            Object other) {
        if (other == null) {
            return Boolean.FALSE;
        }
        if (!proxy.getClass().equals(other.getClass())) {
            return Boolean.FALSE;
        }

        // iterate over all fields and comparing field values of both objects
        List<IBeanFieldMetaInfo> fieldMetas = this.context.metaInfo().fieldMetaInfos();
        for (int index = 0; index < fieldMetas.size(); index++) {
            // calling getter on other object to retrieve value from it
            Method getter = fieldMetas.get(index).getterMethod();
            Object otherValue = ReflectionUtil.invokeMethodUnchecked(other, getter);
            if (!Objects.equals(this.beanValues[index], otherValue)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private Object invokeBeanTypeDefaultMethod(Object bean, Method method) throws Throwable {
        try {
            return method.invoke(bean);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException("unexpected exception calling default method " + method,
                    e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private Object invokeBeanTypeDefaultMethod(Object bean, Method method, Object parameter)
            throws Throwable {
        try {
            return method.invoke(bean, parameter);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException("unexpected exception calling default method " + method,
                    e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private Object handleGetterOrSetter(Object proxy, final Method method, Object[] args) {
        final IBeanFieldMetaInfo meta =
                this.context.metaInfo().findFieldMetaWithMethod(method).orElseThrow(
                        () -> new UnsupportedOperationException("unexpected call of " + method));
        if (method.equals(meta.getterMethod())) {
            checkState(args == null || args.length == 0);
            return this.handleGetter(proxy, meta);
        }
        // assuming we deal with a setter now
        checkState(method.equals(meta.setterMethod()));
        checkState(args != null && args.length == 1);
        return this.handleSetter(proxy, meta, args[0]);
    }

    private Object handleGetter(Object proxy, IBeanFieldMetaInfo fieldMeta) {
        Object originalValue = this.getFieldValue(fieldMeta);
        Object modifiedValue = this.extendedInterfaceHandler.interceptGetterCall(this.context,
                fieldMeta, originalValue, proxy);
        if (this.isFieldTypeDifferentToGetterReturnType(fieldMeta)) {
            modifiedValue = this.context.beanStyleHandler().convertReturnValueOfGetterCall(
                    fieldMeta.getterMethod().getReturnType(), modifiedValue);
        }
        return modifiedValue;
    }

    private boolean isFieldTypeDifferentToGetterReturnType(IBeanFieldMetaInfo fieldMeta) {
        return fieldMeta.fieldType() != fieldMeta.getterMethod().getReturnType();
    }

    private Object handleSetter(Object proxy, IBeanFieldMetaInfo fieldMeta, Object newValue) {
        Object modifiedValueByHandler = this.extendedInterfaceHandler
                .interceptSetterCall(this.context, fieldMeta, newValue, proxy);
        this.setFieldValue(fieldMeta, modifiedValueByHandler);
        if (fieldMeta.setterMethod().getReturnType() != void.class) {
            return this.context.beanStyleHandler().createReturnValueForSetterCall(proxy,
                    fieldMeta.setterMethod(), newValue);
        }
        return null; // for void return type
    }

    /*
     * @see
     * org.coliper.ibean.proxy.IBeanFieldAccess#setFieldValue(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void setFieldValue(final String fieldName, Object newValue) {
        Objects.requireNonNull(fieldName, "fieldName");
        IBeanFieldMetaInfo meta = this.context.metaInfo().findFieldMetaWithFieldName(fieldName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "unknown field name '" + fieldName + "'"));
        this.setFieldValue(meta, newValue);
    }

    /*
     * @see
     * org.coliper.ibean.proxy.IBeanFieldAccess#getFieldValue(java.lang.String)
     */
    @Override
    public Object getFieldValue(String fieldName) {
        Objects.requireNonNull(fieldName, "fieldName");
        IBeanFieldMetaInfo meta = this.context.metaInfo().findFieldMetaWithFieldName(fieldName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "unknown field name '" + fieldName + "'"));
        return this.getFieldValue(meta);
    }

    /*
     * @see
     * org.coliper.ibean.proxy.IBeanFieldAccess#setFieldValue(org.coliper.ibean.
     * IBeanFieldMetaInfo, java.lang.Object)
     */
    @Override
    public void setFieldValue(IBeanFieldMetaInfo fieldMeta, Object newValue) {
        Objects.requireNonNull(fieldMeta, "fieldMeta");
        if (fieldMeta.fieldType().isPrimitive()) {
            checkArgument(newValue != null, "primitive type cannot be set to null");
        }
        this.beanValues[fieldMeta.ordinal()] = newValue;
    }

    /*
     * @see
     * org.coliper.ibean.proxy.IBeanFieldAccess#getFieldValue(org.coliper.ibean.
     * IBeanFieldMetaInfo)
     */
    @Override
    public Object getFieldValue(IBeanFieldMetaInfo fieldMeta) {
        Objects.requireNonNull(fieldMeta, "fieldMeta");
        return this.beanValues[fieldMeta.ordinal()];
    }
}
