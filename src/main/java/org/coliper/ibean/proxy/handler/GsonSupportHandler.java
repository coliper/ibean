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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.proxy.ExtensionSupport;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.IBeanFieldAccess;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.coliper.ibean.util.ReflectionUtil;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;

/**
 * @author alex@coliper.org
 *
 */
public class GsonSupportHandler extends StatelessExtensionHandler {
    /**
     * {@link ExtensionSupport} related to this handler supposed to be used when
     * configuring extension handlers in {@link IBeanFactory}s, for example in
     * {@link ProxyIBeanFactory.Builder#withInterfaceSupport(ExtensionSupport)}.
     */
    public static final ExtensionSupport SUPPORT =
            new ExtensionSupport(GsonSupport.class, GsonSupportHandler.class, false/* stateful */);

    private static final Method JSON_READ_METHOD =
            ReflectionUtil.lookupInterfaceMethod(GsonSupport.class, (GsonSupport s) -> {
                s.readFromJsonObject(null, null);
            });
    private static final Method JSON_WRITE_METHOD =
            ReflectionUtil.lookupInterfaceMethod(GsonSupport.class, (GsonSupport s) -> {
                s.writeToJsonObject(null, null);
            });

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
        if (JSON_WRITE_METHOD.equals(method)) {
            Objects.requireNonNull(params, "params");
            Preconditions.checkArgument(params.length == 2);
            this.writeToJsonObject((JsonObject) params[0], (JsonSerializationContext) params[1],
                    context, bean);
        } else if (JSON_READ_METHOD.equals(method)) {
            Objects.requireNonNull(params, "params");
            Preconditions.checkArgument(params.length == 2);
            this.readFromJsonObject((JsonObject) params[0], (JsonDeserializationContext) params[1],
                    context, bean);
        } else {
            throw new IllegalStateException("unexpected method call " + method);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.coliper.ibean.extension.GsonSupport#jsonRead(com.google.gson.stream.
     * JsonReader)
     */
    private void readFromJsonObject(JsonObject jsonObject, JsonDeserializationContext jsonContext,
            IBeanContext<?> context, IBeanFieldAccess bean) throws IOException {
        Set<Entry<String, JsonElement>> elements = jsonObject.entrySet();
        for (Entry<String, JsonElement> element : elements) {
            final String fieldName = element.getKey();
            final IBeanFieldMetaInfo meta = context.metaInfo().findFieldMetaWithFieldName(fieldName)
                    .orElseThrow(() -> new JsonSyntaxException("unknown property " + fieldName
                            + " for type " + context.metaInfo().beanType()));
            final Object value =
                    this.readElement(element.getValue(), jsonContext, meta.fieldType());
            bean.setFieldValue(meta, value);
        }
    }

    /**
     * @param value
     * @param jsonContext
     * @param fieldType
     * @return
     */
    private Object readElement(JsonElement value, JsonDeserializationContext jsonContext,
            Class<?> fieldType) {
        if (value.isJsonNull()) {
            return null;
        } else if (ClassUtils.isPrimitiveOrWrapper(fieldType)) {
            return this.readPrimitiveElement(value, fieldType);
        } else if (String.class == fieldType) {
            return value.getAsString();
        } else {
            return jsonContext.deserialize(value, fieldType);
        }
    }

    /**
     * @param value
     * @param fieldType
     * @return
     */
    private Object readPrimitiveElement(JsonElement value, Class<?> fieldType) {
        Class<?> prim = Primitives.unwrap(fieldType);
        if (boolean.class == prim) {
            return Boolean.valueOf(value.getAsBoolean());
        }
        if (byte.class == prim) {
            return Byte.valueOf(value.getAsByte());
        }
        if (char.class == prim) {
            return Character.valueOf(value.getAsCharacter());
        }
        if (int.class == prim) {
            return Integer.valueOf(value.getAsInt());
        }
        if (short.class == prim) {
            return Short.valueOf(value.getAsShort());
        }
        if (long.class == prim) {
            return Long.valueOf(value.getAsLong());
        }
        if (double.class == prim) {
            return Double.valueOf(value.getAsDouble());
        }
        if (float.class == prim) {
            return Float.valueOf(value.getAsFloat());
        }
        throw new RuntimeException("unexpected type " + fieldType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.coliper.ibean.extension.GsonSupport#jsonWrite(com.google.gson.stream.
     * JsonWriter)
     */
    private void writeToJsonObject(JsonObject jsonObject, JsonSerializationContext jsonContext,
            IBeanContext<?> context, IBeanFieldAccess bean) {
        for (IBeanFieldMetaInfo meta : context.metaInfo().fieldMetaInfos()) {
            final String fieldName = meta.fieldName();
            final Object value = bean.getFieldValue(meta);
            if (value == null) {
                jsonObject.add(fieldName, null);
            } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                this.writePrimitiveElement(jsonObject, fieldName, value);
            } else if (value instanceof String) {
                jsonObject.addProperty(fieldName, (String) value);
            } else {
                jsonObject.add(fieldName, jsonContext.serialize(value));
            }
        }
    }

    /**
     * @param jsonObject
     * @param value
     * @return
     */
    private void writePrimitiveElement(JsonObject jsonObject, String fieldName, Object value) {
        if (value instanceof Number) {
            jsonObject.addProperty(fieldName, (Number) value);
        } else if (value instanceof Boolean) {
            jsonObject.addProperty(fieldName, (Boolean) value);
        } else if (value instanceof Character) {
            jsonObject.addProperty(fieldName, (Character) value);
        } else {
            throw new RuntimeException(
                    "unexpected value " + value + " of type " + value.getClass());
        }
    }

}
