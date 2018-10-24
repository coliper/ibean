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

import java.util.List;

import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.proxy.ProxyIBeanFactory.Builder;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * Bundles an {@link ExtensionHandler} with its supported interfaces. This tuple
 * is then used as a configuration element for creating and customizing
 * {@link IBeanFactory}s. See {@link ExtensionHandler} for a general description
 * of the {@link ProxyIBeanFactory} extension concept. See
 * {@link Builder#withInterfaceSupport(ExtensionSupport)} for where and how to
 * use {@link ExtensionSupport}. {@link ExtensionSupport} instances are created
 * via constructor {@link #ExtensionSupport(Class, Class, boolean)} and are then
 * immutable.
 * 
 * @author alex@coliper.org
 */
public class ExtensionSupport {

    private static void validateHandlerType(Class<? extends ExtensionHandler> handlerType) {
        try {
            handlerType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("class " + handlerType + "is not a valid handler "
                    + "type as it does not contain a public default constructor");
        }
    }

    private final Class<?> supportedInterface;
    private final Class<? extends ExtensionHandler> handlerType;
    private final boolean handlerStateful;

    /**
     * Creates a new immutable {@link ExtensionSupport}.
     * 
     * @param supportedInterface
     *            specifies the extension interface supported by the
     *            {@link ExtensionHandler}. Must be a Java interface type.
     * @param handlerType
     *            the {@link ExtensionHandler} type responsible for intercepting
     *            IBean calls to the extension interface. Must be a sublcass of
     *            {@link ExtensionHandler}.
     * @param handlerStateful
     *            specifies if the handler is stateful or stateless. If
     *            <code>true</code> a handler type will be treated as stateful
     *            an a new handler instance will be created for each new IBean
     *            instance.
     */
    public ExtensionSupport(Class<?> supportedInterface,
            Class<? extends ExtensionHandler> handlerType, boolean handlerStateful) {
        requireNonNull(supportedInterface, "supportedInterface");
        requireNonNull(handlerType, "handlerType");
        checkArgument(supportedInterface.isInterface(), "supportedInterface %s is not an interface",
                supportedInterface);
        validateHandlerType(handlerType);
        this.supportedInterface = supportedInterface;
        this.handlerType = handlerType;
        this.handlerStateful = handlerStateful;
    }

    /**
     * Provides the interface that is covered by the corresponding handler.
     * 
     * @return the extension interface type
     */
    public Class<?> supportedInterface() {
        return this.supportedInterface;
    }

    /**
     * Provides the {@link ExtensionHandler} implementation responsible for the
     * supported interface.
     * 
     * @return the {@code ExtensionHandler} implementation
     */
    public Class<? extends ExtensionHandler> handlerType() {
        return handlerType;
    }

    /**
     * Returns <code>true</code> if the handler is stateful and needs to have an
     * individual instance per IBean object.
     * 
     * @return <code>false</code> if a single handler instance can handle all
     *         bean objects
     */
    public boolean handlerStateful() {
        return handlerStateful;
    }

    /**
     * Returns the {@link #supportedInterface()} together with all
     * sub-interfaces of that interface.
     * 
     * @return a list of interface types. Never <code>null</code> but might be
     *         empty.
     */
    public List<Class<?>> supportedInterfaceAndSuperInterfaces() {
        return ReflectionUtil.getSuperTypesInclRoot(this.supportedInterface);
    }

}
