/**
 * 
 */
package org.coliper.ibean.codegen;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.AbstractBeanHashCodeTest;

public class BeanHashCodeTest extends AbstractBeanHashCodeTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return CodegenFactoryUtil.factoryWithStyle(style);
    }
}
