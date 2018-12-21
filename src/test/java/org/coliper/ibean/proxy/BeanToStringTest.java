/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.factory.AbstractBeanToStringTest;

public class BeanToStringTest extends AbstractBeanToStringTest {

    /**
     * @param factory
     */
    public BeanToStringTest() {
        super(ProxyIBeanFactory.builder().build());
    }

    protected void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }

}
