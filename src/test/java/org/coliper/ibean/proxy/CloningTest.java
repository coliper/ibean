/**
 * 
 */
package org.coliper.ibean.proxy;

import org.coliper.ibean.factory.AbstractCloningTest;

public class CloningTest extends AbstractCloningTest {

    /**
     * @param factory
     */
    public CloningTest() {
        super(ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build());
    }

}
