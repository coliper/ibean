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

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * Used for creating IBean relevant meta information for a given IBean
 * interface. See {@link #parse(Class, BeanStyle, List)} for details.
 * 
 * @author alex@coliper.org
 */
public class IBeanMetaInfoParser {

    private static final String CUSTOM_EQUALS_METHOD_NAME = "_equals";
    private static final String CUSTOM_HASHCODE_METHOD_NAME = "_hashCode";

    /**
     * Validates if a given class matches all criteria for being an IBean
     * interface and if that is the case then retrieves all relevant information
     * about the all fields contained in the class. See
     * {@link IBeanFieldMetaInfo} about what information is gathered for each
     * field.
     * <p>
     * A given type is a valid IBean interface if following criteria match:
     * <ul>
     * <li>The bean type contains no methods other setters and getters, with the
     * exception of methods of some specifically excluded super interfaces.</li>
     * <li>All getters and setters match to the used {@link BeanStyle}.</li>
     * <li>Every getter method has a matching setter and vice versa.</li>
     * </ul>
     * 
     * @param beanType
     *            the IBean interface candidate to parse
     * @param beanStyle
     *            the bean style to use for parsing
     * @param ignorableSuperInterfaces
     *            a list of super interfaces of the provided {@code beanType}
     *            whose methods should be excluded from being considered as
     *            getters and setters. These interfaces are typically extension
     *            interfaces like {@link Freezable}.
     * @param <T>
     *            generic type {@code T} is the bean class provided with
     *            parameter {@code beanType}
     * @return the meta information for the IBean interface
     * @throws InvalidIBeanTypeException
     *             if the given {@code beanType} does not match the criteria for
     *             being a valid IBean interface
     */
    public <T> IBeanTypeMetaInfo<T> parse(Class<T> beanType, BeanStyle beanStyle,
            List<Class<?>> ignorableSuperInterfaces) throws InvalidIBeanTypeException {
        requireNonNull(beanType, "beanType");
        requireNonNull(beanStyle, "beanStyle");
        requireNonNull(ignorableSuperInterfaces, "ignorableSuperInterfaces");
        this.assertOrThrowException(beanType.isInterface(), beanType, "type is not an interface");

        final List<Method> getterSetterMethods =
                this.lookupPotentialGettersAndSetters(beanType, ignorableSuperInterfaces);
        final List<IBeanFieldMetaInfo> fieldMetaList =
                this.createFieldMetaInfo(beanType, beanStyle, getterSetterMethods);
        final Method customEqualsMethod = this.lookupCustomEqualsMethod(beanType);
        final Method customHashCodeMethod = this.lookupCustomHashCodeMethod(beanType);
        return new IBeanTypeMetaInfo<>(beanType, beanStyle, fieldMetaList, customEqualsMethod,
                customHashCodeMethod);
    }

    private Method lookupCustomEqualsMethod(Class<?> beanType) {
        final int noOfParams = 1;
        return this.lookupUniqueDefaultMethodWithName(beanType, CUSTOM_EQUALS_METHOD_NAME,
                noOfParams);
    }

    private Method lookupCustomHashCodeMethod(Class<?> beanType) {
        final int noOfParams = 0;
        return this.lookupUniqueDefaultMethodWithName(beanType, CUSTOM_HASHCODE_METHOD_NAME,
                noOfParams);
    }

    private List<Method> lookupPotentialGettersAndSetters(Class<?> beanType,
            List<Class<?>> ignorableSuperInterfaces) {
        Method[] allMethods = beanType.getMethods();
        final List<Method> filteredMethods = new ArrayList<>();
        for (Method method : allMethods) {
            if (method.getDeclaringClass() == Object.class) {
                continue; // toString(), equals() etc can be ignored
            }
            if (method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue; // ignore static and Java8 default methods
            }
            if (isIgnorableMethod(method, ignorableSuperInterfaces)) {
                continue;
            }
            filteredMethods.add(method);
        }
        return filteredMethods;
    }

    /**
     * @return
     */
    private boolean isIgnorableMethod(Method method, List<Class<?>> ignorableSuperInterfaces) {
        for (Class<?> ignorableType : ignorableSuperInterfaces) {
            if (ReflectionUtil.doesMethodBelongToType(method, ignorableType)) {
                return true;
            }
        }
        return false;
    }

    // datastructure to temporary hold the (incomplete) field meta information
    private static class TempFieldMeta {
        static final Function<TempFieldMeta, IBeanFieldMetaInfo> CONVERTER =
                t -> new IBeanFieldMetaInfo(t.name, t.type, t.getter, t.setter, t.ordinal);
        String name;
        int ordinal;
        Class<?> type;
        Method getter;
        Method setter;

        TempFieldMeta(String name) {
            this.name = name;
        }
    }

    /**
     * @param beanType
     * @param beanStyle
     * @param getterSetterMethods
     * @return
     */
    private List<IBeanFieldMetaInfo> createFieldMetaInfo(final Class<?> beanType,
            BeanStyle beanStyle, List<Method> getterSetterMethods) {
        // we use a sorted map to have a predictable order of field metas
        // (sorted by field name)
        final Map<String, TempFieldMeta> metaMap = new TreeMap<>();
        for (Method method : getterSetterMethods) {
            this.addMethodToMetaMap(beanType, beanStyle, metaMap, method);
        }

        TempFieldMeta[] tmpMeta = metaMap.values().toArray(new TempFieldMeta[metaMap.size()]);
        for (int i = 0; i < tmpMeta.length; i++) {
            tmpMeta[i].ordinal = i;
            this.validateMeta(beanType, tmpMeta[i]);
        }
        return Arrays.stream(tmpMeta).map(TempFieldMeta.CONVERTER).collect(Collectors.toList());
    }

    /**
     * @param metaMap
     * @param method
     */
    private void addMethodToMetaMap(Class<?> beanType, BeanStyle beanStyle,
            Map<String, TempFieldMeta> metaMap, Method method) {
        boolean isSetterOrGetter = false;
        if (beanStyle.isGetterMethod(method)) {
            addGetterToMetaMap(beanType, beanStyle, metaMap, method);
            isSetterOrGetter = true;
        }
        if (beanStyle.isSetterMethod(method)) {
            addSetterToMetaMap(beanType, beanStyle, metaMap, method);
            isSetterOrGetter = true;
        }
        this.assertOrThrowException(isSetterOrGetter, beanType, "method %s is not setter or getter",
                method);
    }

    /**
     */
    private void addSetterToMetaMap(Class<?> beanType, BeanStyle beanStyle,
            Map<String, TempFieldMeta> metaMap, Method setter) {
        String fieldName = beanStyle.convertSetterNameToFieldName(setter.getName());
        TempFieldMeta meta = metaMap.computeIfAbsent(fieldName, k -> new TempFieldMeta(k));
        this.assertOrThrowException(meta.setter == null, beanType, "clashing setters %s and %s",
                setter, meta.setter);
        meta.setter = setter;
        this.setFieldTypeIfNullAndGetterSetterGiven(beanType, beanStyle, meta);
    }

    /**
     */
    private void addGetterToMetaMap(Class<?> beanType, BeanStyle beanStyle,
            Map<String, TempFieldMeta> metaMap, Method method) {
        String fieldName = beanStyle.convertGetterNameToFieldName(method.getName());
        metaMap.computeIfAbsent(fieldName, k -> new TempFieldMeta(k));
        TempFieldMeta meta = metaMap.get(fieldName);
        this.assertOrThrowException(meta.getter == null, beanType, "clashing getters %s and %s",
                method, meta.getter);
        meta.getter = method;
        this.setFieldTypeIfNullAndGetterSetterGiven(beanType, beanStyle, meta);
    }

    /**
     * @param method
     * @param meta
     */
    private void setFieldTypeIfNullAndGetterSetterGiven(Class<?> beanType, BeanStyle beanStyle,
            TempFieldMeta meta) {
        if (meta.type != null) {
            return; // already set, nothing to do
        }
        if (meta.getter != null && meta.setter != null) {
            meta.type = beanStyle.determineFieldTypeFromGetterAndSetter(beanType, meta.getter,
                    meta.setter);
        }
    }

    private void validateMeta(Class<?> beanType, TempFieldMeta meta) {
        checkState(meta.name != null, "missing fieldName in internal structure");
        checkState(meta.getter != null || meta.setter != null,
                "missing methods in internal structure");
        this.assertOrThrowException(meta.getter != null, beanType, "missing getter for setter %s",
                meta.setter);
        this.assertOrThrowException(meta.setter != null, beanType, "missing setter for getter %s",
                meta.getter);
        checkState(meta.type != null, "missing field type in internal structure");
    }

    private void assertOrThrowException(boolean condition, Class<?> beanType, String message,
            Object... args) {
        if (!condition) {
            throw new InvalidIBeanTypeException(beanType, String.format(message, args));
        }
    }

    private Method lookupUniqueDefaultMethodWithName(Class<?> beanType, String methodName,
            int noOfParams) {
        final Method[] allMethods = beanType.getMethods();
        Method foundMethod = null;

        for (Method method : allMethods) {
            if (!method.isDefault()) {
                continue;
            }
            if (!methodName.equals(method.getName())) {
                continue;
            }
            if (method.getParameterCount() != noOfParams) {
                continue;
            }
            if (foundMethod != null) {
                throw new InvalidIBeanTypeException(beanType,
                        "several default methods with name '" + methodName + "' found");
            }
            foundMethod = method;
        }

        return foundMethod;
    }

}
