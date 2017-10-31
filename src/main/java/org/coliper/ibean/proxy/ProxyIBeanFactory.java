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
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanMetaInfoParser;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.proxy.handler.CloneableHandler;
import org.coliper.ibean.proxy.handler.CompletableHandler;
import org.coliper.ibean.proxy.handler.FreezableHandler;
import org.coliper.ibean.proxy.handler.ModificationAwareHandler;
import org.coliper.ibean.proxy.handler.NullSafeHandler;
import org.coliper.ibean.proxy.handler.OptionalSupportHandler;

import com.google.common.collect.ImmutableList;

/**
 * @author alex@coliper.org
 *
 */
public class ProxyIBeanFactory implements IBeanFactory {

  //@formatter:off     
    public static List<ExtensionSupport> DEFAULT_INTERFACE_SUPPORTS =
            ImmutableList.of(ModificationAwareHandler.SUPPORT, 
                    OptionalSupportHandler.SUPPORT,
                    NullSafeHandler.SUPPORT,
                    FreezableHandler.SUPPORT,
                    CompletableHandler.SUPPORT,
                    CloneableHandler.SUPPORT);
  //@formatter:on 

    private final IBeanMetaInfoParser metaInfoParser;
    private final ToStringStyle toStringStyle;
    private final BeanStyle beanStyle;
    private final ProxyIBeanFactoryExtensionKit extendedInterfacesKit;

    private final Map<Class<?>, IBeanContext<?>> contextCache = new ConcurrentHashMap<>();

    /**
     * @param metaInfoParser
     * @param toStringStyle
     * @param beanStyle
     */
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Optional<IBeanMetaInfoParser> metaInfoParser = Optional.empty();
        private Optional<ToStringStyle> toStringStyle = Optional.empty();
        private Optional<BeanStyle> beanStyle = Optional.empty();
        private List<ExtensionSupport> interfaceSupport = new ArrayList<>();

        private Builder() {
        }

        public Builder withMetaInfoParser(IBeanMetaInfoParser metaInfoParser) {
            requireNonNull(metaInfoParser);
            this.metaInfoParser = Optional.of(metaInfoParser);
            return this;
        }

        public Builder withToStringStyle(ToStringStyle toStringStyle) {
            requireNonNull(toStringStyle);
            this.toStringStyle = Optional.of(toStringStyle);
            return this;
        }

        public Builder withBeanStyle(BeanStyle beanStyle) {
            requireNonNull(beanStyle);
            this.beanStyle = Optional.of(beanStyle);
            return this;
        }

        public Builder withDefaultInterfaceSupport() {
            this.interfaceSupport.addAll(DEFAULT_INTERFACE_SUPPORTS);
            return this;
        }

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
