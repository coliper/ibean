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

import java.lang.reflect.Method;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

/**
 * @author alex@coliper.org
 *
 */
public interface ExtensionHandler {
    Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable;

    Object interceptGetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object returnValue, Object proxyInstance);

    Object interceptSetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object newValue, Object proxyInstance);

    default void onInitStateful(Object proxyInstance, IBeanTypeMetaInfo<?> metaInfo) {
    }
}
