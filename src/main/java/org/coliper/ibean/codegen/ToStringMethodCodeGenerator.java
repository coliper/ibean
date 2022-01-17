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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class ToStringMethodCodeGenerator {

    private static final Method TO_STRING_METHOD =
            MethodUtils.getMatchingAccessibleMethod(Object.class, "toString");
    private static final String TO_STRING_BUILDER_VARIABLE_NAME = "bldr";

    private final IBeanTypeMetaInfo<?> metaInfo;
    private final BeanCodeElements codeElements;

    /**
     * @param codeElements
     * @param metaInfo
     */
    ToStringMethodCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo) {
        this.metaInfo = metaInfo;
        this.codeElements = codeElements;
    }

    MethodSpec createMethod() {
        final Builder methodBuilder = JavaPoetUtil.methodSpecBuilderFromOverride(TO_STRING_METHOD);
        methodBuilder.addStatement(
                "$T $N = new $T(this, this.$N.toStringStyleForInterface($T.class))",
                ToStringBuilder.class, TO_STRING_BUILDER_VARIABLE_NAME, ToStringBuilder.class,
                CommonCodeSnippets.FACTORY_FIELD_NAME, this.metaInfo.beanType());
        for (IBeanFieldMetaInfo iBeanFieldMetaInfo : this.metaInfo.fieldMetaInfos()) {
            this.addStatementForProperty(methodBuilder, iBeanFieldMetaInfo.fieldName());
        }
        methodBuilder.addStatement("return $N.toString()", TO_STRING_BUILDER_VARIABLE_NAME);
        return methodBuilder.build();
    }

    private void addStatementForProperty(MethodSpec.Builder builder, String propertyName) {
        final String fieldName = this.codeElements.fieldNameFromPropertyName(propertyName);
        builder.addStatement("$N.append($S, this.$N)", TO_STRING_BUILDER_VARIABLE_NAME,
                propertyName, fieldName);
    }

}
