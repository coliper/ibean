/**
 * 
 */
package org.coliper.ibean.proxy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coliper.ibean.BeanStyle;
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

public class BeanEqualsTest {
    private IBeanFactory factory;

    /**
     * @param factory
     */
    public BeanEqualsTest() {
        this.factory = ProxyIBeanFactory.builder().build();
    }

    private void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }

    @Test
    public void testEmptyBean() {
        EmptyBeanImpl regularBean = new EmptyBeanImpl();
        EmptyBean bean1 = this.factory.create(EmptyBean.class);
        EmptyBean bean2 = this.factory.create(EmptyBean.class);
        assertTrue(bean1.equals(bean2));
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));
    }

    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassicImpl regularBean = new SampleBeanClassicImpl();
        SampleBeanClassic bean1 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean2 = this.factory.create(SampleBeanClassic.class);

        assertTrue(bean1.equals(bean2)); // initially both beans are empty:
                                         // beans equal
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        regularBean.fillWithTestValues();

        regularBean.copyTo(bean1); // only bean1 has new values: beans differ
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        regularBean.copyTo(bean2); // bean2 now also has new values: beans equal
        assertTrue(bean1.equals(bean2));
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        bean1.setString("other"); // change bean1 again: beans differ
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));
    }

    @Test
    public void testSampleBeanModern() throws Exception {
        this.switchToModernStyleBuilder();
        SampleBeanModernImpl regularBean = new SampleBeanModernImpl();
        SampleBeanModern bean1 = this.factory.create(SampleBeanModern.class);
        SampleBeanModern bean2 = this.factory.create(SampleBeanModern.class);

        assertTrue(bean1.equals(bean2)); // initially both beans are empty:
                                         // beans equal
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        regularBean.fillWithTestValues();

        regularBean.copyTo(bean1); // only bean1 has new values: beans differ
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        regularBean.copyTo(bean2); // bean2 now also has new values: beans equal
        assertTrue(bean1.equals(bean2));
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        bean1.string("other"); // change bean1 again: beans differ
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));
    }

    @Test
    public void testPrimitivesBeanClassicWithValues() throws Exception {
        PrimitivesBeanClassicImpl regularBean = new PrimitivesBeanClassicImpl();
        PrimitivesBeanClassic bean1 = this.factory.create(PrimitivesBeanClassic.class);
        PrimitivesBeanClassic bean2 = this.factory.create(PrimitivesBeanClassic.class);

        assertTrue(bean1.equals(bean2)); // initially both beans are empty:
                                         // beans equal
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        regularBean.fillWithTestValues();

        regularBean.copyTo(bean1); // only bean1 has new values: beans differ
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        regularBean.copyTo(bean2); // bean2 now also has new values: beans equal
        assertTrue(bean1.equals(bean2));
        assertTrue(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));

        bean1.setIntPrimitive(666); // change bean1 again: beans differ
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertFalse(bean1.equals(regularBean));
    }

    public static interface Base {
        int getY();

        void setY(int i);
    }

    public static interface A extends Base {
        int getX();

        void setX(int i);
    }

    public static interface B extends Base {
        int getX();

        void setX(int i);
    }

    @Test
    public void testDifferentTypes() {
        A beanA = this.factory.create(A.class);
        B beanB = this.factory.create(B.class);
        assertFalse(beanA.equals(beanB));
        assertFalse(beanB.equals(beanA));

        beanA.setX(13);
        beanB.setX(13);
        assertFalse(beanA.equals(beanB));
        assertFalse(beanB.equals(beanA));
    }

    @Test
    public void testBaseTypes() {
        A beanA = this.factory.create(A.class);
        Base beanB = this.factory.create(Base.class);
        assertFalse(beanA.equals(beanB));
        assertFalse(beanB.equals(beanA));

        beanA.setY(13);
        beanB.setY(13);
        assertFalse(beanA.equals(beanB));
        assertFalse(beanB.equals(beanA));
    }

    @Test
    public void testCycle() throws Exception {
        SampleBeanClassic bean1 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean2 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean3 = this.factory.create(SampleBeanClassic.class);

        // one bean cycle (1) <-> (1) , (2) <-> (2)
        bean1.setSelf(bean1);
        bean2.setSelf(bean2);
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertTrue(bean1.equals(bean1));
        assertTrue(bean2.equals(bean2));

        // two bean cycle (1) <-> (2)
        bean2.setSelf(bean1);
        bean1.setSelf(bean2);
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertTrue(bean1.equals(bean1));
        assertTrue(bean2.equals(bean2));

        // three bean cycle (1) -> (2) -> (3) -> (1)
        bean1.setSelf(bean2);
        bean2.setSelf(bean3);
        bean3.setSelf(bean1);
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertTrue(bean1.equals(bean1));
        assertTrue(bean2.equals(bean2));

        // indirect cycle (1) -> (2) <-> (3)
        bean1.setSelf(bean2);
        bean2.setSelf(bean3);
        bean3.setSelf(bean2);
        assertFalse(bean1.equals(bean2));
        assertFalse(bean2.equals(bean1));
        assertTrue(bean1.equals(bean1));
        assertTrue(bean2.equals(bean2));

        // symmetric cycle (1) -> (3) <-> (3) <- (2)
        bean1.setSelf(bean3);
        bean2.setSelf(bean3);
        bean3.setSelf(bean3);
        assertTrue(bean1.equals(bean2));
        assertTrue(bean2.equals(bean1));
        assertTrue(bean1.equals(bean1));
        assertTrue(bean2.equals(bean2));

    }
}
