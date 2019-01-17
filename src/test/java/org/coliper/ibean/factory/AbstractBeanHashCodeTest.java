/**
 * 
 */
package org.coliper.ibean.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.coliper.ibean.BeanTypeWithCustomEquals;
import org.coliper.ibean.EmptyBean;
import org.coliper.ibean.IBean;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanClassic;
import org.coliper.ibean.SampleBeanClassicImpl;
import org.coliper.ibean.SampleBeanModern;
import org.coliper.ibean.SampleBeanModernImpl;
import org.junit.Test;

public abstract class AbstractBeanHashCodeTest extends AbstractFactoryTest {

    @Test
    public void testEmptyBean() {
        EmptyBean bean1 = this.factory.create(EmptyBean.class);
        EmptyBean bean2 = this.factory.create(EmptyBean.class);
        assertTrue(bean1.hashCode() == bean2.hashCode());
    }

    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassic regularBean = new SampleBeanClassicImpl().fillWithTestValues();

        SampleBeanClassic bean1 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean2 = this.factory.create(SampleBeanClassic.class);
        assertTrue(bean1.hashCode() == bean2.hashCode());

        regularBean.copyTo(bean1); // only bean1 has new values: beans differ
        // Theoretically hash-code could be same but we assume that
        // hash-algorithm is good enough
        assertTrue(bean1.hashCode() != bean2.hashCode());

        regularBean.copyTo(bean2); // bean2 now also has new values: beans equal
        assertTrue(bean1.hashCode() == bean2.hashCode());
    }

    @Test
    public void testSampleBeanModern() throws Exception {
        SampleBeanModern regularBean = new SampleBeanModernImpl().fillWithTestValues();

        SampleBeanModern bean1 = this.factoryModern.create(SampleBeanModern.class);
        SampleBeanModern bean2 = this.factoryModern.create(SampleBeanModern.class);
        assertTrue(bean1.hashCode() == bean2.hashCode());

        regularBean.copyTo(bean1); // only bean1 has new values: beans differ
        // Theoretically hash-code could be same but we assume that
        // hash-algorithm is good enough
        assertTrue(bean1.hashCode() != bean2.hashCode());

        regularBean.copyTo(bean2); // bean2 now also has new values: beans equal
        assertTrue(bean1.hashCode() == bean2.hashCode());
    }

    @Test
    public void testPrimitivesBeanClassicWithValues() throws Exception {
        PrimitivesBeanClassic regularBean = new PrimitivesBeanClassicImpl().fillWithTestValues();
        PrimitivesBeanClassic bean1 = this.factory.create(PrimitivesBeanClassic.class);
        PrimitivesBeanClassic bean2 = this.factory.create(PrimitivesBeanClassic.class);

        assertTrue(bean1.hashCode() == bean2.hashCode());

        regularBean.copyTo(bean1); // only bean1 has new values: beans differ
        // Theoretically hash-code could be same but we assume that
        // hash-algorithm is good enough
        assertTrue(bean1.hashCode() != bean2.hashCode());

        regularBean.copyTo(bean2); // bean2 now also has new values: beans equal
        assertTrue(bean1.hashCode() == bean2.hashCode());
    }

    @Test
    public void testCycle() throws Exception {
        SampleBeanClassic bean1 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean2 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean3 = this.factory.create(SampleBeanClassic.class);

        // one bean cycle (1) <-> (1) , (2) <-> (2)
        bean1.setSelf(bean1);
        bean2.setSelf(bean2);
        bean1.hashCode(); // we just check that it is not crashing and do not
                          // care about the result

        // two bean cycle (1) <-> (2)
        bean2.setSelf(bean1);
        bean1.setSelf(bean2);
        bean1.hashCode(); // we just check that it is not crashing and do not
                          // care about the result

        // three bean cycle (1) -> (2) -> (3) -> (1)
        bean1.setSelf(bean2);
        bean2.setSelf(bean3);
        bean3.setSelf(bean1);
        bean1.hashCode(); // we just check that it is not crashing and do not
                          // care about the result

        // indirect cycle (1) -> (2) <-> (3)
        bean1.setSelf(bean2);
        bean2.setSelf(bean3);
        bean3.setSelf(bean2);
        bean1.hashCode(); // we just check that it is not crashing and do not
                          // care about the result

        // symmetric cycle (1) -> (3) <-> (3) <- (2)
        bean1.setSelf(bean3);
        bean2.setSelf(bean3);
        bean3.setSelf(bean3);
        assertEquals(bean1.hashCode(), bean2.hashCode());
    }

    @Test
    public void testCustomHashCode() {
        BeanTypeWithCustomEquals bean1 = IBean.newOf(BeanTypeWithCustomEquals.class);
        bean1.setId("#1");
        bean1.setName("John");

        BeanTypeWithCustomEquals bean2 = IBean.newOf(BeanTypeWithCustomEquals.class);
        bean2.setId("#2");
        bean2.setName("John");

        assertThat(bean1.hashCode() == bean2.hashCode()).isFalse();

        bean2.setId("#1");
        bean2.setName("Mary");
        assertThat(bean1.hashCode() == bean2.hashCode()).isTrue();
    }

}
