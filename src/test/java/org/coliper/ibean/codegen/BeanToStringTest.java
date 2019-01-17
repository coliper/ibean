/**
 * 
 */
package org.coliper.ibean.codegen;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.AbstractBeanToStringTest;

public class BeanToStringTest extends AbstractBeanToStringTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return CodegenFactoryUtil.factoryWithStyle(style);
    }
}
