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
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.ArrayUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.codegen.JavaPoetUtil;
import org.coliper.ibean.extension.ModificationAware;
import org.coliper.ibean.extension.ModificationAwareExt;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.util.ReflectionUtil;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link NullSafe}.
 * 
 * @author alex@coliper.org
 */
public class ModificationAwareExtensionCodeGenerator implements ExtensionCodeGenerator {

    private static final Method IS_MODIFIED_METHOD =
            ReflectionUtil.lookupInterfaceMethod(ModificationAware.class, s -> s.isModified());
    private static final Method RESET_MODIFIED_METHOD =
            ReflectionUtil.lookupInterfaceMethod(ModificationAware.class, s -> s.resetModified());
    private static final Method ALL_FIELDS_MODIFIED_METHOD = ReflectionUtil
            .lookupInterfaceMethod(ModificationAwareExt.class, s -> s.allFieldsModified());
    private static final Method GET_MODIFIED_FIELD_NAMES_METHOD = ReflectionUtil
            .lookupInterfaceMethod(ModificationAwareExt.class, s -> s.getModifiedFieldNames());

    private static final String PROPERTY_ENUM_NAME = "PropEnum$";
    private static final String PROPERT_ENUM_VALUE_PREFIX = "P_";
    private static final String DIRTY_FLAGS_FIELD_NAME = "dirtyFlags";

    @Override
    public List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        List<MethodSpec> methList = new ArrayList<>();
        methList.add(this.createIsModifiedImplementation(beanMeta, beanCodeElements));
        methList.add(this.createResetModifieldImplementation(beanMeta, beanCodeElements));
        if (ModificationAwareExt.class.isAssignableFrom(beanMeta.beanType())) {
            methList.add(this.createAllFieldsModifiedImplementation(beanMeta, beanCodeElements));
            methList.add(this.createGetModifiedFieldsImplementation(beanMeta, beanCodeElements));
        }
        return methList;
    }

    private MethodSpec createIsModifiedImplementation(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        //@formatter:off
        return JavaPoetUtil.methodSpecBuilderFromOverride(IS_MODIFIED_METHOD)
                .addStatement("return $T.contains(this.$N, true)", 
                        ArrayUtils.class, DIRTY_FLAGS_FIELD_NAME)
                .build();
        //@formatter:on
    }

    private MethodSpec createResetModifieldImplementation(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        //@formatter:off
        return JavaPoetUtil.methodSpecBuilderFromOverride(RESET_MODIFIED_METHOD)
                .addStatement("$T.fill($N, false)", Arrays.class, DIRTY_FLAGS_FIELD_NAME)
                .build();
        //@formatter:on
    }

    private MethodSpec createAllFieldsModifiedImplementation(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        //@formatter:off
        return JavaPoetUtil.methodSpecBuilderFromOverride(ALL_FIELDS_MODIFIED_METHOD)
                .addStatement("return !$T.contains($N, false)", 
                        ArrayUtils.class, DIRTY_FLAGS_FIELD_NAME)
                .build();
        //@formatter:on
    }

    private MethodSpec createGetModifiedFieldsImplementation(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        //@formatter:off
        return JavaPoetUtil.methodSpecBuilderFromOverride(GET_MODIFIED_FIELD_NAMES_METHOD)
                .addStatement("final $T<String> props = new $T<>(this.$N.length)",
                        List.class, ArrayList.class, DIRTY_FLAGS_FIELD_NAME)
                .beginControlFlow("for (int i = 0; i < this.$N.length; i++)", 
                        DIRTY_FLAGS_FIELD_NAME)
                .beginControlFlow("if (this.$N[i])", DIRTY_FLAGS_FIELD_NAME)
                .addStatement("props.add($N.values()[i].name().substring($L))",
                        PROPERTY_ENUM_NAME, 
                        String.valueOf(PROPERT_ENUM_VALUE_PREFIX.length()))
                .endControlFlow()
                .endControlFlow()
                .addStatement("return props.toArray(String[]::new)")
                .build();
        //@formatter:on
    }

    @Override
    public CodeBlock createSetterCodeBlock(IBeanFieldMetaInfo fieldMeta) {
        //@formatter:off
        return CodeBlock.builder()
                .addStatement("this.$N[$L.$L.ordinal()] = true", 
                        DIRTY_FLAGS_FIELD_NAME,
                        PROPERTY_ENUM_NAME, 
                        PROPERT_ENUM_VALUE_PREFIX + fieldMeta.fieldName())
                .build();
        //@formatter:on
    }

    @Override
    public List<FieldSpec> createExtensionSpecificFields(IBeanTypeMetaInfo<?> beanMeta) {
        return List.of(FieldSpec
                .builder(boolean[].class, DIRTY_FLAGS_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new boolean[$L]", String.valueOf(beanMeta.fieldMetaInfos().size()))
                .build());
    }

    @Override
    public List<TypeSpec> createNestedTypes(IBeanTypeMetaInfo<?> beanMeta) {
        final TypeSpec.Builder enumSpecBuilder = TypeSpec.enumBuilder(PROPERTY_ENUM_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC);
        for (IBeanFieldMetaInfo propMeta : beanMeta.fieldMetaInfos()) {
            enumSpecBuilder.addEnumConstant(PROPERT_ENUM_VALUE_PREFIX + propMeta.fieldName());
        }
        return List.of(enumSpecBuilder.build());
    }

}
