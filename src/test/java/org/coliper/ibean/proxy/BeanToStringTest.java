/**
 * 
 */
package org.coliper.ibean.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.EmptyBean;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanClassic;
import org.coliper.ibean.SampleBeanClassicImpl;
import org.coliper.ibean.SampleBeanModern;
import org.coliper.ibean.SampleBeanModernImpl;
import org.junit.Test;

public class BeanToStringTest {
    private IBeanFactory factory;

    /**
     * @param factory
     */
    public BeanToStringTest() {
        this.factory = ProxyIBeanFactory.builder().build();
    }
    
    private void switchToModernStyleBuilder() {
        this.factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
    }

    @Test
    public void testEmptyBean() {
        EmptyBean bean = this.factory.create(EmptyBean.class);
        String expected;
        expected = "EmptyBean[]";
        assertEquals(expected, bean.toString());
    }

    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassicImpl regularBean = new SampleBeanClassicImpl().fillWithTestValues();

        SampleBeanClassic bean = this.factory.create(SampleBeanClassic.class);
        //@formatter:off
        String expected = "SampleBeanClassic["
                + "booleanPrimitive=false,"
                + "date=<null>,"
                + "intObject=<null>,"
                + "self=<null>,"
                + "string=<null>]";
        //@formatter:on
        assertEquals(expected, bean.toString());

        regularBean.copyTo(bean);
        //@formatter:off
        expected = "SampleBeanClassic\\["
                + "booleanPrimitive=true,"
                + "date=Thu Jan 01 01:00:00 CET 1970,"
                + "intObject=2147483647,"
                + "self=org.coliper.ibean.SampleBeanClassicImpl@[0-9a-z]+,"
                + "string=dummy 2134452\\]";
        //@formatter:on
        final String string = bean.toString();
        assertTrue("does not match: " + string, string.matches(expected));
    }

    @Test
    public void testSampleBeanModern() throws Exception {
        this.switchToModernStyleBuilder();
        SampleBeanModernImpl regularBean = new SampleBeanModernImpl().fillWithTestValues();

        SampleBeanModern bean = this.factory.create(SampleBeanModern.class);
        //@formatter:off
        String expected = "SampleBeanModern["
                + "booleanPrimitive=false,"
                + "date=<null>,"
                + "intObject=<null>,"
                + "self=<null>,"
                + "string=<null>]";
        //@formatter:on
        assertEquals(expected, bean.toString());

        regularBean.copyTo(bean);
        //@formatter:off
        expected = "SampleBeanModern\\["
                + "booleanPrimitive=true,"
                + "date=Thu Jan 01 01:00:00 CET 1970,"
                + "intObject=2147483647,"
                + "self=org.coliper.ibean.SampleBeanModernImpl@[0-9a-z]+,"
                + "string=dummy 2134452\\]";
        //@formatter:on
        final String string = bean.toString();
        assertTrue("does not match: " + string, string.matches(expected));
    }

    @Test
    public void testPrimitivesBeanClassic() throws Exception {
        PrimitivesBeanClassicImpl regularBean = new PrimitivesBeanClassicImpl()
                .fillWithTestValues();
        PrimitivesBeanClassic bean = this.factory.create(PrimitivesBeanClassic.class);
        String expected;
        //@formatter:off
        expected = "PrimitivesBeanClassic["
                + "boolean=false,booleanObject=<null>,"
                + "byte=0,byteObject=<null>,"
                + "char=\u0000,charObject=<null>,"
                + "double=0.0,doubleObject=<null>,"
                + "float=0.0,floatObject=<null>,"
                + "int=0,intObject=<null>,"
                + "long=0,longObject=<null>,"
                + "short=0,shortObject=<null>]";
        //@formatter:on
        assertEquals(expected, bean.toString());
        
        regularBean.copyTo(bean);
        //@formatter:off
        expected = "PrimitivesBeanClassic["
                + "boolean=true,booleanObject=true,"
                + "byte=-128,byteObject=127,"
                + "char=x,charObject=@,"
                + "double=2.2250738585072014E-308,doubleObject=1.7976931348623157E308,"
                + "float=-Infinity,floatObject=NaN,"
                + "int=-2147483648,intObject=-1,"
                + "long=9223372036854775807,longObject=-9223372036854775808,"
                + "short=32767,shortObject=-32768]";
        //@formatter:on
        assertEquals(expected, bean.toString());
    }

    @Test
    public void testDifferentToStringStyle() throws Exception {
        IBeanFactory otherFactory = ProxyIBeanFactory.builder()
                .withToStringStyle(ToStringStyle.JSON_STYLE).build();
        SampleBeanClassicImpl regularBean = new SampleBeanClassicImpl().fillWithTestValues();

        SampleBeanClassic bean = otherFactory.create(SampleBeanClassic.class);
        //@formatter:off
        String expected = "{\"booleanPrimitive\":false,"
                + "\"date\":null,"
                + "\"intObject\":null,"
                + "\"self\":null,"
                + "\"string\":null}";
        //@formatter:on
        assertEquals(expected, bean.toString());

        regularBean.copyTo(bean);
        //@formatter:off
        expected = "\\{\"booleanPrimitive\":true,"
                + "\"date\":\"Thu Jan 01 01:00:00 CET 1970\","
                + "\"intObject\":2147483647,"
                + "\"self\":\"org\\.coliper\\.ibean\\.SampleBeanClassicImpl@[0-9a-z]+\","
                + "\"string\":\"dummy 2134452\"\\}";
        //@formatter:on
        final String string = bean.toString();
        assertTrue("does not match: " + string, string.matches(expected));
    }

    @Test
    public void testCycle() throws Exception {
        // TODO: should not contain cryptic proxy for cycles
        SampleBeanClassic bean1 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean2 = this.factory.create(SampleBeanClassic.class);
        SampleBeanClassic bean3 = this.factory.create(SampleBeanClassic.class);
        String expected;
        //@formatter:off
        expected = "SampleBeanClassic\\["
                + "booleanPrimitive=false,"
                + "date=<null>,"
                + "intObject=<null>,"
                + "self=[^,]+,"
                + "string=<null>\\]";
        //@formatter:on
        String actual;

        // one bean cycle
        bean1.setSelf(bean1);
        actual = bean1.toString();
        assertTrue("not matching: " + actual, actual.matches(expected));

        // two bean cycle
        bean2.setSelf(bean1);
        bean1.setSelf(bean2);
        actual = bean1.toString();
        //@formatter:off
        expected = "SampleBeanClassic\\["
                + "booleanPrimitive=false,"
                + "date=<null>,"
                + "intObject=<null>,"
                + "self=SampleBeanClassic\\["
                    + "booleanPrimitive=false,"
                    + "date=<null>,"
                    + "intObject=<null>,"
                    + "self=[^,]+,"
                    + "string=<null>\\],"
                + "string=<null>\\]";
        //@formatter:on
        assertTrue("not matching: " + actual, actual.matches(expected));

        // three bean cycle
        bean2.setSelf(bean3);
        bean3.setSelf(bean1);
        actual = bean1.toString();
        //@formatter:off
        expected = "SampleBeanClassic\\["
                + "booleanPrimitive=false,"
                + "date=<null>,"
                + "intObject=<null>,"
                + "self=SampleBeanClassic\\["
                    + "booleanPrimitive=false,"
                    + "date=<null>,"
                    + "intObject=<null>,"
                    + "self=SampleBeanClassic\\["
                        + "booleanPrimitive=false,"
                        + "date=<null>,"
                        + "intObject=<null>,"
                        + "self=[^,]+,"
                        + "string=<null>\\],"
                    + "string=<null>\\],"
                + "string=<null>\\]";
        //@formatter:on
        assertTrue("not matching: " + actual, actual.matches(expected));

        // indirect cycle
        bean2.setSelf(bean3);
        bean3.setSelf(bean2);
        actual = bean1.toString();
        // expected is same as at "three bean circle" above
        assertTrue("not matching: " + actual, actual.matches(expected));
    }

}
