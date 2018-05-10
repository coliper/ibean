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

package org.coliper.ibean.util;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.ClassUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Reflection;

/**
 * Contains static helper methods related to classes and reflection.
 * 
 * @author alex@coliper.org
 */
public class ReflectionUtil {

    //@formatter:off
    public static final Map<Class<?>, Object> DEFAULTS_MAP =
            new ImmutableMap.Builder<Class<?>, Object>()
                    .put(byte.class, Byte.valueOf((byte) 0))
                    .put(short.class, Short.valueOf((short) 0))
                    .put(int.class, Integer.valueOf(0))
                    .put(long.class, Long.valueOf(0L))
                    .put(float.class, Float.valueOf((float) 0.0))
                    .put(double.class, Double.valueOf(0.0))
                    .put(boolean.class, Boolean.FALSE)
                    .put(char.class, Character.valueOf('\u0000'))
                    .build();
    //@formatter:on

    private static Map<Class<?>, List<Class<?>>> SUPERTYPE_INCL_CACHE = new ConcurrentHashMap<>();

    /**
     * Determines if two classes are linked in a class hierarchy.
     * 
     * @param type1
     *            a class or <code>null</code>
     * @param type2
     *            the class to compare type1 with or <code>null</code>
     * @return <code>true</code> if both classes are identical or if one type is
     *         base class of other. <code>false</code> if one of the given types
     *         or both are <code>null</code> or not in a direct hierarchical
     *         relation.
     */
    public static boolean areClassesRelated(Class<?> type1, Class<?> type2) {
        if (type1 == null || type2 == null) {
            return false;
        }
        return type1 == type2 || type1.isAssignableFrom(type2) || type2.isAssignableFrom(type1);
    }

    /**
     * Checks if method belongs to a given type or super-type.
     * 
     * @param method
     *            the method to check
     * @param type
     *            any class to search for the given method
     * @return <code>true</code> if given method is declared in given type or
     *         one of its super types.
     */
    public static boolean doesMethodBelongToType(Method method, Class<?> type) {
        return method.getDeclaringClass().isAssignableFrom(type);
    }

    /**
     * Calls {@link Method#invoke(Object, Object...)} on the given method but
     * catches away all thrown Exceptions.
     * 
     * @param object
     *            the object the method to call on. Can be <code>null</code> for
     *            static methods
     * @param method
     *            the method to execute
     * @return the value returned from method call
     * @throws RuntimeException
     *             wrapped around the checked exception from the method
     *             invocation
     */
    public static Object invokeMethodUnchecked(Object object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Collects all classes and interfaces a given type extends or implements.
     * This methods executes fast as its results are cached.
     * 
     * @param rootType
     *            the class to examine
     * @return an immutable list of super classes and interfaces including the
     *         given type but excluding {@link Object}
     */
    public static List<Class<?>> getSuperTypesInclRoot(final Class<?> rootType) {
        return SUPERTYPE_INCL_CACHE.computeIfAbsent(rootType,
                (t) -> getSuperTypesInclRootUncached(t));
    }

    private static List<Class<?>> getSuperTypesInclRootUncached(Class<?> rootType) {
        Objects.requireNonNull(rootType, "rootType");
        final Iterable<Class<?>> classIterable =
                ClassUtils.hierarchy(rootType, ClassUtils.Interfaces.INCLUDE);
        final ImmutableList.Builder<Class<?>> list = ImmutableList.builder();
        classIterable.forEach((c) -> {
            if (c != Object.class)
                list.add(c);
        });
        return list.build();
    }

    private static final class LastMethodCallRecordingProxy implements InvocationHandler {
        private Method lastMethodCalled = null;

        public Method getLastMethodCalled() {
            return lastMethodCalled;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            this.lastMethodCalled = method;
            if (method.getReturnType().isPrimitive() && method.getReturnType() != void.class) {
                return primitiveTypeDefaultValue(method.getReturnType());
            }
            return null;
        }
    }

    public static <T> Method lookupInterfaceMethod(Class<T> interfaceType,
            Consumer<T> methodSpecifier) {
        requireNonNull(interfaceType, "interfaceType");
        requireNonNull(methodSpecifier, "methodSpecifier");
        checkArgument(interfaceType.isInterface(), "%s is not an interface type", interfaceType);
        final LastMethodCallRecordingProxy handler = new LastMethodCallRecordingProxy();
        T proxy = Reflection.newProxy(interfaceType, handler);
        methodSpecifier.accept(proxy);
        final Method method = handler.getLastMethodCalled();
        checkArgument(method != null,
                "given methodSpecifier does not call a method on interface %s", interfaceType);
        return method;
    }

    /*
     * no instances
     */
    private ReflectionUtil() {

    }

    /**
     * Returnes the default value for a given primitive type, basically all
     * flavours of zero for the number types and <code>false</code> for
     * <code>boolean.class</code>.
     */
    public static Object primitiveTypeDefaultValue(Class<?> primitiveType) {
        requireNonNull(primitiveType, "primitiveType");
        Object ret = ReflectionUtil.DEFAULTS_MAP.get(primitiveType);
        checkArgument(ret != null, "%s is not a primitive type", primitiveType);
        return ret;
    }

}
