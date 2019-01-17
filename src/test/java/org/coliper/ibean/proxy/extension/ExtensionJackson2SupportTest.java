package org.coliper.ibean.proxy.extension;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.extension.AbstractExtensionJackson2SupportTest;
import org.coliper.ibean.proxy.ProxyFactoryUtil;

public class ExtensionJackson2SupportTest extends AbstractExtensionJackson2SupportTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return ProxyFactoryUtil.factoryWithStyle(style);
    }
}
