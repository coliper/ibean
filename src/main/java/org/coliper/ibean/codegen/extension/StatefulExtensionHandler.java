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

package org.coliper.ibean.codegen.extension;

import java.lang.reflect.Method;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;

/**
 * /** Abstract base class for {@link ExtensionHandler} implementations that
 * need to maintain their own state. Concrete implementations can choose with of
 * the default implementations of
 * {@link #handleExtendedInterfaceCall(IBeanContext, IBeanFieldAccess, Object, Method, Object[])},
 * {@link #interceptGetterCall(IBeanContext, IBeanFieldMetaInfo, Object, Object)}
 * and
 * {@link #interceptSetterCall(IBeanContext, IBeanFieldMetaInfo, Object, Object)}
 * to override.
 * <p>
 * For stateful handlers it is recommendable if the handler itself implements
 * the extension interface. This class' implementation of
 * {@link #handleExtendedInterfaceCall(IBeanContext, IBeanFieldAccess, Object, Method, Object[])}
 * does simply forwards any extension interface call to the bean to associated
 * handler instance.
 * 
 * @author alex@coliper.org
 */
public abstract class StatefulExtensionHandler implements ExtensionHandler {

    /**
     * This default implementation re-calls the interface method onto the
     * handler itself assuming the handler implements the extension interface as
     * well.
     * 
     * @see ExtensionHandler#handleExtendedInterfaceCall(IBeanContext,
     *      IBeanFieldAccess, Object, Method, Object[])
     */
    @Override
    public Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable {
        return method.invoke(this, params);
    }

}
