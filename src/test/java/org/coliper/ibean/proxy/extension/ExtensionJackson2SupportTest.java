package org.coliper.ibean.proxy.extension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.BeanTestUtil;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanModern;
import org.coliper.ibean.extension.Jackson2ModuleForIBeans;
import org.coliper.ibean.extension.Jackson2Support;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtensionJackson2SupportTest {

    final IBeanFactory factoryClassic =
            ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
    final ObjectMapper jackson2Classic = this.createObjectMapper(factoryClassic);
    final IBeanFactory factoryModern = ProxyIBeanFactory.builder().withDefaultInterfaceSupport()
            .withBeanStyle(BeanStyle.MODERN_WITH_OPTIONAL).build();
    final ObjectMapper jackson2Modern = this.createObjectMapper(factoryModern);

    public static interface PrimitivesBeanClassicJackson2
            extends PrimitivesBeanClassic, Jackson2Support {
    }

    public static interface SampleBeanModernJackson2 extends SampleBeanModern, Jackson2Support {
    }

    public static interface Nested extends Jackson2Support {
        SampleBeanModernJackson2 sample1();

        Nested sample1(SampleBeanModernJackson2 b);

        SampleBeanModernJackson2 sample2();

        Nested sample2(SampleBeanModernJackson2 b);

        SampleBeanModernJackson2 sample3();

        Nested sample3(SampleBeanModernJackson2 b);

        Nested date(Date d);

        Date date();
    }

    public ExtensionJackson2SupportTest() {
    }

    private ObjectMapper createObjectMapper(IBeanFactory factory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jackson2ModuleForIBeans(factory));
        return mapper;
    }

    @Test
    public void testPrimitivesTestValuesIBeanRoot() throws Exception {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root = this.factoryClassic.create(PrimitivesBeanClassicJackson2.class)
                .fillWithTestValues();
        json = this.jackson2Classic.writeValueAsString(root);
        PrimitivesBeanClassic copy =
                this.jackson2Classic.readValue(json, PrimitivesBeanClassicImpl.class);
        copy.assertEqual(root, requireNestedObjectsSame);
        copy = this.jackson2Classic.readValue(json, PrimitivesBeanClassicJackson2.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testPrimitivesTestValuesClassRoot() throws Exception {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root = new PrimitivesBeanClassicImpl().fillWithTestValues();
        json = this.jackson2Classic.writeValueAsString(root);
        PrimitivesBeanClassic copy =
                this.jackson2Classic.readValue(json, PrimitivesBeanClassicJackson2.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testPrimitivesNullValuesIBeanRoot() throws Exception {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root = this.factoryClassic.create(PrimitivesBeanClassicJackson2.class)
                .fillWithNullValues();
        json = this.jackson2Classic.writeValueAsString(root);
        PrimitivesBeanClassic copy =
                this.jackson2Classic.readValue(json, PrimitivesBeanClassicImpl.class);
        copy.assertEqual(root, requireNestedObjectsSame);
        copy = this.jackson2Classic.readValue(json, PrimitivesBeanClassicJackson2.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testPrimitivesNullValuesClassRoot() throws Exception {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root = new PrimitivesBeanClassicImpl().fillWithNullValues();
        json = this.jackson2Classic.writeValueAsString(root);
        PrimitivesBeanClassic copy =
                this.jackson2Classic.readValue(json, PrimitivesBeanClassicJackson2.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testModernStyleTestValues() throws Exception {
        final boolean requireNestedObjectsSame = false;
        String json;
        SampleBeanModern root = this.factoryModern.create(SampleBeanModernJackson2.class)
                .fillWithTestValues().self(null);
        json = this.jackson2Modern.writeValueAsString(root);
        assertThat(json).contains("\"booleanPrimitive\":true");
        assertThat(json).contains("\"date\":0");
        assertThat(json).contains("\"intObject\":2147483647");
        assertThat(json).contains("self\":null");
        assertThat(json).contains("\"string\":\"dummy 2134452\"");
        SampleBeanModern copy = this.jackson2Modern.readValue(json, SampleBeanModernJackson2.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testModernStyleNullValues() throws Exception {
        final boolean requireNestedObjectsSame = false;
        String json;
        SampleBeanModern root =
                this.factoryModern.create(SampleBeanModernJackson2.class).fillWithNullValues();
        json = this.jackson2Modern.writeValueAsString(root);
        assertThat(json).contains("\"booleanPrimitive\":false");
        assertThat(json).contains("\"date\":null");
        assertThat(json).contains("\"intObject\":0");
        assertThat(json).contains("self\":null");
        assertThat(json).contains("\"string\":null");
        SampleBeanModern copy = this.jackson2Modern.readValue(json, SampleBeanModernJackson2.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testNested() throws Exception {
        final boolean requireNestedObjectsSame = false;
        final Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
        String json;
        Nested root = this.factoryModern.create(Nested.class).date(now)
                .sample1(this.factoryModern.create(SampleBeanModernJackson2.class))
                .sample3(this.factoryModern.create(SampleBeanModernJackson2.class));
        json = this.jackson2Modern.writeValueAsString(root);
        Nested copy = this.jackson2Modern.readValue(json, Nested.class);
        BeanTestUtil.assertEqualsBean(Nested.class, BeanStyle.MODERN, root, copy,
                requireNestedObjectsSame);
    }
}
