/**
 * 
 */
package org.coliper.ibean.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.coliper.ibean.EmptyBean;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanClassic;
import org.coliper.ibean.SampleBeanClassicImpl;
import org.coliper.ibean.SampleBeanModern;
import org.coliper.ibean.SampleBeanModernImpl;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

public abstract class AbstractBeanToStringTest extends AbstractFactoryTest {

    @Test
    public void testEmptyBean() {
        EmptyBean bean = this.factory.create(EmptyBean.class);
        String expected;
        expected = "EmptyBean[]";
        assertEquals(expected, bean.toString());
    }

    @Test
    public void testSampleBeanClassic() throws Exception {
        SampleBeanClassic regularBean = new SampleBeanClassicImpl().fillWithTestValues();

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
        SampleBeanModern regularBean = new SampleBeanModernImpl().fillWithTestValues();

        SampleBeanModern bean = this.factoryModern.create(SampleBeanModern.class);
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
        PrimitivesBeanClassic regularBean = new PrimitivesBeanClassicImpl().fillWithTestValues();
        PrimitivesBeanClassic bean = this.factory.create(PrimitivesBeanClassic.class);
        String expected;
        //@formatter:off
        expected = "PrimitivesBeanClassic["
                + "booleanObject=<null>,booleanPrimitive=false,"
                + "byteObject=<null>,bytePrimitive=0,"
                + "charObject=<null>,charPrimitive=\u0000,"
                + "doubleObject=<null>,doublePrimitive=0.0,"
                + "floatObject=<null>,floatPrimitive=0.0,"
                + "intObject=<null>,intPrimitive=0,"
                + "longObject=<null>,longPrimitive=0,"
                + "shortObject=<null>,shortPrimitive=0]";
        //@formatter:on
        assertEquals(expected, bean.toString());

        regularBean.copyTo(bean);
        //@formatter:off
        expected = "PrimitivesBeanClassic["
                + "booleanObject=true,booleanPrimitive=true,"
                + "byteObject=127,bytePrimitive=-128,"
                + "charObject=@,charPrimitive=x,"
                + "doubleObject=1.7976931348623157E308,"
                + "doublePrimitive=2.2250738585072014E-308,"
                + "floatObject=1.4E-45,floatPrimitive=3.4028235E38,"
                + "intObject=-1,intPrimitive=-2147483648,"
                + "longObject=-9223372036854775808,longPrimitive=9223372036854775807,"
                + "shortObject=-32768,shortPrimitive=32767]";
        //@formatter:on
        assertEquals(expected, bean.toString());
    }

    @Test
    public void testDifferentToStringStyle() throws Exception {
        final String NL = System.lineSeparator();
        IBeanFactory otherFactory = ProxyIBeanFactory.builder()
                .withToStringStyle(ToStringStyle.MULTI_LINE_STYLE).build();

        SampleBeanClassic bean = otherFactory.create(SampleBeanClassic.class);
        //@formatter:off
        String expected = "org.coliper.ibean.SampleBeanClassic@[0-9a-f]+\\[" + NL +
                "  booleanPrimitive=false" + NL +
                "  date=<null>" + NL +
                "  intObject=<null>" + NL +
                "  self=<null>" + NL +
                "  string=<null>" + NL + 
                "\\]";
        //@formatter:on
        assertThat(bean.toString()).matches(expected);

        bean.fillWithTestValues();
        //@formatter:off
        expected = "org.coliper.ibean.SampleBeanClassic@[0-9a-f]+\\[" + NL +
                "  booleanPrimitive=true" + NL +
                "  date=Thu Jan 01 01:00:00 CET 1970" + NL +
                "  intObject=2147483647" + NL +
                "  self=org.coliper.ibean.SampleBeanClassicImpl@[0-9a-f]+" + NL +
                "  string=dummy 2134452" + NL + 
                "\\]";
        //@formatter:on
        assertThat(bean.toString()).matches(expected);
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
