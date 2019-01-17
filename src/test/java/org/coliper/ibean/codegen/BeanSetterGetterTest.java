/**
 * 
 */
package org.coliper.ibean.codegen;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.factory.AbstractBeanSetterGetterTest;

public class BeanSetterGetterTest extends AbstractBeanSetterGetterTest {
    @Override
    protected IBeanFactory createBeanFactory(BeanStyle style) {
        return CodegenFactoryUtil.factoryWithStyle(style);
    }
}
