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
import org.coliper.ibean.extension.CloneableBean;
import org.junit.Test;

public class CloningTest {
    private IBeanFactory factory;

    /**
     * @param factory
     */
    public CloningTest() {
        this.factory = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
    }

    public static interface EmptyBeanCloneable extends EmptyBean, CloneableBean<EmptyBeanCloneable> {       
    }
    
    @Test
    public void testEmptyBean() throws Exception {
        EmptyBeanCloneable bean = this.factory.create(EmptyBeanCloneable.class);
        EmptyBeanCloneable clone = bean.clone();
        assertThat(clone).isNotNull();
    }

    public static interface SampleBeanClassicCloneable extends SampleBeanClassic, CloneableBean<SampleBeanClassicCloneable> {      
    }
    
    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassicImpl expected = new SampleBeanClassicImpl().fillWithTestValues();
        SampleBeanClassicCloneable bean = this.factory.create(SampleBeanClassicCloneable.class);
        expected.copyTo(bean);
        SampleBeanClassic clone = bean.clone();
        BeanTestUtil.assertEqualsBean(SampleBeanClassic.class, BeanStyle.CLASSIC, expected, clone);
        clone.setString("slkdfjfff");
        assertThat(clone.getString()).isEqualTo("slkdfjfff");
    }

    public static interface PrimitivesBeanClassicCloneable extends PrimitivesBeanClassic, CloneableBean<PrimitivesBeanClassicCloneable> {     
    }
    
    @Test
    public void testPrimitivesBeanClassic() throws Exception {
        PrimitivesBeanClassicImpl expected = new PrimitivesBeanClassicImpl().fillWithTestValues();
        PrimitivesBeanClassicCloneable bean = this.factory.create(PrimitivesBeanClassicCloneable.class);
        expected.copyTo(bean);
        PrimitivesBeanClassic clone = bean.clone();
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, expected, clone);
        clone.setInt(-77);
        assertThat(clone.getInt()).isEqualTo(-77);
    }
}
