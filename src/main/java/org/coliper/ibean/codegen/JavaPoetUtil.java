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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
public class JavaPoetUtil {

    public static MethodSpec.Builder methodSpecBuilderFromOverride(Method method,
            String... argNames) {
        Objects.requireNonNull(method, "method");
        Builder builder = MethodSpec.methodBuilder(method.getName()).addAnnotation(Override.class)
                .returns(method.getReturnType());
        final Modifier visibility = getMethodVisibility(method);
        if (visibility != null) {
            builder.addModifiers(visibility);
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        Preconditions.checkArgument(paramTypes.length == argNames.length,
                "invalid number of argument names provided");
        int i = 0;
        for (Class<?> paramType : paramTypes) {
            builder.addParameter(paramType, argNames[i++]);
        }
        Type[] thrownExceptionTypes = method.getGenericExceptionTypes();
        for (Type type : thrownExceptionTypes) {
            builder.addException(type);
        }
        builder.returns(method.getReturnType());
        return builder;
    }

    public static Modifier getMethodVisibility(Method method) {
        Objects.requireNonNull(method, "method");
        final int modifiers = method.getModifiers();
        if (java.lang.reflect.Modifier.isPublic(modifiers)) {
            return Modifier.PUBLIC;
        }
        if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
            return Modifier.PRIVATE;
        }
        if (java.lang.reflect.Modifier.isProtected(modifiers)) {
            return Modifier.PROTECTED;
        }
        return null;
    }

    public static String createLiteralBoxingSnippet(Class<?> literalType) {
        if (!literalType.isPrimitive()) {
            return "$L"; // no conversion code required
        }

        // do manual boxing for primitive types
        if (byte.class == literalType) {
            return "Byte.valueOf($L)";
        }
        if (short.class == literalType) {
            return "Short.valueOf($L)";
        }
        if (int.class == literalType) {
            return "Integer.valueOf($L)";
        }
        if (long.class == literalType) {
            return "Long.valueOf($L)";
        }
        if (float.class == literalType) {
            return "Float.valueOf($L)";
        }
        if (double.class == literalType) {
            return "Double.valueOf($L)";
        }
        if (char.class == literalType) {
            return "Character.valueOf($L)";
        }
        if (boolean.class == literalType) {
            return "Boolean.valueOf($L)";
        }

        throw new RuntimeException("unexpected primitive type " + literalType);
    }

    public static String callInstanceMethodSnippet(Method method) {
        return null;
    }

    public static String callStaticMethodSnippet(Class<?> type, String methodName) {
        return null;
    }

    private static String callMethodSnippet(Class<?> type, String methodName, String instance) {
        return null;
    }

}
