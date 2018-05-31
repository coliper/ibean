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

package org.coliper.ibean.proxy.handler;

import java.lang.reflect.Method;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;

/**
 * Abstract base class for {@link ExtensionHandler} implementations that do not
 * have to maintain their own state. Concrete implementations can choose with of
 * the default implementations of
 * {@link #handleExtendedInterfaceCall(IBeanContext, IBeanFieldAccess, Object, Method, Object[])},
 * {@link #interceptGetterCall(IBeanContext, IBeanFieldMetaInfo, Object, Object)}
 * and
 * {@link #interceptSetterCall(IBeanContext, IBeanFieldMetaInfo, Object, Object)}
 * to override.
 * 
 * @author alex@coliper.org
 */
public abstract class StatelessExtensionHandler implements ExtensionHandler {

    /**
     * This default implementation always throws a
     * {@link UnsupportedOperationException}. Should be always overridden in
     * case the extension interface contains its own methods.
     * 
     * @see org.coliper.ibean.proxy.ExtensionHandler#handleExtendedInterfaceCall(IBeanContext,
     *      IBeanFieldAccess, Object, Method, Object[])
     */
    @Override
    public Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable {
        throw new UnsupportedOperationException("unexpected call of " + method);
    }

}
