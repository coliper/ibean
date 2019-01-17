package org.coliper.ibean.proxy.extension;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.extension.AbstractExtensionGsonSupportTest;
import org.coliper.ibean.proxy.ProxyFactoryUtil;

public class ExtensionGsonSupportTest extends AbstractExtensionGsonSupportTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return ProxyFactoryUtil.factoryWithStyle(style);
    }
}
