/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.factory.AbstractBeanHashCodeTest;

public class BeanHashCodeTest extends AbstractBeanHashCodeTest {
    /**
     * @param factory
     */
    public BeanHashCodeTest() {
        super(ProxyIBeanFactory.builder().build());
    }

    protected void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }
}
