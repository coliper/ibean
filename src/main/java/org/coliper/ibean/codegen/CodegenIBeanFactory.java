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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.codehaus.janino.JavaSourceClassLoader;
import org.codehaus.janino.util.ResourceFinderClassLoader;
import org.codehaus.janino.util.resource.ResourceFinder;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.InvalidIBeanTypeException;

/**
 * @author alex@coliper.org
 *
 */
public class CodegenIBeanFactory implements IBeanFactory {

    private static final String JAVA_FILE_NAME_EXTENSION = ".java";

    public static final String DEFAULT_PACKAGE_NAME =
            CodegenIBeanFactory.class.getPackage().getName() + ".generated";

    /*
     * Converts a full class name with packages to a class name used for the
     * generated implementation. This is done by converting the package name to
     * a camel case string plus adding the class name and "Impl" at the end. For
     * example 'com.some.package.FancyType' would be converted into
     * 'ComSomePackageFancyTypeImpl'.
     */
    private static final UnaryOperator<String> DEFAULT_TYPE_NAME_BUILDER =
            new UnaryOperator<String>() {
                @Override
                public String apply(String fullClassNameWithPackage) {
                    StringBuilder generatedTypeName = new StringBuilder(fullClassNameWithPackage);
                    boolean convertNextToUpperCase = true;
                    for (int i = 0; i < generatedTypeName.length(); i++) {
                        char currentChar = generatedTypeName.charAt(i);
                        if ('.' == currentChar) {
                            generatedTypeName.deleteCharAt(i);
                            i--;
                            convertNextToUpperCase = true;
                            continue;
                        }
                        if (convertNextToUpperCase) {
                            generatedTypeName.setCharAt(i, Character.toUpperCase(currentChar));
                            convertNextToUpperCase = false;
                        }
                    }
                    generatedTypeName.append("Impl");

                    return generatedTypeName.toString();
                }
            };

    private static final UnaryOperator<String> DEFAULT_FIELD_NAME_BUILDER =
            new UnaryOperator<String>() {

                @Override
                public String apply(String fieldName) {
                    return "_" + fieldName;
                }
            };

    private final ClassLoader beanClassLoader;
    private final Map<Class<?>, Class<?>> implementationTypeMap =
            Collections.synchronizedMap(new HashMap<>());

    /**
     * @param beanClassLoader
     */
    CodegenIBeanFactory(JavaSourceClassLoader beanClassLoader) {
        ResourceFinder resourceFinder = new Resou;
        String characterEncoding = null;
        this.beanClassLoader = new ResourceFinderClassLoader(resourceFinder, this.getClass().getClassLoader());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.IBeanFactory#create(java.lang.Class)
     */
    @Override
    public <T> T create(Class<T> beanType) throws InvalidIBeanTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    public static interface SimpleBean {
        int getInt();

        void setInt(int i);
    }

    public static void main(String[] args) throws IOException {
        IBeanTypeMetaInfo<?> beanMeta = new IBeanMetaInfoParser().parse(SimpleBean.class,
                BeanStyle.CLASSIC, Collections.emptyList());
        File sourceFolder = Files.createTempDirectory(DEFAULT_PACKAGE_NAME).toFile();
        BeanCodeGenerator generator = new BeanCodeGenerator(sourceFolder, DEFAULT_PACKAGE_NAME,
                DEFAULT_TYPE_NAME_BUILDER, DEFAULT_FIELD_NAME_BUILDER);
        generator.createBeanSourceFile(beanMeta);

    }
}
