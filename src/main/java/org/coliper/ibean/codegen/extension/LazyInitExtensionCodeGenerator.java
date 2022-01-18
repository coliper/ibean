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

package org.coliper.ibean.codegen.extension;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.CommonCodeSnippets;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.extension.LazyInitChild;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.proxy.ExtensionHandler;

import com.squareup.javapoet.CodeBlock;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link NullSafe}.
 * 
 * @author alex@coliper.org
 */
public class LazyInitExtensionCodeGenerator implements ExtensionCodeGenerator {

    @Override
    public CodeBlock createGetterCodeBlock(IBeanFieldMetaInfo fieldMeta,
            BeanCodeElements beanCodeElements) {
        if (!LazyInitChild.class.isAssignableFrom(fieldMeta.fieldType())) {
            return EMPTY_BLOCK;
        }
        final String fieldName = beanCodeElements.fieldNameFromPropertyName(fieldMeta.fieldName());
        //@formatter:off
        return CodeBlock.builder()
                .beginControlFlow("if ($N == null)", CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME)
                .addStatement("$N = ($T)this.$N.create($T.class)",
                        CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME,
                        fieldMeta.fieldType(),
                        CommonCodeSnippets.FACTORY_FIELD_NAME,
                        fieldMeta.fieldType())
                .addStatement("this.$N = $N", fieldName, CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME)
                .endControlFlow()
                .build();
        //@formatter:on
    }

}