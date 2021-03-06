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

import org.apache.commons.lang3.builder.ToStringStyle;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanTypeMetaInfo;

/**
 * {@link IBeanContext} is provided to {@link ExtensionHandler}s to provide them
 * information about the context of a IBean method call. Only used in
 * {@link ExtensionHandler} context, for example in
 * {@link ExtensionHandler#handleExtendedInterfaceCall(IBeanContext, IBeanFieldAccess, Object, java.lang.reflect.Method, Object[])}
 * 
 * @author alex@coliper.org
 */
public class IBeanContext<T> {
    private final ProxyIBeanFactory beanFactory;
    private final IBeanTypeMetaInfo<T> metaInfo;
    private final ToStringStyle toStringStyle;
    private final BeanStyle beanStyle;
    private final BeanStyleHandler beanStyleHandler;

    /**
     * Internal constructor as only created by the framework itself.
     */
    IBeanContext(ProxyIBeanFactory beanFactory, IBeanTypeMetaInfo<T> metaInfo,
            ToStringStyle toStringStyle, BeanStyle beanStyle, BeanStyleHandler beanStyleHandler) {
        this.beanFactory = beanFactory;
        this.metaInfo = metaInfo;
        this.toStringStyle = toStringStyle;
        this.beanStyle = beanStyle;
        this.beanStyleHandler = beanStyleHandler;
    }

    /**
     * Provides the factory that created the IBean. Especially useful if the
     * {@link ExtensionHandler} itself needs to create other IBeans.
     * 
     * @return the factory which is handling the method call this context
     *         relates to
     */
    public ProxyIBeanFactory beanFactory() {
        return this.beanFactory;
    }

    /**
     * Provides the meta information about the IBean.
     * 
     * @return meta information about bean type {@code T}
     */
    public IBeanTypeMetaInfo<T> metaInfo() {
        return metaInfo;
    }

    /**
     * Provides the style used in IBean's {@link Object#toString()} method.
     * 
     * @return the {@code ToStringStyle}
     */
    public ToStringStyle toStringStyle() {
        return toStringStyle;
    }

    /**
     * Provides the {@link BeanStyle} of the IBean.
     * 
     * @return the bean style this bean complies to
     */
    public BeanStyle beanStyle() {
        return beanStyle;
    }

    /**
     * Provides the {@link BeanStyleHandler} used by the bean factory to deal
     * with the given <code>BeanStyle</code>.
     * 
     * @return the factory specific handler for the given bean style
     */
    public BeanStyleHandler beanStyleHandler() {
        return beanStyleHandler;
    }

}
