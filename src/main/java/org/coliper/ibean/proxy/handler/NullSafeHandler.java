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

import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.extension.NullSafetyException;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;
import org.coliper.ibean.proxy.ProxyIBeanFactory;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link NullSafe}.
 * 
 * @author alex@coliper.org
 */
public class NullSafeHandler extends StatelessExtensionHandler {

    /**
     * {@link ExtensionSupport} related to this handler supposed to be used when
     * configuring extension handlers in {@link IBeanFactory}s, for example in
     * {@link ProxyIBeanFactory.Builder#withInterfaceSupport(ExtensionSupport)}.
     */
    public static final ExtensionSupport SUPPORT = new ExtensionSupport(NullSafe.class,
            NullSafeHandler.class, false/* stateful */);

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.proxy.handler.StatelessExtendedInterfaceHandler#
     * interceptGetterCall(org.coliper.ibean.proxy.IBeanContext,
     * org.coliper.ibean.IBeanFieldMetaInfo, java.lang.Object)
     */
    @Override
    public Object interceptGetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            IBeanFieldAccess bean, Object returnValue, Object proxyInstance) {
        if (returnValue == null) {
            throw new NullSafetyException(fieldMeta.fieldName());
        }
        return returnValue;
    }

}
