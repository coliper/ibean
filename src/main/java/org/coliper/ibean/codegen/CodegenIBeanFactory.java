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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.codehaus.janino.JavaSourceClassLoader;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.InvalidIBeanTypeException;

import com.google.common.base.Charsets;

/**
 * @author alex@coliper.org
 *
 */
public class CodegenIBeanFactory implements IBeanFactory {

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
    private final SourceCodeStore sourceCodeStore;
    private final String genCodePackageName = "codegen";
    private final Function<Class<?>, String> implTypeNameFunc = DEFAULT_TYPE_NAME_BUILDER;
    private final Charset streamEncoding = Charsets.UTF_8;
    private final BeanStyle beanStyle = BeanStyle.CLASSIC;
    private final ToStringStyle toStringStyle = ToStringStyle.SHORT_PREFIX_STYLE;
    private final BeanStyleSpecificCodeGenerator beanStyleHandler =
            BeanStyleSpecificCodeGenerator.CLASSIC;
    private final Map<Class<?>, ExtensionCodeGenerator> extensionCodeGeneratorMap;

    /**
     * @param beanClassLoader
     */
    CodegenIBeanFactory() {
        this.sourceCodeStore = new SourceCodeStore(this.streamEncoding);
        this.beanClassLoader = new JavaSourceClassLoader(this.getClass().getClassLoader(),
                this.sourceCodeStore, this.streamEncoding.name());
        this.extensionCodeGeneratorMap = Collections.emptyMap();
    }

    public static CodegenIBeanFactory builder() {
        return new CodegenIBeanFactory();
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

        T bean;
        try {
            bean = beanType.cast(implementationType.newInstance());
            FieldUtils.writeDeclaredField(bean, CommonCodeSnippets.FACTORY_FIELD_NAME, this);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidIBeanTypeException(beanType, e.toString());
        }

        return bean;
    }

    private String createImplementationClassName(Class<?> beanType) {
        return this.implTypeNameFunc.apply(beanType);
    }

    private Class<?> createIBeanImplementation(Class<?> beanInterfaceType) {
        final String implementationClassName =
                this.createImplementationClassName(beanInterfaceType);
        final String fullImplementationClassName =
                this.genCodePackageName + "." + implementationClassName;
        final String beanSourceCode =
                this.createBeanSource(beanInterfaceType, implementationClassName);
        System.out.println(beanSourceCode);
        this.sourceCodeStore.addCode(fullImplementationClassName, beanSourceCode);
        try {
            return this.beanClassLoader.loadClass(fullImplementationClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("failed creating implementation class '"
                    + fullImplementationClassName + "' for bean type " + beanInterfaceType, e);
        } finally {
            this.sourceCodeStore.removeCode(fullImplementationClassName);
        }
    }

    /**
     * @param beanInterfaceType
     * @param implementationClassName
     * @return
     */
    private String createBeanSource(Class<?> beanInterfaceType, String implementationTypeName) {
        List<Class<?>> ignorableSuperInterfaces = Collections.emptyList();
        IBeanTypeMetaInfo<?> meta = new IBeanMetaInfoParser().parse(beanInterfaceType, beanStyle,
                ignorableSuperInterfaces);
        return new BeanCodeGenerator(this.genCodePackageName, implementationTypeName, meta,
                this.beanStyleHandler, this.extensionCodeGeneratorsForType(beanInterfaceType))
                        .generateSourceCode();
    }

    private ExtensionCodeGenerator[] extensionCodeGeneratorsForType(Class<?> beanInterfaceType) {
        return this.extensionCodeGeneratorMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(beanInterfaceType))
                .map(entry -> entry.getValue()).toArray(ExtensionCodeGenerator[]::new);
    }

    public ToStringStyle toStringStyle() {
        return this.toStringStyle;
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
        SimpleBean bean2 = factory.create(SimpleBean.class);
        bean.setInt(9238748);
        System.out.println("int: " + bean.getInt());
        System.out.println("hash: " + bean.hashCode());
        System.out.println("toString: " + bean.toString());
        System.out.println("equals: " + bean.equals(bean2));
        bean2.setInt(9238748);
        System.out.println("equals: " + bean.equals(bean2));
    }
}
