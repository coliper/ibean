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

import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.MethodSpec;

/**
 * @author alex@coliper.org
 *
 */
class BeanMethodsCodeGenerator {

    private final BeanCodeElements codeElements;
    private final IBeanTypeMetaInfo<?> metaInfo;
    private final BeanStyleSpecificCodeGenerator beanStyleHandler;
    private final ExtensionCodeGenerator[] extensionCodeGenerators;

    /**
     * @param codeElements
     * @param metaInfo
     * @param extensionCodeGenerators
     */
    BeanMethodsCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo,
            BeanStyleSpecificCodeGenerator beanStyleHandler,
            ExtensionCodeGenerator[] extensionCodeGenerators) {
        this.codeElements = codeElements;
        this.metaInfo = metaInfo;
        this.beanStyleHandler = beanStyleHandler;
        this.extensionCodeGenerators = extensionCodeGenerators;
    }

    List<MethodSpec> createMethods() {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.add(new HashCodeMethodCodeGenerator(codeElements, metaInfo).createMethod());
        methodSpecs.add(new EqualsMethodCodeGenerator(codeElements, metaInfo).createMethod());
        methodSpecs.add(new ToStringMethodCodeGenerator(codeElements).createMethod());
        methodSpecs.addAll(new GetterMethodsCodeGenerator(codeElements, metaInfo, beanStyleHandler,
                extensionCodeGenerators).createMethods());
        methodSpecs.addAll(new SetterMethodsCodeGenerator(codeElements, metaInfo, beanStyleHandler,
                extensionCodeGenerators).createMethods());
        for (ExtensionCodeGenerator extensionCodeGenerator : this.extensionCodeGenerators) {
            methodSpecs.addAll(extensionCodeGenerator.createInterfaceMethodImplementations(metaInfo,
                    codeElements));
        }
        return methodSpecs;
    }
}
