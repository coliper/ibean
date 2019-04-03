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

import java.util.ArrayList;
import java.util.List;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class SetterMethodsCodeGenerator {

    private final BeanCodeElements codeElements;
    private final IBeanTypeMetaInfo<?> metaInfo;
    private final BeanStyleSpecificCodeGenerator beanStyleHandler;
    private final ExtensionCodeGenerator[] extensionCodeGenerators;

    /**
     * @param codeElements
     * @param metaInfo
     * @param beanStyleHandler
     * @param extensionCodeGenerators
     */
    SetterMethodsCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo,
            BeanStyleSpecificCodeGenerator beanStyleHandler,
            ExtensionCodeGenerator[] extensionCodeGenerators) {
        this.codeElements = codeElements;
        this.metaInfo = metaInfo;
        this.beanStyleHandler = beanStyleHandler;
        this.extensionCodeGenerators = extensionCodeGenerators;
    }

    List<MethodSpec> createMethods() {
        final List<MethodSpec> methods = new ArrayList<>();
        List<IBeanFieldMetaInfo> fields = metaInfo.fieldMetaInfos();
        for (IBeanFieldMetaInfo fieldMeta : fields) {
            createSetterForField(methods, fieldMeta);
        }
        return methods;
    }

    private void createSetterForField(final List<MethodSpec> methods,
            IBeanFieldMetaInfo fieldMeta) {
        final String fieldName = codeElements.fieldNameFromPropertyName(fieldMeta.fieldName());
        Builder methodBuilder = JavaPoetUtil.methodSpecBuilderFromOverride(fieldMeta.setterMethod(),
                CommonCodeSnippets.SETTER_ARGUMENT_NAME);
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        codeBlockBuilder.addStatement("$T $L = $L", fieldMeta.fieldType(),
                CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME,
                CommonCodeSnippets.SETTER_ARGUMENT_NAME);
        codeBlockBuilder.addStatement("$L = $L", fieldName,
                CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME);
        for (ExtensionCodeGenerator extensionCodeGenerator : this.extensionCodeGenerators) {
            codeBlockBuilder.add(extensionCodeGenerator.createGetterCodeBlock(fieldMeta));
        }
        codeBlockBuilder.add(this.beanStyleHandler.createSetterEndBlock(fieldMeta));
        methodBuilder.addCode(codeBlockBuilder.build());
        methods.add(methodBuilder.build());
    }

}
