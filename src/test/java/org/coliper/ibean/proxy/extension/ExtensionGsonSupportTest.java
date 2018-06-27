package org.coliper.ibean.proxy.extension;

import java.util.Date;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.BeanTestUtil;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
import org.coliper.ibean.SampleBeanModern;
import org.coliper.ibean.SampleBeanModernImpl;
import org.coliper.ibean.extension.GsonSerializerDeserializerForIBeans;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExtensionGsonSupportTest {

    final IBeanFactory factoryClassic =
            ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
    final Gson gsonClassic = new GsonBuilder().registerTypeHierarchyAdapter(GsonSupport.class,
            new GsonSerializerDeserializerForIBeans(factoryClassic)).create();
    final IBeanFactory factoryModern = ProxyIBeanFactory.builder().withDefaultInterfaceSupport()
            .withBeanStyle(BeanStyle.MODERN).build();
    final Gson gsonModern = new GsonBuilder().registerTypeHierarchyAdapter(GsonSupport.class,
            new GsonSerializerDeserializerForIBeans(factoryModern)).create();

    public static interface PrimitivesBeanClassicGson extends PrimitivesBeanClassic, GsonSupport {
    }

    public static interface SampleBeanModernGson extends SampleBeanModern, GsonSupport {
    }

    public static interface Nested extends GsonSupport {
        SampleBeanModernGson sample1();

        Nested sample1(SampleBeanModernGson b);

        SampleBeanModernGson sample2();

        Nested sample2(SampleBeanModernGson b);

        SampleBeanModernGson sample3();

        Nested sample3(SampleBeanModernGson b);

        Nested date(Date d);

        Date date();
    }

    public ExtensionGsonSupportTest() {
    }

    @Test
    public void testPrimitivesTestValuesIBeanRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root =
                this.factoryClassic.create(PrimitivesBeanClassicGson.class).fillWithTestValues();
        json = this.gsonClassic.toJson(root);
        PrimitivesBeanClassic copy =
                this.gsonClassic.fromJson(json, PrimitivesBeanClassicImpl.class);
        copy.assertEqual(root, requireNestedObjectsSame);
        copy = this.gsonClassic.fromJson(json, PrimitivesBeanClassicGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testPrimitivesTestValuesClassRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root = new PrimitivesBeanClassicImpl().fillWithTestValues();
        json = this.gsonClassic.toJson(root);
        PrimitivesBeanClassic copy =
                this.gsonClassic.fromJson(json, PrimitivesBeanClassicGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testPrimitivesNullValuesIBeanRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root =
                this.factoryClassic.create(PrimitivesBeanClassicGson.class).fillWithNullValues();
        json = this.gsonClassic.toJson(root);
        PrimitivesBeanClassic copy =
                this.gsonClassic.fromJson(json, PrimitivesBeanClassicImpl.class);
        copy.assertEqual(root, requireNestedObjectsSame);
        copy = this.gsonClassic.fromJson(json, PrimitivesBeanClassicGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testPrimitivesNullValuesClassRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        PrimitivesBeanClassic root = new PrimitivesBeanClassicImpl().fillWithNullValues();
        json = this.gsonClassic.toJson(root);
        PrimitivesBeanClassic copy =
                this.gsonClassic.fromJson(json, PrimitivesBeanClassicGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testModernStyleTestValuesIBeanRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        SampleBeanModern root = this.factoryModern.create(SampleBeanModernGson.class)
                .fillWithTestValues().self(null);
        json = this.gsonModern.toJson(root);
        SampleBeanModern copy = this.gsonModern.fromJson(json, SampleBeanModernImpl.class);
        copy.assertEqual(root, requireNestedObjectsSame);
        copy = this.gsonModern.fromJson(json, SampleBeanModernGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testModernStyleTestValuesClassRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        SampleBeanModern root = new SampleBeanModernImpl().fillWithTestValues().self(null);
        json = this.gsonModern.toJson(root);
        SampleBeanModern copy = this.gsonModern.fromJson(json, SampleBeanModernGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testModernStyleNullValuesIBeanRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        SampleBeanModern root =
                this.factoryModern.create(SampleBeanModernGson.class).fillWithNullValues();
        json = this.gsonModern.toJson(root);
        SampleBeanModern copy = this.gsonModern.fromJson(json, SampleBeanModernImpl.class);
        copy.assertEqual(root, requireNestedObjectsSame);
        copy = this.gsonModern.fromJson(json, SampleBeanModernGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testModernStyleNullValuesClassRoot() {
        final boolean requireNestedObjectsSame = false;
        String json;
        SampleBeanModern root = new SampleBeanModernImpl().fillWithNullValues();
        json = this.gsonModern.toJson(root);
        SampleBeanModern copy = this.gsonModern.fromJson(json, SampleBeanModernGson.class);
        copy.assertEqual(root, requireNestedObjectsSame);
    }

    @Test
    public void testNested() {
        final boolean requireNestedObjectsSame = false;
        final Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
        String json;
        Nested root = this.factoryModern.create(Nested.class).date(now)
                .sample1(this.factoryModern.create(SampleBeanModernGson.class))
                .sample3(this.factoryModern.create(SampleBeanModernGson.class));
        json = this.gsonModern.toJson(root);
        Nested copy = this.gsonModern.fromJson(json, Nested.class);
        BeanTestUtil.assertEqualsBean(Nested.class, BeanStyle.MODERN, root, copy,
                requireNestedObjectsSame);
    }
}
