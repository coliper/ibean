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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.google.common.collect.ImmutableMap;

/**
 * Class used internally by {@link ProxyIBean} to process calls to methods that belong to the 
 * extension interfaces. Every {@link ProxyIBean} owns an instance of 
 * {@link ExtensionHandlerDispatcher} and routes every extension interface call to it.
 * <p>
 * {@link ExtensionHandlerDispatcher} instances can only be created via its nested {@link Builder}
 * class.
 * 
 * @author alex@coliper.org
 *
 */
class ExtensionHandlerDispatcher {
    
    /*
     * ExtensionHandlerDispatcher itself does not handle extension interface calls. It just 
     * dispatches them further to the appropriate ExtensionHandler. To do so it holds all required
     * handlers in "handlerMap".
     */

    private static final ExtensionHandlerDispatcher EMPTY_BUNDLES_HANDLER =
            new ExtensionHandlerDispatcher(Collections.emptyMap());

    static class Builder {
        private final ImmutableMap.Builder<Class<?>, ExtensionHandler> handlerMapBuilder =
                new ImmutableMap.Builder<>();

        void add(Class<?> type, ExtensionHandler handler) {
            this.handlerMapBuilder.put(type, handler);
        }

        ExtensionHandlerDispatcher build() {

            final ImmutableMap<Class<?>, ExtensionHandler> map = this.handlerMapBuilder.build();
            if (map.isEmpty()) {
                return EMPTY_BUNDLES_HANDLER;
            }
            return new ExtensionHandlerDispatcher(map);
        }
    }

    // Maps all supported extension interface types to the corresponding ExtensionHandler.
    // For example it would contain NullSafe.class mapped to a NullSafeHandler instance.
    private final Map<Class<?>, ExtensionHandler> handlerMap;

    /**
     * @param interfaceSetupMap
     */
    private ExtensionHandlerDispatcher(Map<Class<?>, ExtensionHandler> handlerMap) {
        requireNonNull(handlerMap, "handlerMap");
        // we do not copy the map as we trust the caller
        this.handlerMap = handlerMap;
    }

    Object interceptGetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object returnValue, Object proxyInstance) {
        Object modifiedReturnValue = returnValue;
        for (ExtensionHandler handler : handlerMap.values()) {
            modifiedReturnValue = handler.interceptGetterCall(context, fieldMeta,
                    modifiedReturnValue, proxyInstance);
        }
        return modifiedReturnValue;
    }

    Object interceptSetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object newValue, Object proxyInstance) {
        Object modifiedNewValue = newValue;
        for (ExtensionHandler handler : handlerMap.values()) {
            modifiedNewValue = handler.interceptSetterCall(context, fieldMeta, modifiedNewValue,
                    proxyInstance);
        }
        return modifiedNewValue;
    }

    private ExtensionHandler handlerForType(Class<?> type) {
        ExtensionHandler handler = this.handlerMap.get(type);
        if (handler == null) {
            throw new IllegalStateException("unknown type " + type);
        }
        return handler;
    }

    boolean canHandleCall(Method method) {
        requireNonNull(method, "method");
        return this.handlerMap.containsKey(method.getDeclaringClass());
    }

    Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable {
        return this.handlerForType(method.getDeclaringClass()).handleExtendedInterfaceCall(context,
                bean, proxyInstance, method, params);
    }

    void initHandler(Object proxyInstance, IBeanTypeMetaInfo<?> metaInfo) {
        for (ExtensionHandler handler : this.handlerMap.values()) {
            handler.onInitStateful(proxyInstance, metaInfo);
        }

    }

}
