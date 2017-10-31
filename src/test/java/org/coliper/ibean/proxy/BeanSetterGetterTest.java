/**
 * 
 */
package org.coliper.ibean.proxy;

import static org.junit.Assert.assertNotNull;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.BeanTestUtil;
import org.coliper.ibean.EmptyBean;
import org.coliper.ibean.EmptyBeanImpl;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanClassic;
import org.coliper.ibean.SampleBeanClassicImpl;
import org.coliper.ibean.SampleBeanModern;
import org.coliper.ibean.SampleBeanModernImpl;
import org.junit.Test;

public class BeanSetterGetterTest {
    private IBeanFactory factory;

    /**
     * @param factory
     */
    public BeanSetterGetterTest() {
        this.factory = ProxyIBeanFactory.builder().build();
    }
    
    private void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }

    @Test
    public void testEmptyBeanDefaultValues() {
        EmptyBeanImpl expected = new EmptyBeanImpl();
        EmptyBean bean = this.factory.create(EmptyBean.class);
        BeanTestUtil.assertEqualsBean(EmptyBean.class, BeanStyle.CLASSIC, expected, bean);
    }

    @Test
    public void testPrimitivesBeanClassicDefaultValues() {
        PrimitivesBeanClassicImpl expected = new PrimitivesBeanClassicImpl();
        PrimitivesBeanClassic bean = this.factory.create(PrimitivesBeanClassic.class);
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, expected,
                bean);
    }

    @Test
    public void testSampleBeanClassicDefaultValues() {
        SampleBeanClassicImpl expected = new SampleBeanClassicImpl();
        SampleBeanClassic bean = this.factory.create(SampleBeanClassic.class);
        BeanTestUtil.assertEqualsBean(SampleBeanClassic.class, BeanStyle.CLASSIC, expected, bean);
    }

    @Test
    public void testSampleBeanModernDefaultValues() {
        this.switchToModernStyleBuilder();
        SampleBeanModernImpl expected = new SampleBeanModernImpl();
        SampleBeanModern bean = this.factory.create(SampleBeanModern.class);
        BeanTestUtil.assertEqualsBean(SampleBeanModern.class, BeanStyle.MODERN, expected, bean);
    }

    @Test
    public void testSampleBeanClassicWithValues() throws Exception {
        SampleBeanClassicImpl expected = new SampleBeanClassicImpl().fillWithTestValues();
        SampleBeanClassic bean = this.factory.create(SampleBeanClassic.class);
        assertNotNull(bean);

        expected.copyTo(bean);
        BeanTestUtil.assertEqualsBean(SampleBeanClassic.class, BeanStyle.CLASSIC, expected, bean);
        
        expected.fillWithNullValues();
        expected.copyTo(bean);
        BeanTestUtil.assertEqualsBean(SampleBeanClassic.class, BeanStyle.CLASSIC, expected, bean);
    }

    @Test
    public void testSampleBeanModernWithValues() throws Exception {
        this.switchToModernStyleBuilder();
        SampleBeanModernImpl expected = new SampleBeanModernImpl().fillWithTestValues();
        SampleBeanModern bean = this.factory.create(SampleBeanModern.class);
        assertNotNull(bean);

        expected.copyTo(bean);
        BeanTestUtil.assertEqualsBean(SampleBeanModern.class, BeanStyle.MODERN, expected, bean);
        
        expected.fillWithNullValues();
        expected.copyTo(bean);
        BeanTestUtil.assertEqualsBean(SampleBeanModern.class, BeanStyle.MODERN, expected, bean);
    }

    @Test
    public void testPrimitivesBeanClassicWithValues() throws Exception {
        PrimitivesBeanClassicImpl expected = new PrimitivesBeanClassicImpl().fillWithTestValues();
        PrimitivesBeanClassic bean = this.factory.create(PrimitivesBeanClassic.class);
        assertNotNull(bean);

        expected.copyTo(bean);
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, expected,
                bean);

        expected.fillWithNullValues();
        expected.copyTo(bean);
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, expected,
                bean);
    }
}
