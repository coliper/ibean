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
import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
public class JavaPoetUtil {

    public static MethodSpec.Builder methodSpecBuilderFromOverride(Method method) {
        Objects.requireNonNull(method, "method");
        Builder builder = MethodSpec.methodBuilder(method.getName()).addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC).returns(method.getReturnType());
        Class<?>[] paramTypes = method.getParameterTypes();
        for (Class<?> paramType : paramTypes) {
            builder.addParameter(parameterSpec);
        }
        return builder;
    }

}
