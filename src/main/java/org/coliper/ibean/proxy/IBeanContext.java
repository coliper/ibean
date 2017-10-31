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
 * @author alex@coliper.org
 *
 */
public class IBeanContext<T> {
    private final ProxyIBeanFactory beanFactory;
    private final IBeanTypeMetaInfo<T> metaInfo;
    private final ToStringStyle toStringStyle;
    private final BeanStyle beanStyle;

    /**
     * @param metaInfo
     * @param toStringStyle
     * @param beanStyle
     */
    IBeanContext(ProxyIBeanFactory beanFactory, IBeanTypeMetaInfo<T> metaInfo,
            ToStringStyle toStringStyle, BeanStyle beanStyle) {
        this.beanFactory = beanFactory;
        this.metaInfo = metaInfo;
        this.toStringStyle = toStringStyle;
        this.beanStyle = beanStyle;
    }
    
    public ProxyIBeanFactory beanFactory() {
        return this.beanFactory;
    }

    /**
     * @return the metaInfo
     */
    public IBeanTypeMetaInfo<T> metaInfo() {
        return metaInfo;
    }

    /**
     * @return the toStringStyle
     */
    public ToStringStyle toStringStyle() {
        return toStringStyle;
    }

    /**
     * @return the beanStyle
     */
    public BeanStyle beanStyle() {
        return beanStyle;
    }

}
