package org.coliper.ibean.proxy.extension;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.factory.extension.AbstractExtensionGsonSupportTest;
import org.coliper.ibean.proxy.ProxyIBeanFactory;

public class ExtensionGsonSupportTest extends AbstractExtensionGsonSupportTest {
    @Override
    protected ProxyIBeanFactory createBeanFactory(BeanStyle beanStyle) {
        return ProxyIBeanFactory.builder().withDefaultInterfaceSupport().withBeanStyle(beanStyle)
                .build();
    }
}
