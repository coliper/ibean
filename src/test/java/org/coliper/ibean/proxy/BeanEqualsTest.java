/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.factory.AbstractBeanEqualsTest;

public class BeanEqualsTest extends AbstractBeanEqualsTest {
    /**
     * @param factory
     */
    public BeanEqualsTest() {
        super(ProxyIBeanFactory.builder().build());
    }

    protected void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }
}
