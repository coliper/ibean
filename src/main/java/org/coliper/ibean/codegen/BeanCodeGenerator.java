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

import static java.util.Objects.requireNonNull;

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

    private static final UnaryOperator<String> DEFAULT_FIELD_NAME_BUILDER =
            new UnaryOperator<String>() {

                @Override
                public String apply(String fieldName) {
                    return "_" + fieldName;
                }
            };

    private final String packageName;
    private final UnaryOperator<String> fieldNameBuilder = DEFAULT_FIELD_NAME_BUILDER;

    /**
     * @param sourceFolder
     * @param classFolder
     * @param packageName
     */
    BeanCodeGenerator(String packageName) {
        requireNonNull(packageName, "packageName");

        this.packageName = packageName;
    }

    private static final String NL = System.lineSeparator();
//@formatter:off    
    private static final String CODE = 
            "package $package;" + NL + 
            "public class $className implements $beanType {" + NL + 
            "  private int i;" + NL + 
            "  public int getInt() { return i; }" + NL + 
            "  public void setInt(int x) { this.i = x; }" + NL + 
            "}" + NL + 
//            "" + NL + 
            "" + NL; 
  //@formatter:on

    /**
     * @param sourceFile
     * @param beanMeta
     */
    String generateSourceCode(final String implementationTypeName,
            final IBeanTypeMetaInfo<?> beanMeta) {
        // TODO Auto-generated method stub
        // return CODE.replace("$package", this.packageName)
        // .replace("$className", implementationTypeName)
        // .replace("$beanType", beanMeta.beanType().getName());
        final BeanCodeElements codeElements =
                this.createBeanCodeElements(beanMeta, implementationTypeName);
        final Builder typeBuilder = TypeSpec.classBuilder(implementationTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        this.addFields(typeBuilder, codeElements, beanMeta);
        this.addMethods(typeBuilder, codeElements, beanMeta);

        JavaFile javaFile = JavaFile.builder(this.packageName, typeBuilder.build()).build();

        StringWriter codeBuffer = new StringWriter();
        try {
            javaFile.writeTo(codeBuffer);
        } catch (IOException e) {
            throw new IllegalStateException("unexpected IOException", e);
        }

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
        BeanFieldsCodeGenerator fieldGen = new BeanFieldsCodeGenerator(codeElements, beanMeta);
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
                new BeanMethodsCodeGenerator(codeElements, beanMeta).createMethods();
        for (MethodSpec methodSpec : methodSpecs) {
            typeBuilder.addMethod(methodSpec);
        }
    }

}
