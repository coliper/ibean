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
class BeanMethodCodeGenerator {

    private final BeanCodeElements codeElements;
    private final IBeanTypeMetaInfo<?> metaInfo;

    /**
     * @param codeElements
     * @param metaInfo
     */
    BeanMethodCodeGenerator(BeanCodeElements codeElements, IBeanTypeMetaInfo<?> metaInfo) {
        this.codeElements = codeElements;
        this.metaInfo = metaInfo;
    }

    List<MethodSpec> createMethods() {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.add(new HashCodeMethodCodeGenerator(codeElements, metaInfo).createMethod());
        return methodSpecs;
    }
}
