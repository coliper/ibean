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

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;

/**
 * @author alex@coliper.org
 *
 */
public abstract class CodegenFactoryUtil {

    public static IBeanFactory factoryWithStyle(BeanStyle style) {
        if (style == BeanStyle.CLASSIC) {
            return CodegenIBeanFactory.builder().beanStyleClassic().build();
        }
        if (style == BeanStyle.CLASSIC_WITH_OPTIONAL) {
            return CodegenIBeanFactory.builder().beanStyleClassicWithOptional().build();
        }
        if (style == BeanStyle.MODERN) {
            return CodegenIBeanFactory.builder().beanStyleModern().build();
        }
        throw new RuntimeException("unexpected bean style " + style);
    }

}
