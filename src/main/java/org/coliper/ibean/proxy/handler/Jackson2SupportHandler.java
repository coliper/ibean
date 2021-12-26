/*
 * Copyright (C) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.coliper.ibean.proxy.handler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.extension.Jackson2Support;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.coliper.ibean.util.ReflectionUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link Jackson2Support}.
 * 
 * @author alex@coliper.org
 */
public class Jackson2SupportHandler extends StatelessExtensionHandler {
    /**
     * {@link ExtensionSupport} related to this handler supposed to be used when
     * configuring extension handlers in {@link IBeanFactory}s, for example in
     * {@link ProxyIBeanFactory.Builder#withInterfaceSupport(ExtensionSupport)}.
     */
    public static final ExtensionSupport SUPPORT = new ExtensionSupport(Jackson2Support.class,
            Jackson2SupportHandler.class, false/* stateful */);

    private static final Method READ_PROPERTY_VALUE_FROM_JSON_PARSER_METHOD =
            ReflectionUtil.lookupFailableInterfaceMethod(Jackson2Support.class,
                    s -> s.readPropertyValueFromJsonParser(null, null, null));
    private static final Method SERIALIZE_METHOD = ReflectionUtil
            .lookupFailableInterfaceMethod(Jackson2Support.class, s -> s.serialize(null, null));
    private static final Method SERIALIZE_WITH_TYPE_METHOD =
            ReflectionUtil.lookupFailableInterfaceMethod(Jackson2Support.class,
                    s -> s.serializeWithType(null, null, null));

    /*
     * (non-Javadoc)
     * 
     * @see org.coliper.ibean.proxy.handler.StatelessExtensionHandler#
     * handleExtendedInterfaceCall(org.coliper.ibean.proxy.IBeanContext,
     * org.coliper.ibean.proxy.IBeanFieldAccess, java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable {
        if (READ_PROPERTY_VALUE_FROM_JSON_PARSER_METHOD.equals(method)) {
            Objects.requireNonNull(params, "params");
            Preconditions.checkArgument(params.length == 3);
            this.readPropertyValueFromJsonParser((String) params[0], (JsonParser) params[1],
                    (DeserializationContext) params[2], context, bean);
        } else if (SERIALIZE_METHOD.equals(method)) {
            Objects.requireNonNull(params, "params");
            Preconditions.checkArgument(params.length == 2);
            this.serialize((JsonGenerator) params[0], (SerializerProvider) params[1], context,
                    bean);
        } else if (SERIALIZE_WITH_TYPE_METHOD.equals(method)) {
            Objects.requireNonNull(params, "params");
            Preconditions.checkArgument(params.length == 3);
            this.serializeWithType((JsonGenerator) params[0], (SerializerProvider) params[1],
                    (TypeSerializer) params[2], proxyInstance, context, bean);
        } else {
            throw new IllegalStateException("unexpected method call " + method);
        }
        return null;
    }

    private void readPropertyValueFromJsonParser(String propertyName, JsonParser parser,
            DeserializationContext ctxt, IBeanContext<?> context, IBeanFieldAccess bean)
            throws IOException {
        final IBeanFieldMetaInfo meta =
                context.metaInfo().findFieldMetaWithFieldName(propertyName)
                        .orElseThrow(() -> new JsonParseException(
                                "unknown property " + propertyName + " for type "
                                        + context.metaInfo().beanType(),
                                parser.getCurrentLocation()));
        final Object value = this.readFieldValue(parser, ctxt, meta.fieldType());
        bean.setFieldValue(meta, value);
    }

    private Object readFieldValue(JsonParser parser, DeserializationContext ctxt,
            Class<?> fieldType) throws IOException {
        if (JsonToken.VALUE_NULL.equals(parser.getCurrentToken())) {
            return null;
        } else if (ClassUtils.isPrimitiveOrWrapper(fieldType)) {
            return this.readPrimitiveElement(parser, fieldType);
        } else if (String.class == fieldType) {
            return parser.getValueAsString();
        } else {
            return ctxt.readValue(parser, fieldType);
        }
    }

    private Object readPrimitiveElement(JsonParser parser, Class<?> fieldType) throws IOException {
        Class<?> prim = Primitives.unwrap(fieldType);
        if (boolean.class == prim) {
            return Boolean.valueOf(parser.getBooleanValue());
        }
        if (byte.class == prim) {
            return Byte.valueOf(parser.getByteValue());
        }
        if (char.class == prim) {
            return Character.valueOf(parser.getValueAsString().charAt(0));
        }
        if (int.class == prim) {
            return Integer.valueOf(parser.getIntValue());
        }
        if (short.class == prim) {
            return Short.valueOf(parser.getShortValue());
        }
        if (long.class == prim) {
            return Long.valueOf(parser.getLongValue());
        }
        if (double.class == prim) {
            return Double.valueOf(parser.getDoubleValue());
        }
        if (float.class == prim) {
            return Float.valueOf(parser.getFloatValue());
        }
        throw new RuntimeException("unexpected type " + fieldType);
    }

    private void serializeWithType(JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer, Object proxyInstance, IBeanContext<?> context,
            IBeanFieldAccess bean) throws IOException {
        typeSer.writeTypePrefixForObject(proxyInstance, gen, context.metaInfo().beanType());
        this.serialize(gen, serializers, context, bean);
        typeSer.writeTypeSuffixForObject(proxyInstance, gen);
    }

    private void serialize(JsonGenerator gen, SerializerProvider serializers,
            IBeanContext<?> context, IBeanFieldAccess bean) throws IOException {
        gen.writeStartObject();
        serializeFields(gen, serializers, context, bean);
        gen.writeEndObject();
    }

    private void serializeFields(JsonGenerator gen, SerializerProvider serializers,
            IBeanContext<?> context, IBeanFieldAccess bean) throws IOException {
        for (IBeanFieldMetaInfo meta : context.metaInfo().fieldMetaInfos()) {
            final String fieldName = meta.fieldName();
            gen.writeFieldName(fieldName);
            final Object value = bean.getFieldValue(meta);
            serializers.defaultSerializeValue(value, gen);
        }
    }
}
