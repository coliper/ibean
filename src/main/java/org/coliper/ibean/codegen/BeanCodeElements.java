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

package org.coliper.ibean.codegen;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @author alex@coliper.org
 *
 */
public class BeanCodeElements {
    private final String beanClassName;
    private final String beanPackageName;
    private final Map<String, String> fieldNameMap;

    /**
     * @param beanClassName
     * @param beanPackageName
     * @param fieldNameMap
     */
    BeanCodeElements(String beanClassName, String beanPackageName,
            Map<String, String> fieldNameMap) {
        this.beanClassName = beanClassName;
        this.beanPackageName = beanPackageName;
        this.fieldNameMap = ImmutableMap.copyOf(fieldNameMap);
    }

    public String beanClassName() {
        return beanClassName;
    }

    public String beanPackageName() {
        return beanPackageName;
    }

    public String fieldNameFromPropertyName(String propertyName) {
        final String ret = this.fieldNameMap.get(propertyName);
        checkArgument(ret != null, "invalid property name %s", propertyName);
        return ret;
    }

    public Collection<String> fieldNames() {
        return this.fieldNameMap.values();
    }
}
