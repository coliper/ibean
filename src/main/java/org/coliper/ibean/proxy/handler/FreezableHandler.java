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

import static java.util.Objects.requireNonNull;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.extension.BeanFrozenException;
import org.coliper.ibean.extension.TempFreezable;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanContext;

/**
 * @author alex@coliper.org
 *
 */
public class FreezableHandler extends StatefulExtensionHandler implements TempFreezable<Object> {
    public static final ExtensionSupport SUPPORT =
            new ExtensionSupport(TempFreezable.class, FreezableHandler.class, true/* stateful */);

    private boolean frozen = false;
    private Object proxyInstance = null;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.coliper.ibean.proxy.IBeanInvocationHandler#onInitStateful(org.coliper
     * .ibean.IBeanTypeMetaInfo)
     */
    @Override
    public void onInitStateful(Object proxyInstance, IBeanTypeMetaInfo<?> metaInfo) {
        requireNonNull(proxyInstance, "proxyInstance");
        this.proxyInstance = proxyInstance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.proxy.handler.StatefulExtendedInterfaceHandler#
     * interceptSetterCall(org.coliper.ibean.proxy.IBeanContext,
     * org.coliper.ibean.IBeanFieldMetaInfo, java.lang.Object)
     */
    @Override
    public Object interceptSetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object newValue, Object proxyInstance) {
        if (this.frozen) {
            throw new BeanFrozenException();
        }
        return newValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.extension.Freezable#freeze()
     */
    @Override
    public Object freeze() {
        this.frozen = true;
        return this.proxyInstance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.extension.Freezable#isFrozen()
     */
    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.extension.TempFreezable#unfreeze()
     */
    @Override
    public Object unfreeze() {
        this.frozen = false;
        return this.proxyInstance;
    }

}
