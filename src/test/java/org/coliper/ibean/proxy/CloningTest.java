/**
 * 
 */
package org.coliper.ibean.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CloningTest {
    private IBeanFactory factory;

    /**
     * @param factory
     */
    public CloningTest() {
        this.factory = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
    }

    public static interface EmptyBeanCloneable
            extends EmptyBean, CloneableBean<EmptyBeanCloneable> {
    }

    @Test
    public void testEmptyBean() throws Exception {
        EmptyBeanCloneable bean = this.factory.create(EmptyBeanCloneable.class);
        EmptyBeanCloneable clone = bean.clone();
        assertThat(clone).isNotNull();
    }

    public static interface SampleBeanClassicCloneable
            extends SampleBeanClassic, CloneableBean<SampleBeanClassicCloneable> {
    }

    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassic expected = new SampleBeanClassicImpl().fillWithTestValues();
        SampleBeanClassicCloneable bean = this.factory.create(SampleBeanClassicCloneable.class);
        expected.copyTo(bean);
        SampleBeanClassic clone = bean.clone();
        BeanTestUtil.assertEqualsBean(SampleBeanClassic.class, BeanStyle.CLASSIC, expected, clone);
        clone.setString("slkdfjfff");
        assertThat(clone.getString()).isEqualTo("slkdfjfff");
    }

    public static interface PrimitivesBeanClassicCloneable
            extends PrimitivesBeanClassic, CloneableBean<PrimitivesBeanClassicCloneable> {
    }

    @Test
    public void testPrimitivesBeanClassic() throws Exception {
        PrimitivesBeanClassic expected = new PrimitivesBeanClassicImpl().fillWithTestValues();
        PrimitivesBeanClassicCloneable bean =
                this.factory.create(PrimitivesBeanClassicCloneable.class);
        expected.copyTo(bean);
        PrimitivesBeanClassic clone = bean.clone();
        BeanTestUtil.assertEqualsBean(PrimitivesBeanClassic.class, BeanStyle.CLASSIC, expected,
                clone);
        clone.setIntPrimitive(-77);
        assertThat(clone.getIntPrimitive()).isEqualTo(-77);
    }

    public static interface NestedBean extends CloneableBean<NestedBean> {
        SampleBeanClassicCloneable getSample();

        void setSample(SampleBeanClassicCloneable s);

        List<PrimitivesBeanClassicCloneable> getList();

        void setList(List<PrimitivesBeanClassicCloneable> l);
    }

    @Test
    public void testDeepClone() throws Exception {
        NestedBean nestedOrig = this.factory.create(NestedBean.class);
        PrimitivesBeanClassicCloneable prim1Orig =
                this.factory.create(PrimitivesBeanClassicCloneable.class);
        new PrimitivesBeanClassicImpl().fillWithTestValues().copyTo(prim1Orig);
        PrimitivesBeanClassicCloneable prim2Orig =
                this.factory.create(PrimitivesBeanClassicCloneable.class);
        new PrimitivesBeanClassicImpl().fillWithNullValues().copyTo(prim2Orig);
        SampleBeanClassicCloneable smplOrig = this.factory.create(SampleBeanClassicCloneable.class);
        new SampleBeanClassicImpl().fillWithTestValues().copyTo(smplOrig);
        nestedOrig.setSample(smplOrig);
        nestedOrig.setList(Lists.newArrayList(prim1Orig, null, prim2Orig));

        NestedBean nestedCopy = nestedOrig.deepClone();

        assertThat(nestedCopy).isNotNull();
        assertThat(nestedCopy).isNotSameAs(nestedOrig);
        assertThat(nestedCopy.getSample()).isNotSameAs(nestedOrig.getSample());
        assertThat(nestedCopy.getSample()).isEqualTo(nestedOrig.getSample());
        assertThat(nestedCopy.getList()).isNotSameAs(nestedOrig.getList());
        assertThat(nestedCopy.getList()).isEqualTo(nestedOrig.getList());
        for (int i = 0; i < nestedOrig.getList().size(); i++) {
            if (nestedOrig.getList().get(i) != null) {
                assertThat(nestedCopy.getList().get(i)).isNotSameAs(nestedOrig.getList().get(i));
                assertThat(nestedCopy.getList().get(i)).isEqualTo(nestedOrig.getList().get(i));
            } else {
                assertThat(nestedCopy.getList().get(i)).isNull();
            }
        }
    }

    @Test
    public void testDeepCloneWithImmutableList() throws Exception {
        NestedBean nestedOrig = this.factory.create(NestedBean.class);
        PrimitivesBeanClassicCloneable prim1Orig =
                this.factory.create(PrimitivesBeanClassicCloneable.class);
        nestedOrig.setList(ImmutableList.of(prim1Orig));

        NestedBean nestedCopy = nestedOrig.deepClone();

        assertThat(nestedCopy).isNotNull();
        assertThat(nestedCopy).isNotSameAs(nestedOrig);
        assertThat(nestedCopy.getList()).isSameAs(nestedOrig.getList());
    }
}
