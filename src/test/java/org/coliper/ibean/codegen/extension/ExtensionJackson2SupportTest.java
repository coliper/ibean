package org.coliper.ibean.codegen.extension;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.codegen.CodegenFactoryUtil;
import org.coliper.ibean.factory.extension.AbstractExtensionJackson2SupportTest;

public class ExtensionJackson2SupportTest extends AbstractExtensionJackson2SupportTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return CodegenFactoryUtil.factoryWithStyle(style);
    }
}
