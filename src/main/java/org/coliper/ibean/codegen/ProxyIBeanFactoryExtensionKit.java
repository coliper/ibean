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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.coliper.ibean.IBeanTypeMetaInfo;

import com.google.common.collect.ImmutableList;

/**
 * Class representing a configuration of all extension interfaces that are supported by a
 * {@link ProxyIBeanFactory}. It is only used internally by {@link ProxyIBeanFactory} to store
 * its extension interface setup.<p>
 * {@link ProxyIBeanFactoryExtensionKit} does not only hold the extension interface configuration,
 * it also supports creating new {@link ExtensionHandlerDispatcher} instances for new beans objects
 * (see {@link #createHandlerFor(IBeanTypeMetaInfo)}).
 * 
 * @author alex@coliper.org
 */
class ProxyIBeanFactoryExtensionKit {

    private static class IBeanHandlerInterceptorBundle {
        final ExtensionSupport support;
        final Optional<ExtensionHandler> handler;

        IBeanHandlerInterceptorBundle(ExtensionHandler handler, ExtensionSupport support) {
            this.support = support;
            this.handler = Optional.ofNullable(handler);
        }

    }

    private static List<IBeanHandlerInterceptorBundle> createBundleList(
            List<ExtensionSupport> intfSupportList) {
        ImmutableList.Builder<IBeanHandlerInterceptorBundle> listBuilder = ImmutableList.builder();
        for (ExtensionSupport intfSupport : intfSupportList) {
            final IBeanHandlerInterceptorBundle bundle = createGlobalBundle(intfSupport);
            listBuilder.add(bundle);
        }
        return listBuilder.build();
    }

    private static IBeanHandlerInterceptorBundle createGlobalBundle(ExtensionSupport intfSupport) {
        final ExtensionHandler handler;
        if (intfSupport.handlerStateful()) {
            handler = null;
        } else {
            handler = createHandler(intfSupport);
        }
        final IBeanHandlerInterceptorBundle bundle =
                new IBeanHandlerInterceptorBundle(handler, intfSupport);
        return bundle;
    }

    private static ExtensionHandler createHandler(ExtensionSupport support) {
        try {
            return support.handlerType().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // unexpected as this was already checked when creating the support
            // instance
            throw new IllegalStateException(e);
        }
    }

    private static List<Class<?>> createSupportedInterfacesList(
            List<ExtensionSupport> intfSupportList) {
        Set<Class<?>> interfaceSet = new HashSet<>();
        for (ExtensionSupport intfSupport : intfSupportList) {
            interfaceSet.addAll(intfSupport.supportedInterfaceAndSuperInterfaces());
        }
        return ImmutableList.copyOf(interfaceSet);
    }

    private final List<IBeanHandlerInterceptorBundle> bundles;
    private final List<Class<?>> supportedInterfaces;

    ProxyIBeanFactoryExtensionKit(List<ExtensionSupport> intfSupportList) {
        requireNonNull(intfSupportList, "intfSupportList");

        this.bundles = createBundleList(intfSupportList);
        this.supportedInterfaces = createSupportedInterfacesList(intfSupportList);
    }

    ExtensionHandlerDispatcher createHandlerFor(IBeanTypeMetaInfo<?> metaInfo) {
        requireNonNull(metaInfo, "metaInfo");
        ExtensionHandlerDispatcher.Builder dispatcherBuilder =
                new ExtensionHandlerDispatcher.Builder();

        for (IBeanHandlerInterceptorBundle bundle : bundles) {
            addHandlerForSupportedTypesToDispatcherBuilder(metaInfo, dispatcherBuilder, bundle);
        }

        return dispatcherBuilder.build();
    }

    private void addHandlerForSupportedTypesToDispatcherBuilder(IBeanTypeMetaInfo<?> metaInfo,
            ExtensionHandlerDispatcher.Builder dispatcherBuilder,
            IBeanHandlerInterceptorBundle bundle) {
        ExtensionHandler handler = bundle.handler.orElse(null);
        for (Class<?> supportedType : bundle.support.supportedInterfaceAndSuperInterfaces()) {
            if (supportedType.isAssignableFrom(metaInfo.beanType())) {
                if (handler == null) {
                    handler = createHandler(bundle.support);
                }
                dispatcherBuilder.add(supportedType, handler);
            }
        }
    }

    List<Class<?>> getSupportedExtendedInterfaces() {
        return this.supportedInterfaces;
    }

}
