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

import javax.lang.model.element.Modifier;

import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class HashCodeMethodCodeGenerator {

    // TODO: move to ReflectionUtil
    private static final String TO_STRING_METHOD_NAME;
    static {
        try {
            TO_STRING_METHOD_NAME = Object.class.getMethod("hashCode").getName();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    private final BeanCodeElements codeElements;
    private final IBeanTypeMetaInfo<?> metaInfo;

    /**
     * @param codeElements
     * @param metaInfo
     */
    HashCodeMethodCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo) {
        this.codeElements = codeElements;
        this.metaInfo = metaInfo;
    }

    MethodSpec createMethod() {
        Builder methodBuilder = MethodSpec.methodBuilder(TO_STRING_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC).addAnnotation(Override.class);
        methodBuilder.addStatement("return -23");
        return methodBuilder.build();
    }

}
