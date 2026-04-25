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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.codegen.JavaPoetUtil;
import org.coliper.ibean.extension.CloneableBean;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.util.ReflectionUtil;

import com.squareup.javapoet.MethodSpec;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link CloneableBean}.
 * 
 * @author alex@coliper.org
 */
public class CloneableBeanExtensionCodeGenerator implements ExtensionCodeGenerator {

    private final static Method CLONE_METHOD =
            ReflectionUtil.lookupInterfaceMethod(CloneableBean.class, s -> s.clone());
    private final static Method DEEP_CLONE_METHOD =
            ReflectionUtil.lookupInterfaceMethod(CloneableBean.class, s -> s.deepClone());

    @Override
    public List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        List<MethodSpec> ret = new ArrayList<>();
        ret.add(this.createCloneMethod(beanMeta, beanCodeElements));
        ret.add(this.createDeepCloneMethod(beanMeta, beanCodeElements));
        return ret;
    }

    private MethodSpec createCloneMethod(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        MethodSpec.Builder builder = JavaPoetUtil.methodSpecBuilderFromOverride(CLONE_METHOD);
        builder.returns(beanMeta.beanType());
        //@formatter:off
        builder.beginControlFlow("try")
                .addStatement("return ($T)super.clone()", beanMeta.beanType())
                .nextControlFlow("catch ($T ex)", CloneNotSupportedException.class)
                .addStatement("throw new RuntimeException(\"unexpected\", ex)")
                .endControlFlow();
        //@formatter:on
        return builder.build();
    }

    private MethodSpec createDeepCloneMethod(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        final MethodSpec.Builder builder =
                JavaPoetUtil.methodSpecBuilderFromOverride(DEEP_CLONE_METHOD);
        builder.returns(beanMeta.beanType());
        //@formatter:off
        builder.addStatement("final $L clone", beanCodeElements.beanClassName())
                .beginControlFlow("try")
                .addStatement("clone = ($L)super.clone()", beanCodeElements.beanClassName())
                .nextControlFlow("catch ($T ex)", CloneNotSupportedException.class)
                .addStatement("throw new RuntimeException(\"unexpected\", ex)")
                .endControlFlow()
                .addStatement("Object tmpVal");
        //@formatter:on
        for (IBeanFieldMetaInfo fieldMeta : beanMeta.fieldMetaInfos()) {
            final String internalFieldName =
                    beanCodeElements.fieldNameFromPropertyName(fieldMeta.fieldName());
            addDeepCloneCodeForField(builder, internalFieldName, fieldMeta.fieldType());
        }
        builder.addStatement("return clone");
        return builder.build();
    }

    private void addDeepCloneCodeForField(final MethodSpec.Builder builder,
            final String internalFieldName, Class<?> fieldType) {
        if (fieldType.isPrimitive()) {
            return;
        }
        //@formatter:off
        builder.addStatement("tmpVal = this.$L", internalFieldName)
                .beginControlFlow("if (tmpVal != null && tmpVal instanceof $T)", CloneableBean.class)
                .addStatement("tmpVal = (($T)tmpVal).deepClone()", CloneableBean.class) 
                .addStatement("clone.$L = ($T)tmpVal", internalFieldName, fieldType)
                .nextControlFlow("else")
                .addStatement("clone.$L = ($T)$T.cloneIfPossible(this.$L)",
                        internalFieldName, fieldType, ObjectUtils.class, internalFieldName)
                .endControlFlow();
        //@formatter:on
    }

}
