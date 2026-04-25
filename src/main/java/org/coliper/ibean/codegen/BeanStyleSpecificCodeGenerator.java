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
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.beanstyle.ClassicBeanStyle;
import org.coliper.ibean.beanstyle.ClassicBeanStyleWithOptionalSupport;
import org.coliper.ibean.beanstyle.ModernBeanStyle;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.coliper.ibean.proxy.ProxyIBeanFactory.Builder;

import com.squareup.javapoet.CodeBlock;

/**
 * {@code BeanStyleHandler}s are used by the {@link ProxyIBeanFactory} to deal
 * with {@link BeanStyle}s that differ from the classic bean style in their
 * runtime behavior.
 * <p>
 * {@code BeanStyleHandlers}s are necessary in two cases:
 * <ul>
 * <li>Setter methods of a bean style do not return {@code void}.</li>
 * <li>Getter methods of a bean style return a type that does not match to the
 * type of the corresponding field.</li>
 * </ul>
 * <p>
 * If you want to provide your own bean style and at least one of the two
 * conditions above match to your custom bean style then you need to also
 * provide a {@code BeanStyleHandler}. Style and its matching handler are
 * configured when assembling a {@link ProxyIBeanFactory} using method
 * {@link Builder#withBeanStyle(BeanStyle, BeanStyleSpecificCodeGenerator)}.
 * 
 * @author alex@coliper.org
 */
public interface BeanStyleSpecificCodeGenerator {

    /**
     * <code>BeanStyleHandler</code> for {@link ClassicBeanStyle}.
     */
    BeanStyleSpecificCodeGenerator CLASSIC = new BeanStyleSpecificCodeGenerator() {
    };

    /**
     * <code>BeanStyleHandler</code> for
     * {@link ClassicBeanStyleWithOptionalSupport}.
     */
    BeanStyleSpecificCodeGenerator CLASSIC_WITH_OPTIONAL_SUPPORT =
            new BeanStyleSpecificCodeGenerator() {

                @Override
                public CodeBlock createGetterEndBlock(IBeanFieldMetaInfo fieldMeta) {
                    if (fieldMeta.hasOptionalSupport()) {
                        return CodeBlock.builder().addStatement("return Optional.ofNullable($L)",
                                CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME).build();
                    } else {
                        return BeanStyleSpecificCodeGenerator.super.createGetterEndBlock(fieldMeta);
                    }
                }

            };

    /**
     * <code>BeanStyleHandler</code> for {@link ModernBeanStyle}.
     */
    BeanStyleSpecificCodeGenerator MODERN = new BeanStyleSpecificCodeGenerator() {

        @Override
        public CodeBlock createSetterEndBlock(IBeanFieldMetaInfo fieldMeta) {
            return CodeBlock.builder().addStatement("return this").build();
        }

        @Override
        public CodeBlock createGetterEndBlock(IBeanFieldMetaInfo fieldMeta) {
            if (fieldMeta.hasOptionalSupport()) {
                return CodeBlock.builder().addStatement("return Optional.of($L)",
                        CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME).build();
            } else {
                return BeanStyleSpecificCodeGenerator.super.createGetterEndBlock(fieldMeta);
            }
        }

    };

    default CodeBlock createSetterEndBlock(IBeanFieldMetaInfo fieldMeta) {
        return CodeBlock.builder().build();
    }

    default CodeBlock createGetterEndBlock(IBeanFieldMetaInfo fieldMeta) {
        return CodeBlock.builder()
                .addStatement("return $L", CommonCodeSnippets.TEMP_VALUE_VARIABLE_NAME).build();
    }

}
