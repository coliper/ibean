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
import java.util.Optional;

import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.extension.BeanIncompleteException;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.coliper.ibean.util.ReflectionUtil;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link Completable}.
 *
 * @author alex@coliper.org
 */
public class CompletableHandler extends StatelessExtensionHandler {

    /**
     * {@link ExtensionSupport} related to this handler supposed to be used when
     * configuring extension handlers in {@link IBeanFactory}s, for example in
     * {@link ProxyIBeanFactory.Builder#withInterfaceSupport(ExtensionSupport)}.
     */
    public static final ExtensionSupport SUPPORT = new ExtensionSupport(Completable.class,
            CompletableHandler.class, false/* stateful */);

    private final static Method IS_COMPLETE_METHOD =
            ReflectionUtil.lookupInterfaceMethod(Completable.class, s -> s.isComplete());
    private final static Method ASSERT_COMPLETE_METHOD =
            ReflectionUtil.lookupInterfaceMethod(Completable.class, s -> s.assertComplete());

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.proxy.handler.StatelessExtendedInterfaceHandler#
     * handleExtendedInterfaceCall(org.coliper.ibean.proxy.IBeanContext,
     * org.coliper.ibean.proxy.IBeanFieldAccess, java.lang.reflect.Method,
     * java.lang.Object[])
     */
    @Override
    public Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable {
        if (IS_COMPLETE_METHOD.equals(method)) {
            return this.handleIsCompleteCall(context, bean);
        }
        if (ASSERT_COMPLETE_METHOD.equals(method)) {
            return this.handleAssertCompleteCall(context, bean, proxyInstance);
        }
        throw new UnsupportedOperationException("unexpected call of " + method);
    }

    private Boolean handleIsCompleteCall(IBeanContext<?> context, IBeanFieldAccess bean)
            throws Throwable {
        return Boolean.valueOf(isBeanComplete(context, bean));
    }

    private Object handleAssertCompleteCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance) throws Throwable {
        if (!isBeanComplete(context, bean)) {
            throw new BeanIncompleteException();
        }
        return proxyInstance; /* this */
    }

    private boolean isBeanComplete(IBeanContext<?> context, IBeanFieldAccess bean) {
        for (IBeanFieldMetaInfo fieldMeta : context.metaInfo().fieldMetaInfos()) {
            if (fieldMeta.fieldType().isPrimitive()) {
                continue; // ignore primitive type fields
            }
            if (fieldMeta.getterMethod().getReturnType() == Optional.class
                    && fieldMeta.fieldType() != Optional.class) {
                continue; // we do not care if value is null when Optional is
                          // returned from getter but field type itself is not
                          // optional
            }
            if (bean.getFieldValue(fieldMeta) == null) {
                return false;
            }
        }
        return true;
    }
}
