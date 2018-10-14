/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.factory.AbstractBeanSetterGetterTest;

public class BeanSetterGetterTest extends AbstractBeanSetterGetterTest {
    /**
     * @param factory
     */
    public BeanSetterGetterTest() {
        super(ProxyIBeanFactory.builder().build());
    }

    protected void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }
}
