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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import javax.lang.model.element.Modifier;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

/**
 * @author alex@coliper.org
 *
 */
class BeanCodeGenerator {

    private final File sourceFolder;
    private final String packageName;
    private final UnaryOperator<String> typeNameBuilder;
    private final UnaryOperator<String> fieldNameBuilder;

    /**
     * @param sourceFolder
     * @param classFolder
     * @param packageName
     */
    BeanCodeGenerator(File sourceFolder, String packageName, UnaryOperator<String> typeNameBuilder,
            UnaryOperator<String> fieldNameBuilder) {
        requireNonNull(sourceFolder, "sourceFolder");
        requireNonNull(packageName, "packageName");
        requireNonNull(typeNameBuilder, "typeNameBuilder");
        requireNonNull(fieldNameBuilder, "fieldNameBuilder");
        checkArgument(sourceFolder.isDirectory(), "folder %s is not a directory",
                sourceFolder.getName());

        this.sourceFolder = sourceFolder;
        this.packageName = packageName;
        this.typeNameBuilder = typeNameBuilder;
        this.fieldNameBuilder = fieldNameBuilder;
    }

    String createBeanSourceFile(IBeanTypeMetaInfo<?> beanMeta) throws IOException {
        requireNonNull(beanMeta, "beanMeta");
        final String beanTypeName = typeNameBuilder.apply(beanMeta.beanType().getName());

        return this.generateSourceCode(beanTypeName, beanMeta);
    }

    /**
     * @param sourceFile
     * @param beanMeta
     */
    String generateSourceCode(final String beanTypeName, final IBeanTypeMetaInfo<?> beanMeta)
            throws IOException {
        final BeanCodeElements codeElements = this.createBeanCodeElements(beanMeta, beanTypeName);
        final Builder typeBuilder =
                TypeSpec.classBuilder(beanTypeName).addModifiers(Modifier.PUBLIC, Modifier.FINAL).;
        this.addFields(typeBuilder, codeElements, beanMeta);
        this.addMethods(typeBuilder, codeElements, beanMeta);

        JavaFile javaFile = JavaFile.builder(this.packageName, typeBuilder.build()).build();

        StringWriter codeBuffer = new StringWriter();
        javaFile.writeTo(codeBuffer);

        return codeBuffer.toString();
    }

    /**
     * @return
     */
    private BeanCodeElements createBeanCodeElements(final IBeanTypeMetaInfo<?> beanMeta,
            String beanTypeName) {
        Map<String, String> fieldNameMap = new HashMap<>();
        List<IBeanFieldMetaInfo> fieldMetas = beanMeta.fieldMetaInfos();
        for (IBeanFieldMetaInfo fieldMeta : fieldMetas) {
            final String fieldName = fieldMeta.fieldName();
            fieldNameMap.put(fieldName, this.fieldNameBuilder.apply(fieldName));
        }

        return new BeanCodeElements(beanTypeName, this.packageName, fieldNameMap);
    }

    /**
     * @param typeBuilder
     */
    private void addFields(Builder typeBuilder, BeanCodeElements codeElements,
            final IBeanTypeMetaInfo<?> beanMeta) {
        BeanFieldCodeGenerator fieldGen = new BeanFieldCodeGenerator(codeElements, beanMeta);
        List<FieldSpec> fieldSpecs = fieldGen.createFields();
        for (FieldSpec fieldSpec : fieldSpecs) {
            typeBuilder.addField(fieldSpec);
        }
    }

    /**
     * @param typeBuilder
     * @param codeElements
     * @param beanMeta
     */
    private void addMethods(Builder typeBuilder, BeanCodeElements codeElements,
            IBeanTypeMetaInfo<?> beanMeta) {
        final List<MethodSpec> methodSpecs =
                new BeanMethodCodeGenerator(codeElements, beanMeta).createMethods();
        for (MethodSpec methodSpec : methodSpecs) {
            typeBuilder.addMethod(methodSpec);
        }
    }

}
