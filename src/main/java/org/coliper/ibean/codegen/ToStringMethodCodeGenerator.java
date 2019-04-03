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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class ToStringMethodCodeGenerator {

    private static final Method TO_STRING_METHOD =
            MethodUtils.getMatchingAccessibleMethod(Object.class, "toString");
    private final BeanCodeElements codeElements;

    /**
     * @param codeElements
     * @param metaInfo
     */
    ToStringMethodCodeGenerator(BeanCodeElements codeElements) {
        this.codeElements = codeElements;
    }

    MethodSpec createMethod() {
        Builder methodBuilder = JavaPoetUtil.methodSpecBuilderFromOverride(TO_STRING_METHOD);
        final CodeBlock.Builder methodBlock = CodeBlock.builder();
        this.addStatement(methodBlock);
        methodBuilder.addCode(methodBlock.build());
        return methodBuilder.build();
    }

    private void addStatement(CodeBlock.Builder builder) {
        final StringBuilder statement =
                new StringBuilder("return new $T(this, this.$L.toStringStyle())");
        Collection<String> fields = this.codeElements.fieldNames();
        for (String field : fields) {
            statement.append("$Z.append(").append(field).append(")");
        }
        statement.append("$Z.toString()");
        builder.addStatement(statement.toString(), ToStringBuilder.class,
                CommonCodeSnippets.FACTORY_FIELD_NAME);
    }

}
