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

import javax.lang.model.element.Modifier;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.FieldSpec;

/**
 * @author alex@coliper.org
 *
 */
class BeanFieldsCodeGenerator {

    private final BeanCodeElements codeElements;
    private final IBeanTypeMetaInfo<?> metaInfo;

    /**
     * @param codeElements
     * @param metaInfo
     */
    BeanFieldsCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo) {
        this.codeElements = codeElements;
        this.metaInfo = metaInfo;
    }

    List<FieldSpec> createFields() {
        final List<FieldSpec> fieldSpecs = new ArrayList<>();

        // private CodegenIBeanFactory factory
        fieldSpecs.add(FieldSpec.builder(CodegenIBeanFactory.class,
                CommonCodeSnippets.FACTORY_FIELD_NAME, Modifier.PUBLIC).build());

        List<IBeanFieldMetaInfo> properties = this.metaInfo.fieldMetaInfos();
        for (IBeanFieldMetaInfo fieldMeta : properties) {
            fieldSpecs.add(this.createFieldSpec(fieldMeta));
        }
        return fieldSpecs;
    }

    private FieldSpec createFieldSpec(IBeanFieldMetaInfo fieldMeta) {
        String memberName = this.codeElements.fieldNameFromPropertyName(fieldMeta.fieldName());
        return FieldSpec.builder(fieldMeta.fieldType(), memberName, Modifier.PRIVATE).build();
    }
}
