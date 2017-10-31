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

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.extension.BeanIncompleteException;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.extension.OptionalSupport;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanFieldAccess;

/**
 * @author alex@coliper.org
 *
 */
public class CompletableHandler extends StatelessExtensionHandler {

    public static final ExtensionSupport SUPPORT = new ExtensionSupport(
            Completable.class, CompletableHandler.class, false/* stateful */);

    private static final Method IS_COMPLETE_METHOD;
    private static final Method ASSERT_COMPLETE_METHOD;
    static {
        try {
            IS_COMPLETE_METHOD = Completable.class.getMethod("isComplete");
            ASSERT_COMPLETE_METHOD = Completable.class.getMethod("assertComplete");
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

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

    private Object handleAssertCompleteCall(IBeanContext<?> context, IBeanFieldAccess bean, Object proxyInstance)
            throws Throwable {
        if (!isBeanComplete(context, bean)) {
            throw new BeanIncompleteException();
        }
        return proxyInstance; /* this */
    }

    private boolean isBeanComplete(IBeanContext<?> context, IBeanFieldAccess bean) {
        final boolean optionalSupport =
                OptionalSupport.class.isAssignableFrom(context.metaInfo().beanType());
        for (IBeanFieldMetaInfo fieldMeta : context.metaInfo().fieldMetaInfos()) {
            if (fieldMeta.fieldType().isPrimitive()) {
                continue; // ignore primitive type fields
            }
            if (optionalSupport && fieldMeta.getterMethod().getReturnType() == Optional.class) {
                continue; // we do not care if value is null when Optional is
                          // returned from getter
            }
            if (bean.getFieldValue(fieldMeta) == null) {
                return false;
            }
        }
        return true;
    }
}
