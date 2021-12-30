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
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.codehaus.commons.compiler.util.resource.DirectoryResourceCreator;
import org.codehaus.commons.compiler.util.resource.DirectoryResourceFinder;
import org.codehaus.commons.compiler.util.resource.MapResourceCreator;
import org.codehaus.commons.compiler.util.resource.MapResourceFinder;
import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.ResourceCreator;
import org.codehaus.commons.compiler.util.resource.ResourceFinder;
import org.codehaus.janino.JavaSourceClassLoader;
import org.codehaus.janino.util.ClassFile;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.InvalidIBeanTypeException;
import org.coliper.ibean.codegen.extension.CloneableBeanExtensionCodeGenerator;
import org.coliper.ibean.codegen.extension.CompletableExtensionCodeGenerator;
import org.coliper.ibean.codegen.extension.FreezableExtensionCodeGenerator;
import org.coliper.ibean.codegen.extension.GsonSupportExtensionCodeGenerator;
import org.coliper.ibean.codegen.extension.Jackson2SupportExtensionCodeGenerator;
import org.coliper.ibean.codegen.extension.ModificationAwareExtensionCodeGenerator;
import org.coliper.ibean.codegen.extension.NullSafeExtensionCodeGenerator;
import org.coliper.ibean.extension.CloneableBean;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.extension.Jackson2Support;
import org.coliper.ibean.extension.ModificationAware;
import org.coliper.ibean.extension.ModificationAwareExt;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.extension.TempFreezable;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.ProxyIBeanFactory;

import com.google.common.base.Charsets;

/**
 * @author alex@coliper.org
 *
 */
public class CodegenIBeanFactory implements IBeanFactory {

    private static final Charset INTERNAL_CODE_CHARSET = Charsets.UTF_8;

    public static final String DEFAULT_PACKAGE_NAME =
            CodegenIBeanFactory.class.getPackage().getName() + ".generated";

    //@formatter:off     
    private static Map<Class<?>, ExtensionCodeGenerator> DEFAULT_EXT_GENERATOR_MAP = Map.of(
            NullSafe.class, new NullSafeExtensionCodeGenerator(),
            CloneableBean.class, new CloneableBeanExtensionCodeGenerator(),
            Completable.class, new CompletableExtensionCodeGenerator(),
            Freezable.class, new FreezableExtensionCodeGenerator(),
            TempFreezable.class, new FreezableExtensionCodeGenerator(),
            GsonSupport.class, new GsonSupportExtensionCodeGenerator(),
            Jackson2Support.class, new Jackson2SupportExtensionCodeGenerator(),
            ModificationAware.class, new ModificationAwareExtensionCodeGenerator(),
            ModificationAwareExt.class, new ModificationAwareExtensionCodeGenerator());
    //@formatter:on     

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

    private static Optional<Class<?>> findSubOrSuperTypeInCollection(Class<?> type,
            Collection<Class<?>> typeCollection) {
        for (Class<?> typeElement : typeCollection) {
            if (typeElement.isAssignableFrom(type) || type.isAssignableFrom(typeElement)) {
                return Optional.of(typeElement);
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a {@link Builder} for setting up a new {@link ProxyIBeanFactory}.
     * See class description above for an usage example.
     * 
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    //@formatter:off     
    /**
     * Used for creating new instances of {@link ProxyIBeanFactory}.
     * {@code Builder}s are not created by constructor, they are exclusively
     * created by calling {@link ProxyIBeanFactory#builder()}.
     * <pre>
     * <code>
     * ProxyIBeanFactory factory = ProxyIBeanFactory.builder()
     *         .withBeanStyle(BeanStyle.MODERN_WITH_OPTIONAL)
     *         .withToStringStyle(myToStringStyle)
     *         .withDefaultInterfaceSupport()
     *         .withInterfaceSupport(extensionSupport1)
     *         .withInterfaceSupport(extensionSupport2)
     *         .build();
     * </code>
     * </pre>
     * <p>
     * Please note that a newly built {@code ProxyIBeanFactory} does not contain any
     * extension interface support by default. Even the standard extension interfaces (like
     * {@link NullSafe} or {@link Freezable}) are not supported out of the box, you need to 
     * use {@link Builder#withDefaultInterfaceSupport()} when building the factory.
     */
    //@formatter:on     
    public static class Builder {

        private final Map<Class<?>, ExtensionCodeGenerator> extensionCodeGeneratorMap =
                new HashMap<>();
        private String genCodePackageName = "codegen";
        private Function<Class<?>, String> implTypeNameFunc = DEFAULT_TYPE_NAME_BUILDER;
        private BeanStyle beanStyle = BeanStyle.CLASSIC;
        private ToStringStyle toStringStyle = ToStringStyle.SHORT_PREFIX_STYLE;
        private BeanStyleSpecificCodeGenerator beanStyleHandler =
                BeanStyleSpecificCodeGenerator.CLASSIC;
        private File sourceDirectory;
        private Charset sourceCharset;

        private Builder() {
        }

        /**
         * Determines the {@link ToStringStyle} to be used in the built factory.
         * <p>
         * As a factory uses only style this method should be called only once
         * for each {@code Builder}. If called multiple times only the last call
         * will take effect.
         * 
         * @param toStringStyle
         *            either one of the predefined styles found in
         *            {@link ToStringStyle} or a custom {@link ToStringStyle}
         *            implementation
         * @return the {@code Builder} instance itself to enable chained calls
         */
        public Builder withToStringStyle(ToStringStyle toStringStyle) {
            checkNotNull(toStringStyle);
            this.toStringStyle = toStringStyle;
            return this;
        }

        public Builder withBeanStyleClassic() {
            this.beanStyle = BeanStyle.CLASSIC;
            this.beanStyleHandler = BeanStyleSpecificCodeGenerator.CLASSIC;
            return this;
        }

        public Builder withBeanStyleModern() {
            this.beanStyle = BeanStyle.MODERN;
            this.beanStyleHandler = BeanStyleSpecificCodeGenerator.MODERN;
            return this;
        }

        public Builder withBeanStyleClassicWithOptional() {
            this.beanStyle = BeanStyle.CLASSIC_WITH_OPTIONAL;
            this.beanStyleHandler = BeanStyleSpecificCodeGenerator.CLASSIC_WITH_OPTIONAL_SUPPORT;
            return this;
        }

        public Builder withBeanStyleCustom(BeanStyle beanStyle,
                BeanStyleSpecificCodeGenerator beanStyleHandler) {
            this.beanStyle = beanStyle;
            this.beanStyleHandler = beanStyleHandler;
            return this;
        }

        public Builder withPersistentSourceCode(File sourceDirectory, Charset sourceCharset) {
            this.sourceDirectory = sourceDirectory;
            this.sourceCharset = ObjectUtils.defaultIfNull(sourceCharset, Charsets.UTF_8);
            return this;
        }

        public Builder withInMemorySourceCode() {
            this.sourceDirectory = null;
            this.sourceCharset = null;
            return this;
        }

        //@formatter:off     
        /**
         * Registers a handler for an extension interface that is supposed to be used in the 
         * factory. This method needs to be called for each extension interface that is supposed
         * to be supported by the factory. If you want to use the default handlers for the 
         * built in extension interfaces use {@link #withDefaultInterfaceSupport()}
         * as a shortcut.
         * <p>
         * With this method you can determine handler for custom extension interfaces or you
         * can also provide your own handler for one of the built in extension interfaces.
         * <p>
         * Following sample code registers custom extension interface {@code ExtInterface} with 
         * handler {@code ExtHandler}:
         * <pre>
         * ExtensionSupport extSupport = new ExtensionSupport(
         *         ExtInterface.class, 
         *         ExtHandler.class, 
         *         true); //handler stateful
         * ProxyIBeanFactory factory = ProxyIBeanFactory.builder()
         *         .withDefaultInterfaceSupport()
         *         .withInterfaceSupport(extSupport)
         *         .build();
         * </pre>  
         * <p>
         * Note: this method should be called only once for each extension support. Multiple calls
         * will overwrite prior settings.
         * 
         * @param support an {@link ExtensionSupport} that bundles an extension interface with
         * its handler. All built in handlers already provide an {@link ExtensionSupport}
         * instance that can be used to register a built in extension interface with its 
         * built in default handler, for example {@link NullSafeHandler#SUPPORT}.
         * If handler or interface are custom a specific {@link ExtensionSupport} needs to be 
         * provided.
         * 
         * @return the {@code Builder} instance itself to enable chained calls
         * @see <a href="{@docRoot}/org/coliper/ibean/package-summary.html#package.description">
         *      IBean overview</a> 
         * @see ExtensionSupport
         * @see ExtensionHandler ExtensionHandler (for how to implement custom handlers)
         */
        //@formatter:on     
        /*
         * public Builder withInterfaceSupport(ExtensionSupport support) {
         * checkNotNull(support, "support"); this.interfaceSupport.add(support);
         * return this; }
         */

        /**
         * Convenience method that registers all default extension interfaces
         * with their default handlers. Should be used instead of calling
         * {@link #withInterfaceSupport(ExtensionSupport)} several times.
         * <p>
         * Please note that a newly built {@code ProxyIBeanFactory} does not
         * contain any extension interface support by default. Even the standard
         * extension interfaces (like {@link NullSafe} or {@link Freezable}) are
         * not supported out of the box, you need to use this method when
         * building the factory.
         * <p>
         * This method should be called only once per factory creation.
         * 
         * @return the {@code Builder} instance itself to enable chained calls
         */
        public Builder withDefaultInterfaceSupport() {
            Set<Class<?>> x = DEFAULT_EXT_GENERATOR_MAP.keySet();
            for (Class<?> defaultExtIntf : x) {
                this.withBuiltInInterfaceSupport(defaultExtIntf);
            }
            return this;
        }

        public Builder withBuiltInInterfaceSupport(Class<?> builtInExtensionInterface) {
            checkNotNull(builtInExtensionInterface, "builtInExtensionInterface");
            ExtensionCodeGenerator codeGenerator =
                    DEFAULT_EXT_GENERATOR_MAP.get(builtInExtensionInterface);
            checkArgument(codeGenerator != null, "%s is not a built in extension interface",
                    builtInExtensionInterface.getName());

            this.extensionCodeGeneratorMap.put(builtInExtensionInterface, codeGenerator);
            return this;
        }

        /**
         * Finally creates the specified {@link ProxyIBeanFactory}. Although it
         * is meant that per builder instance this method is executed only once
         * and as a final call, it is not prohibited that the factory is
         * modified afterwards with further {@code withXXX} calls and several
         * factories are created. Still this kind of use is discouraged and
         * might be prohibited in future versions.
         * 
         * @return the newly created factory
         */
        public CodegenIBeanFactory build() {
            return new CodegenIBeanFactory(this.extensionCodeGeneratorMap, this.genCodePackageName,
                    this.implTypeNameFunc, this.beanStyle, this.toStringStyle,
                    this.beanStyleHandler, this.sourceDirectory, this.sourceCharset);
        }
    }

    private final ClassLoader beanClassLoader;
    private final Map<Class<?>, Class<?>> implementationTypeMap = new ConcurrentHashMap<>();
    private final ResourceCreator resourceCreator;
    private final Map<Class<?>, ExtensionCodeGenerator> extensionCodeGeneratorMap;
    private final String genCodePackageName;
    private final Function<Class<?>, String> implTypeNameFunc;
    private final BeanStyle beanStyle;
    private final ToStringStyle toStringStyle;
    private final BeanStyleSpecificCodeGenerator beanStyleHandler;
    private final Charset sourceCharset;
    private final boolean removeCodeAfterCompilation;

    /**
     * @param beanClassLoader
     */
    CodegenIBeanFactory(Map<Class<?>, ExtensionCodeGenerator> extensionCodeGeneratorMap,
            String genCodePackageName, Function<Class<?>, String> implTypeNameFunc,
            BeanStyle beanStyle, ToStringStyle toStringStyle,
            BeanStyleSpecificCodeGenerator beanStyleHandler, File sourceDirectory,
            Charset sourceCharset) {

        final ResourceFinder resourceFinder;
        if (sourceDirectory != null) {
            resourceFinder = new DirectoryResourceFinder(sourceDirectory);
            this.resourceCreator = new DirectoryResourceCreator(sourceDirectory);
            this.sourceCharset = sourceCharset;
            this.removeCodeAfterCompilation = false;
        } else {
            final MapResourceCreator mapResourceCreator = new MapResourceCreator();
            this.resourceCreator = mapResourceCreator;
            resourceFinder = new ResourceFinder() {
                @Override
                public Resource findResource(String resourceName) {
                    return new MapResourceFinder(mapResourceCreator.getMap())
                            .findResource(resourceName);
                }
            };
            this.sourceCharset = INTERNAL_CODE_CHARSET;
            this.removeCodeAfterCompilation = true;
        }
        this.beanClassLoader = new JavaSourceClassLoader(this.getClass().getClassLoader(),
                resourceFinder, this.sourceCharset.name());
        this.extensionCodeGeneratorMap = extensionCodeGeneratorMap;
        this.genCodePackageName = genCodePackageName;
        this.implTypeNameFunc = implTypeNameFunc;
        this.beanStyle = beanStyle;
        this.toStringStyle = toStringStyle;
        this.beanStyleHandler = beanStyleHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.IBeanFactory#create(java.lang.Class)
     */
    @Override
    public <T> T create(Class<T> beanType) throws InvalidIBeanTypeException {
        checkNotNull(beanType, "beanType");
        Class<?> implementationType = this.implementationTypeMap.computeIfAbsent(beanType,
                this::createIBeanImplementation);

        T bean;
        try {
            bean = beanType.cast(implementationType.getDeclaredConstructor().newInstance());
            FieldUtils.writeDeclaredField(bean, CommonCodeSnippets.FACTORY_FIELD_NAME, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
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
        final String resourceName = ClassFile.getSourceResourceName(fullImplementationClassName);
        System.out.println(beanSourceCode);
        // this.resourceCreator.
        this.addCode(resourceName, beanSourceCode);
        try {
            return this.beanClassLoader.loadClass(fullImplementationClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("failed creating implementation class '"
                    + fullImplementationClassName + "' for bean type " + beanInterfaceType, e);
        } finally {
            if (this.removeCodeAfterCompilation) {
                this.resourceCreator.deleteResource(resourceName);
            }
        }
    }

    private void addCode(String fullImplementationClassName, String beanSourceCode) {
        try {
            try (OutputStream os =
                    this.resourceCreator.createResource(fullImplementationClassName)) {
                os.write(beanSourceCode.getBytes(this.sourceCharset));
            }
        } catch (IOException e) {
            throw new IllegalStateException("unable to create bean sources", e);
        }
    }

    /**
     * @param beanInterfaceType
     * @param implementationClassName
     * @return
     */
    private String createBeanSource(Class<?> beanInterfaceType, String implementationTypeName) {
        final List<Class<?>> ignorableSuperInterfaces =
                new ArrayList<>(extensionCodeGeneratorMap.keySet());
        IBeanTypeMetaInfo<?> meta = new IBeanMetaInfoParser().parse(beanInterfaceType, beanStyle,
                ignorableSuperInterfaces);
        return new BeanCodeGenerator(this.genCodePackageName, implementationTypeName, meta,
                this.beanStyleHandler, this.extensionCodeGeneratorsForType(beanInterfaceType))
                        .generateSourceCode();
    }

    private ExtensionCodeGenerator[] extensionCodeGeneratorsForType(Class<?> beanInterfaceType) {
        final Map<Class<?>, ExtensionCodeGenerator> selected = new HashMap<>();
        for (Entry<Class<?>, ExtensionCodeGenerator> codeGen : this.extensionCodeGeneratorMap
                .entrySet()) {
            final Class<?> extType = codeGen.getKey();
            if (extType.isAssignableFrom(beanInterfaceType)) {
                // We need to check if there are sub- or super-interfaces
                // already in "selected". We always want to keep the sub-type
                // and remove the super-type.
                final Optional<Class<?>> subOrSuper =
                        findSubOrSuperTypeInCollection(extType, selected.keySet());
                if (subOrSuper.isPresent()) {
                    // sub- or super-interface found!
                    if (extType.isAssignableFrom(subOrSuper.get())) {
                        // selected already contains sub-interface, we do not
                        // need "extType"
                        continue;
                    }
                    // remove super-interface from "selected"
                    selected.remove(subOrSuper.get());
                }
                selected.put(extType, codeGen.getValue());
            }
        }
        return selected.values().toArray(new ExtensionCodeGenerator[selected.size()]);
    }

    public ToStringStyle toStringStyle() {
        return this.toStringStyle;
    }
}
