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

package org.coliper.ibean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author alex@coliper.org
 *
 */
public class CachedIBeanMetaInfoParser extends IBeanMetaInfoParser {
    
    private static final Map<Class<?>, IBeanTypeMetaInfo<?>> CACHE = new ConcurrentHashMap<>();

    /* (non-Javadoc)
     * @see org.coliper.ibean.IBeanMetaInfoParser#parse(java.lang.Class, org.coliper.ibean.BeanStyle, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> IBeanTypeMetaInfo<T> parse(Class<T> beanType, final BeanStyle beanStyle,
            final List<Class<?>> ignorableSuperInterfaces) {
        IBeanTypeMetaInfo<?> ret = CACHE.computeIfAbsent(beanType, 
                (t) -> super.parse(t, beanStyle, ignorableSuperInterfaces));
        // Only for one bean style meta is cached assuming there are not several bean styles used
        // in parallel.
        if (!ret.beanStyle().equals(beanStyle)) {
            return super.parse(beanType, beanStyle, ignorableSuperInterfaces);
        }
        return (IBeanTypeMetaInfo<T>) ret;
    }
   

}
