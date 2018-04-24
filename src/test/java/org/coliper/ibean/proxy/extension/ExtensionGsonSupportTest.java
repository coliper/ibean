package org.coliper.ibean.proxy.extension;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.PrimitivesBeanClassic;
import org.coliper.ibean.PrimitivesBeanClassicImpl;
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
            .withBeanStyle(BeanStyle.MODERN_WITH_OPTIONAL).build();
    final Gson gsonModern = new GsonBuilder().registerTypeHierarchyAdapter(GsonSupport.class,
            new GsonSerializerDeserializerForIBeans(factoryModern)).create();
    
    public static interface PrimitivesBeanClassicGson extends PrimitivesBeanClassic, GsonSupport {}

    public ExtensionGsonSupportTest() {
    }

    @Test
    public void testPrimitivesTestValues() {
        String json;
        PrimitivesBeanClassic root = this.factoryClassic.create(PrimitivesBeanClassic.class).fillWithTestValues();
        json = this.gsonClassic.toJson(root);
        PrimitivesBeanClassic copy = this.gsonClassic.fromJson(json, PrimitivesBeanClassicImpl.class);
        copy.assertEqual(root);
    }
}
