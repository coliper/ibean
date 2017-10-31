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
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;
import org.coliper.ibean.proxy.ExtensionHandler;

/**
 * @author alex@coliper.org
 *
 */
public abstract class StatefulExtensionHandler implements ExtensionHandler {

    /* (non-Javadoc)
     * @see org.coliper.ibean.proxy.IBeanInvocationHandler#handleExtendedInterfaceCall(org.coliper.ibean.proxy.IBeanContext, org.coliper.ibean.proxy.IBeanFieldAccess, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable {
        return method.invoke(this, params);
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.proxy.IBeanInvocationHandler#interceptGetterCall(org.coliper.ibean.proxy.IBeanContext, org.coliper.ibean.IBeanFieldMetaInfo, java.lang.Object)
     */
    @Override
    public Object interceptGetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object returnValue, Object proxyInstance) {
        return returnValue;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.proxy.IBeanInvocationHandler#interceptSetterCall(org.coliper.ibean.proxy.IBeanContext, org.coliper.ibean.IBeanFieldMetaInfo, java.lang.Object)
     */
    @Override
    public Object interceptSetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object newValue, Object proxyInstance) {
        return newValue;
    }

}
