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

package org.coliper.ibean.proxy;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.CachedIBeanMetaInfoParser;
import org.coliper.ibean.IBean;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.proxy.handler.CloneableHandler;
import org.coliper.ibean.proxy.handler.CompletableHandler;
import org.coliper.ibean.proxy.handler.FreezableHandler;
import org.coliper.ibean.proxy.handler.GsonSupportHandler;
import org.coliper.ibean.proxy.handler.Jackson2SupportHandler;
import org.coliper.ibean.proxy.handler.ModificationAwareHandler;
import org.coliper.ibean.proxy.handler.NullSafeHandler;

import com.google.common.collect.ImmutableList;

//@formatter:off     
/**
 * Default implementation of {@link IBeanFactory} based on Java {@link Proxy}
 * technology. This means it creates IBeans as interface proxies. There is a
 * ready-to-use instance of {@link ProxyIBeanFactory} available by default in
 * {@link IBean} but you might want to create individual instances of
 * {@code ProxyIBeanFactory}. This is necessary if you want to
 * <ul>
 * <li>use a different {@link BeanStyle},</li>
 * <li>change the {@link ToStringStyle} or</li>
 * <li>register your own extension interfaces.</li>
 * </ul>
 * <p>
 * To create a {@code ProxyIBeanFactory} use nested {@link Builder} class. To
 * obey a {@code Builder} instance call {@link #builder()}.<br>
 * For example:
 * 
 * <pre>
 * ProxyIBeanFactory factory = ProxyIBeanFactory.builder()
 *         .withBeanStyle(BeanStyle.MODERN_WITH_OPTIONAL)
 *         .withToStringStyle(myToStringStyle)
 *         .withDefaultInterfaceSupport()
 *         .withInterfaceSupport(extensionSupport1)
 *         .withInterfaceSupport(extensionSupport2)
 *         .build();
 * </pre>
 * <p>
 * Please note that a newly built {@code ProxyIBeanFactory} does not contain any
 * extension interface support by default. Even the standard extension interfaces (like
 * {@link NullSafe} or {@link Freezable}) are not supported out of the box, you need to 
 * use {@link Builder#withDefaultInterfaceSupport()} when building the factory.
 * 
 * @see Proxy
 * @see Builder
 * @author alex@coliper.org
 */
//@formatter:on   
public class ProxyIBeanFactory implements IBeanFactory {

  //@formatter:off     
    private static List<ExtensionSupport> DEFAULT_INTERFACE_SUPPORTS =
            ImmutableList.of(ModificationAwareHandler.SUPPORT, 
                    NullSafeHandler.SUPPORT,
                    FreezableHandler.SUPPORT,
                    CompletableHandler.SUPPORT,
                    CloneableHandler.SUPPORT,
                    GsonSupportHandler.SUPPORT,
                    Jackson2SupportHandler.SUPPORT);
  //@formatter:on 

    private final IBeanMetaInfoParser metaInfoParser;
    private final ToStringStyle toStringStyle;
    private final BeanStyle beanStyle;
    private final ProxyIBeanFactoryExtensionKit extendedInterfacesKit;

    private final Map<Class<?>, IBeanContext<?>> contextCache = new ConcurrentHashMap<>();

    private ProxyIBeanFactory(IBeanMetaInfoParser metaInfoParser, ToStringStyle toStringStyle,
            BeanStyle beanStyle, ProxyIBeanFactoryExtensionKit extendedInterfacesKit) {
        this.metaInfoParser = metaInfoParser;
        this.toStringStyle = toStringStyle;
        this.beanStyle = beanStyle;
        this.extendedInterfacesKit = extendedInterfacesKit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.IBeanFactory#create(java.lang.Class)
     */
    @Override
    public <T> T create(Class<T> beanType) {
        Class<?>[] interfaces = new Class<?>[] { beanType };
        IBeanContext<T> context = this.getOrCreateContext(beanType);
        ExtensionHandlerDispatcher handlerDispatcher =
                this.extendedInterfacesKit.createHandlerFor(context.metaInfo());
        ProxyIBean<T> handler = new ProxyIBean<>(context, handlerDispatcher);
        final T proxy = beanType
                .cast(Proxy.newProxyInstance(beanType.getClassLoader(), interfaces, handler));
        handlerDispatcher.initHandler(proxy, context.metaInfo());
        return proxy;
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
        private Optional<IBeanMetaInfoParser> metaInfoParser = Optional.empty();
        private Optional<ToStringStyle> toStringStyle = Optional.empty();
        private Optional<BeanStyle> beanStyle = Optional.empty();
        private List<ExtensionSupport> interfaceSupport = new ArrayList<>();

        private Builder() {
        }

        /**
         * Sets the {@link IBeanMetaInfoParser} to be used by the factory. This
         * method needs to be used only in very rare cases. If not set the
         * factory will use a {@link CachedIBeanMetaInfoParser} which is best
         * choice for most cases.
         * <p>
         * As a factory uses only one meta parser this method should be called
         * only once for each {@code Builder}. If called multiple times only the
         * last call will take effect.
         * 
         * @param metaInfoParser
         *            the {@link IBeanMetaInfoParser} to be used in the factory
         * @return the {@code Builder} instance itself to enable chained calls
         */
        public Builder withMetaInfoParser(IBeanMetaInfoParser metaInfoParser) {
            requireNonNull(metaInfoParser);
            this.metaInfoParser = Optional.of(metaInfoParser);
            return this;
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
            requireNonNull(toStringStyle);
            this.toStringStyle = Optional.of(toStringStyle);
            return this;
        }

        /**
         * Determines the {@link BeanStyle} to be used in the created factory.
         * <p>
         * As a factory uses only style this method should be called only once
         * for each {@code Builder}. If called multiple times only the last call
         * will take effect.
         * 
         * @param beanStyle
         *            one of the predefined styles in {@link BeanStyle} or a
         *            custom {@link BeanStyle} implementation
         * @return the {@code Builder} instance itself to enable chained calls
         */
        public Builder withBeanStyle(BeanStyle beanStyle) {
            requireNonNull(beanStyle);
            this.beanStyle = Optional.of(beanStyle);
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
        public Builder withInterfaceSupport(ExtensionSupport support) {
            requireNonNull(support, "support");
            this.interfaceSupport.add(support);
            return this;
        }

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
            this.interfaceSupport.addAll(DEFAULT_INTERFACE_SUPPORTS);
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
        public ProxyIBeanFactory build() {
            ProxyIBeanFactoryExtensionKit extendedInterfacesKit =
                    new ProxyIBeanFactoryExtensionKit(this.interfaceSupport);
            return new ProxyIBeanFactory(
                    this.metaInfoParser.orElseGet(() -> new CachedIBeanMetaInfoParser()),
                    this.toStringStyle.orElse(ToStringStyle.SHORT_PREFIX_STYLE),
                    this.beanStyle.orElse(BeanStyle.CLASSIC), extendedInterfacesKit);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> IBeanContext<T> getOrCreateContext(final Class<T> beanType) {
        return (IBeanContext<T>) this.contextCache.computeIfAbsent(beanType,
                (t) -> createContext(t));
    }

    private <T> IBeanContext<T> createContext(Class<T> beanType) {
        final List<Class<?>> supportedExtendedInterfaces =
                this.extendedInterfacesKit.getSupportedExtendedInterfaces();
        IBeanTypeMetaInfo<T> meta =
                this.metaInfoParser.parse(beanType, this.beanStyle, supportedExtendedInterfaces);
        return new IBeanContext<>(this, meta, this.toStringStyle, this.beanStyle);
    }
}
