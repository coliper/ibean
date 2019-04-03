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
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class EqualsMethodCodeGenerator {

    private static final Method EQUALS_METHOD =
            MethodUtils.getMatchingAccessibleMethod(Object.class, "equals", Object.class);
    private static final String ARGUMENT_NAME = "obj";
    private final BeanCodeElements codeElements;
    private final IBeanTypeMetaInfo<?> metaInfo;

    /**
     * @param codeElements
     * @param metaInfo
     */
    EqualsMethodCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo) {
        this.codeElements = codeElements;
        this.metaInfo = metaInfo;
    }

    MethodSpec createMethod() {
        Builder methodBuilder =
                JavaPoetUtil.methodSpecBuilderFromOverride(EQUALS_METHOD, ARGUMENT_NAME);
        final CodeBlock methodBlock;
        if (this.metaInfo.customEqualsMethod().isPresent()) {
            methodBlock = createCustomEqualsCallBlock();
        } else {
            methodBlock = createDefaultEqualsBlock();
        }
        methodBuilder.addCode(methodBlock);
        return methodBuilder.build();
    }

    private CodeBlock createCustomEqualsCallBlock() {
        CodeBlock block = CodeBlock.builder()
                .addStatement("return $L()", this.metaInfo.customEqualsMethod().get().getName())
                .build();
        return block;
    }

    private CodeBlock createDefaultEqualsBlock() {
        final com.squareup.javapoet.CodeBlock.Builder codeBuilder = CodeBlock.builder();
        this.addDefaultChecks(codeBuilder);
        this.addFieldLoop(codeBuilder);
        return codeBuilder.build();
    }

    private void addDefaultChecks(com.squareup.javapoet.CodeBlock.Builder codeBuilder) {
        codeBuilder.beginControlFlow("if ($L == null)", ARGUMENT_NAME);
        codeBuilder.addStatement("return false");
        codeBuilder.endControlFlow();

        codeBuilder.beginControlFlow("if ($L == this)", ARGUMENT_NAME);
        codeBuilder.addStatement("return true");
        codeBuilder.endControlFlow();

        codeBuilder.beginControlFlow("if ($L.getClass() != this.getClass())", ARGUMENT_NAME);
        codeBuilder.addStatement("return false");
        codeBuilder.endControlFlow();
    }

    private void addFieldLoop(com.squareup.javapoet.CodeBlock.Builder codeBuilder) {
        codeBuilder.addStatement("$L other =$W($L)obj", this.codeElements.beanClassName(),
                this.codeElements.beanClassName());

        final StringBuilder statement = new StringBuilder("return new $T()");
        Collection<String> fields = this.codeElements.fieldNames();
        for (String field : fields) {
            statement.append("$Z.append(this.").append(field).append(", other.").append(field)
                    .append(")");
        }
        statement.append("$Z.isEquals()");
        codeBuilder.addStatement(statement.toString(), EqualsBuilder.class);
    }

}
