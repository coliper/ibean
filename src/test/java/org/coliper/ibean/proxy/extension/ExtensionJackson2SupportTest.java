package org.coliper.ibean.proxy.extension;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.factory.extension.AbstractExtensionJackson2SupportTest;
import org.coliper.ibean.proxy.ProxyIBeanFactory;

public class ExtensionJackson2SupportTest extends AbstractExtensionJackson2SupportTest {
    @Override
    protected ProxyIBeanFactory createBeanFactory(BeanStyle beanStyle) {
        return ProxyIBeanFactory.builder().withDefaultInterfaceSupport().withBeanStyle(beanStyle)
                .build();
    }
}
