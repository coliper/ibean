/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.AbstractBeanHashCodeTest;

public class BeanHashCodeTest extends AbstractBeanHashCodeTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return ProxyFactoryUtil.factoryWithStyle(style);
    }
}
