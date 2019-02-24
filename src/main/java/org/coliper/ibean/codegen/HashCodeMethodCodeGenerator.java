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

import org.apache.commons.lang3.reflect.MethodUtils;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class HashCodeMethodCodeGenerator {

    private static final Method HASH_CODE_METHOD =
            MethodUtils.getMatchingAccessibleMethod(Object.class, "hashCode");
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
        Builder methodBuilder = JavaPoetUtil.methodSpecBuilderFromOverride(HASH_CODE_METHOD);
        final CodeBlock methodBlock;
        if (this.metaInfo.customHashCodeMethod().isPresent()) {
            methodBlock = createCustomHashCallBlock();
        } else {
            methodBlock = createHashCalculationBlock();
        }
        methodBuilder.addCode(methodBlock);
        return methodBuilder.build();
    }

    private CodeBlock createCustomHashCallBlock() {
        CodeBlock block =
                CodeBlock.of("return $L()", this.metaInfo.customHashCodeMethod().get().getName());
        return block;
    }

    private CodeBlock createHashCalculationBlock() {
        final StringBuilder statement = new StringBuilder("return Objects.hash(");
        // Collection<String> fields = this.codeElements.fieldNames();
        // for (String field : fields) {
        // final String fieldAsObject = CommonCodeSnippets.
        // statement.append()
        // }
        return null;
    }

}
