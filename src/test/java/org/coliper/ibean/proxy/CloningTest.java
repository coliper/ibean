/**
 * 
 */
package org.coliper.ibean.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.BeanTestUtil;
import org.coliper.ibean.EmptyBean;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanClassic;
import org.coliper.ibean.SampleBeanClassicImpl;
import org.junit.Test;

public class CloningTest {
    private IBeanFactory factory;

    /**
     * @param factory
     */
    public CloningTest() {
        this.factory = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
    }

    @Test
    public void testEmptyBean() throws Exception {
        EmptyBean bean = this.factory.create(EmptyBean.class);
        EmptyBean clone = bean.clone();
        assertThat(clone).isNotNull();
    }
    
    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassicImpl expected = new SampleBeanClassicImpl().fillWithTestValues();
        SampleBeanClassic bean = this.factory.create(SampleBeanClassic.class);
        expected.copyTo(bean);
        SampleBeanClassic clone = bean.clone();
        BeanTestUtil.assertEqualsBean(SampleBeanClassic.class, BeanStyle.CLASSIC, expected, clone);
        clone.setString("slkdfjfff");
        assertThat(clone.getString()).isEqualTo("slkdfjfff");
    }
    
    @Test
    public void testPrimitivesBeanClassic() throws Exception {
        PrimitivesBeanClassicImpl expected = new PrimitivesBeanClassicImpl().fillWithTestValues();
        PrimitivesBeanClassic bean = this.factory.create(PrimitivesBeanClassic.class);
        expected.copyTo(bean);
        PrimitivesBeanClassic clone = bean.clone();
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, expected, clone);
        clone.setInt(-77);
        assertThat(clone.getInt()).isEqualTo(-77);
    }
}
