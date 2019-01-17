/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.AbstractBeanEqualsTest;

public class BeanEqualsTest extends AbstractBeanEqualsTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return ProxyFactoryUtil.factoryWithStyle(style);
    }
}
