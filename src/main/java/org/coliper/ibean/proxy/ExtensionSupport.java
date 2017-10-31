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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;

import org.coliper.ibean.util.ReflectionUtil;

/**
 * @author alex@coliper.org
 *
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
     * @param supportedInterface
     * @param handlerType
     * @param handlerStateful
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

    public Class<?> supportedInterface() {
        return this.supportedInterface;
    }

    /**
     * @return the handlerType
     */
    public Class<? extends ExtensionHandler> handlerType() {
        return handlerType;
    }

    /**
     * @return the handlerStateful
     */
    public boolean handlerStateful() {
        return handlerStateful;
    }

    public List<Class<?>> supportedInterfaceAndSuperInterfaces() {
        return ReflectionUtil.getSuperTypesInclRoot(this.supportedInterface);
    }

}
