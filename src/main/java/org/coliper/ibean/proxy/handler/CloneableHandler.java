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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.extension.CloneableBean;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;
import org.coliper.ibean.proxy.ProxyIBeanFactory;

import com.google.common.base.Throwables;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link CloneableBean}.
 * 
 * @author alex@coliper.org
 */
public class CloneableHandler extends StatelessExtensionHandler {

    /**
     * {@link ExtensionSupport} related to this handler supposed to be used when
     * configuring extension handlers in {@link IBeanFactory}s, for example in
     * {@link ProxyIBeanFactory.Builder#withInterfaceSupport(ExtensionSupport)}.
     */
    public static final ExtensionSupport SUPPORT =
            new ExtensionSupport(CloneableBean.class, CloneableHandler.class, false/* stateful */);

    private static final Method CLONE_METHOD;
    static {
        try {
            CLONE_METHOD = CloneableBean.class.getMethod("clone");
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFields(IBeanTypeMetaInfo<?> meta, Object sourceBean,
            Object targetBean) {
        List<IBeanFieldMetaInfo> f = meta.fieldMetaInfos();
        for (IBeanFieldMetaInfo fieldMeta : f) {
            copyField(fieldMeta, sourceBean, targetBean);
        }
    }

    private static void copyField(IBeanFieldMetaInfo fieldMeta, Object sourceBean,
            Object targetBean) {
        try {
            copyFieldThrowing(fieldMeta, sourceBean, targetBean);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwables.throwIfUnchecked(e.getTargetException());
            throw new RuntimeException(e.getTargetException());
        }
    }

    private static void copyFieldThrowing(IBeanFieldMetaInfo fieldMeta, Object sourceBean,
            Object targetBean) throws IllegalAccessException, InvocationTargetException {
        Object val = fieldMeta.getterMethod().invoke(sourceBean);
        // if value is wrapped into an Optional, unwrap first
        if (val instanceof Optional && fieldMeta.fieldType() != Optional.class) {
            final Optional<?> opt = (Optional<?>) val;
            val = opt.orElse(null);
        }
        fieldMeta.setterMethod().invoke(targetBean, val);
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
        if (CLONE_METHOD.equals(method)) {
            return this.handleCloneCall(context, proxyInstance);
        }
        throw new UnsupportedOperationException("unexpected call of " + method);
    }

    @SuppressWarnings("unchecked")
    private Object handleCloneCall(IBeanContext<?> context, Object proxyInstance) throws Throwable {
        final Object clone = context.beanFactory().create(context.metaInfo().beanType());
        copyFields((IBeanTypeMetaInfo<Object>) context.metaInfo(), proxyInstance, clone);
        return clone;
    }
}
