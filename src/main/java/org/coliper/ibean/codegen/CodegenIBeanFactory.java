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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.codehaus.janino.JavaSourceClassLoader;
import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceFinder;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.InvalidIBeanTypeException;

import com.google.common.base.Preconditions;

/**
 * @author alex@coliper.org
 *
 */
public class CodegenIBeanFactory implements IBeanFactory {

    private static final String JAVA_FILE_NAME_EXTENSION = ".java";

    public static final String DEFAULT_PACKAGE_NAME =
            CodegenIBeanFactory.class.getPackage().getName() + ".generated";

    /*
     * Converts a given bean type to a class name used for the generated
     * implementation. This is done by converting the package name to a camel
     * case string plus adding the class name and "Impl" at the end. For example
     * 'com.some.package.FancyType' would be converted into
     * 'ComSomePackageFancyTypeImpl'.
     */
    private static final Function<Class<?>, String> DEFAULT_TYPE_NAME_BUILDER =
            new Function<Class<?>, String>() {
                @Override
                public String apply(Class<?> interfaceType) {
                    StringBuilder generatedTypeName = new StringBuilder(interfaceType.getName());
                    boolean convertNextToUpperCase = true;
                    for (int i = 0; i < generatedTypeName.length(); i++) {
                        char currentChar = generatedTypeName.charAt(i);
                        if ('.' == currentChar) {
                            generatedTypeName.deleteCharAt(i);
                            i--;
                            convertNextToUpperCase = true;
                            continue;
                        }
                        // replace $ with _
                        if ('$' == currentChar) {
                            generatedTypeName.setCharAt(i, '_');
                        }
                        // replace _ with __
                        if ('_' == currentChar) {
                            generatedTypeName.insert(i, '_');
                            i++;
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

    private final ClassLoader beanClassLoader;
    private final Map<Class<?>, Class<?>> implementationTypeMap = new ConcurrentHashMap<>();
    private final Map<String, Resource> tempResourceMap = new ConcurrentHashMap<>();
    private final String genCodePackageName = "codegen";
    private final Function<Class<?>, String> implTypeNameFunc = DEFAULT_TYPE_NAME_BUILDER;
    private final String streamEncoding = "UTF-8";

    /**
     * @param beanClassLoader
     */
    CodegenIBeanFactory() {
        ResourceFinder resourceFinder = this.createResourceFinder();
        this.beanClassLoader = new JavaSourceClassLoader(this.getClass().getClassLoader(),
                resourceFinder, this.streamEncoding);
    }

    private ResourceFinder createResourceFinder() {
        return new ResourceFinder() {
            @Override
            public Resource findResource(String javaFilePath) {
                Objects.requireNonNull(javaFilePath, "javaFilePath");
                Preconditions.checkArgument(javaFilePath.endsWith(JAVA_FILE_NAME_EXTENSION),
                        "illegal Java file path: %s", javaFilePath);
                final int startIndex = javaFilePath.lastIndexOf('/') + 1;
                final int endIndex = javaFilePath.length() - JAVA_FILE_NAME_EXTENSION.length();
                final String resourceName = javaFilePath.substring(startIndex, endIndex);
                return CodegenIBeanFactory.this.tempResourceMap.get(resourceName);
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.IBeanFactory#create(java.lang.Class)
     */
    @Override
    public <T> T create(Class<T> beanType) throws InvalidIBeanTypeException {
        Objects.requireNonNull(beanType, "beanType");
        Class<?> implementationType = this.implementationTypeMap.computeIfAbsent(beanType,
                this::createIBeanImplementation);
        try {
            return beanType.cast(implementationType.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidIBeanTypeException(beanType, e.toString());
        }
    }

    private String createImplementationClassName(Class<?> beanType) {
        return this.implTypeNameFunc.apply(beanType);
    }

    private Class<?> createIBeanImplementation(Class<?> beanInterfaceType) {
        final String implementationClassName =
                this.createImplementationClassName(beanInterfaceType);
        final String fullImplementationClassName =
                this.genCodePackageName + "." + implementationClassName;
        final String sourceFileName = implementationClassName + JAVA_FILE_NAME_EXTENSION;
        final String beanSourceCode =
                this.createBeanSource(beanInterfaceType, implementationClassName);
        final Resource sourceCodeResource =
                this.createStringResource(sourceFileName, beanSourceCode);
        this.tempResourceMap.put(implementationClassName, sourceCodeResource);
        try {
            return this.beanClassLoader.loadClass(fullImplementationClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("failed creating implementation class '"
                    + fullImplementationClassName + "' for bean type " + beanInterfaceType, e);
        } finally {
            this.tempResourceMap.remove(implementationClassName);
        }
    }

    /**
     * @param beanInterfaceType
     * @param implementationClassName
     * @return
     */
    private String createBeanSource(Class<?> beanInterfaceType, String implementationTypeName) {
        BeanStyle beanStyle = BeanStyle.CLASSIC;
        List<Class<?>> ignorableSuperInterfaces = Collections.emptyList();
        IBeanTypeMetaInfo<?> meta = new IBeanMetaInfoParser().parse(beanInterfaceType, beanStyle,
                ignorableSuperInterfaces);
        return new BeanCodeGenerator(this.genCodePackageName)
                .generateSourceCode(implementationTypeName, meta);
    }

    private Resource createStringResource(final String fileName, final String code) {
        return new Resource() {
            private final long UNDETERMINED_MODIFICATION_TIME = 0;

            @Override
            public InputStream open() throws IOException {
                return new ByteArrayInputStream(
                        code.getBytes(CodegenIBeanFactory.this.streamEncoding));
            }

            @Override
            public long lastModified() {
                return UNDETERMINED_MODIFICATION_TIME;
            }

            @Override
            public String getFileName() {
                return fileName;
            }
        };
    }

    public static interface SimpleBean {
        int getInt();

        void setInt(int i);
    }

    public static void main(String[] args) throws IOException {
        // IBeanTypeMetaInfo<?> beanMeta = new
        // IBeanMetaInfoParser().parse(SimpleBean.class,
        // BeanStyle.CLASSIC, Collections.emptyList());
        // File sourceFolder =
        // Files.createTempDirectory(DEFAULT_PACKAGE_NAME).toFile();
        // BeanCodeGenerator generator = new BeanCodeGenerator(sourceFolder,
        // DEFAULT_PACKAGE_NAME,
        // DEFAULT_TYPE_NAME_BUILDER, DEFAULT_FIELD_NAME_BUILDER);
        // generator.createBeanSourceFile(beanMeta);

        CodegenIBeanFactory factory = new CodegenIBeanFactory();
        SimpleBean bean = factory.create(SimpleBean.class);
        bean.setInt(9238748);
        System.out.println("int: " + bean.getInt());
    }
}
