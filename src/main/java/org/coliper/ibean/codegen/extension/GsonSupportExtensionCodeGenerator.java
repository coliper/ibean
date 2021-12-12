package org.coliper.ibean.codegen.extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.codegen.JavaPoetUtil;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.proxy.ExtensionHandler;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link Freezable}.
 *
 * @author alex@coliper.org
 */
public class GsonSupportExtensionCodeGenerator implements ExtensionCodeGenerator {

    private final static Method READ_FROM_JSON_OBJECT_METHOD =
            MethodUtils.getAccessibleMethod(GsonSupport.class, "readFromJsonObject",
                    JsonObject.class, JsonDeserializationContext.class);
    private final static Method WRITE_TO_JSON_OBJECT_METHOD =
            MethodUtils.getAccessibleMethod(GsonSupport.class, "writeToJsonObject",
                    JsonObject.class, JsonSerializationContext.class);
    private static final String JSON_OBJECT_PARAM_NAME = "jsonObject";
    private static final String CONTEXT_PARAM_NAME = "context";

    private static final Map<Class<?>, String> READ_EXPRESSION_MAP;
    static {
        Map<Class<?>, String> m;
        m = new HashMap<>();
        m.put(boolean.class, "value.getAsBoolean()");
        m.put(char.class, "value.getAsCharacter()");
        m.put(byte.class, "value.getAsByte()");
        m.put(short.class, "value.getAsShort()");
        m.put(int.class, "value.getAsInt()");
        m.put(long.class, "value.getAsLong()");
        m.put(float.class, "value.getAsFloat()");
        m.put(double.class, "value.getAsDouble()");
        m.put(Boolean.class, "value.isJsonNull() ? null : Boolean.valueOf(value.getAsBoolean())");
        m.put(Character.class,
                "value.isJsonNull() ? null : Character.valueOf(value.getAsCharacter())");
        m.put(Byte.class, "value.isJsonNull() ? null : Byte.valueOf(value.getAsByte())");
        m.put(Short.class, "value.isJsonNull() ? null : Short.valueOf(value.getAsShort())");
        m.put(Integer.class, "value.isJsonNull() ? null : Integer.valueOf(value.getAsInt())");
        m.put(Long.class, "value.isJsonNull() ? null : Long.valueOf(value.getAsLong())");
        m.put(Float.class, "value.isJsonNull() ? null : Float.valueOf(value.getAsFloat())");
        m.put(Double.class, "value.isJsonNull() ? null : Double.valueOf(value.getAsDouble())");
        m.put(String.class, "value.isJsonNull() ? null : value.getAsString()");
        READ_EXPRESSION_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<Class<?>, String> WRITE_EXPRESSION_MAP;
    static {
        Map<Class<?>, String> m;
        m = new HashMap<>();
        m.put(boolean.class, "addProperty($S, (Boolean)$N)");
        m.put(char.class, "addProperty($S, (Character)$N)");
        m.put(byte.class, "addProperty($S, (Byte)$N)");
        m.put(short.class, "addProperty($S, (Short)$N)");
        m.put(int.class, "addProperty($S, (Integer)$N)");
        m.put(long.class, "addProperty($S, (Long)$N)");
        m.put(float.class, "addProperty($S, (Float)$N)");
        m.put(double.class, "addProperty($S, (Double)$N)");
        m.put(Boolean.class, "addProperty($S, $N)");
        m.put(Character.class, "addProperty($S, $N)");
        m.put(Byte.class, "addProperty($S, $N)");
        m.put(Short.class, "addProperty($S, $N)");
        m.put(Integer.class, "addProperty($S, $N)");
        m.put(Long.class, "addProperty($S, $N)");
        m.put(Float.class, "addProperty($S, $N)");
        m.put(Double.class, "addProperty($S, $N)");
        m.put(String.class, "addProperty($S, $N)");
        WRITE_EXPRESSION_MAP = Collections.unmodifiableMap(m);
    }

    @Override
    public List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        List<MethodSpec> methodList = new ArrayList<>();
        methodList.add(this.createWriteToJsonObjectImplementation(beanCodeElements, beanMeta));
        methodList.add(this.createReadFromJsonObjectImplementation(beanCodeElements, beanMeta));
        return methodList;
    }

    private MethodSpec createReadFromJsonObjectImplementation(BeanCodeElements beanCodeElements,
            IBeanTypeMetaInfo<?> beanMeta) {
        MethodSpec.Builder methodSpec = JavaPoetUtil.methodSpecBuilderFromOverride(
                READ_FROM_JSON_OBJECT_METHOD, JSON_OBJECT_PARAM_NAME, CONTEXT_PARAM_NAME);
        for (IBeanFieldMetaInfo fieldMeta : beanMeta.fieldMetaInfos()) {
            methodSpec.addCode(createReadCodeForField(beanCodeElements, fieldMeta));
        }
        return methodSpec.build();
    }

    private CodeBlock createReadCodeForField(BeanCodeElements beanCodeElements,
            IBeanFieldMetaInfo fieldMeta) {
        final String objectDeserializationExpression =
                CodeBlock.of("($T)$N.deserialize(value, $T.class)", fieldMeta.fieldType(),
                        CONTEXT_PARAM_NAME, fieldMeta.fieldType()).toString();
        final String valueExpression = READ_EXPRESSION_MAP.getOrDefault(fieldMeta.fieldType(),
                objectDeserializationExpression);
        final String fieldName = beanCodeElements.fieldNameFromPropertyName(fieldMeta.fieldName());
        final CodeBlock.Builder code = CodeBlock.builder();
        code.beginControlFlow("if ($N.has($S))", JSON_OBJECT_PARAM_NAME, fieldMeta.fieldName());
        code.addStatement("$T value = $N.get($S)", JsonElement.class, JSON_OBJECT_PARAM_NAME,
                fieldMeta.fieldName());
        code.addStatement("this.$N = $L", fieldName, valueExpression);
        code.endControlFlow();
        return code.build();
    }

    private MethodSpec createWriteToJsonObjectImplementation(BeanCodeElements beanCodeElements,
            IBeanTypeMetaInfo<?> beanMeta) {
        MethodSpec.Builder methodSpec = JavaPoetUtil.methodSpecBuilderFromOverride(
                WRITE_TO_JSON_OBJECT_METHOD, JSON_OBJECT_PARAM_NAME, CONTEXT_PARAM_NAME);
        for (IBeanFieldMetaInfo fieldMeta : beanMeta.fieldMetaInfos()) {
            methodSpec.addCode(createWriteCodeForField(beanCodeElements, fieldMeta));
        }
        return methodSpec.build();
    }

    private CodeBlock createWriteCodeForField(BeanCodeElements beanCodeElements,
            IBeanFieldMetaInfo fieldMeta) {
        final String propertyName = fieldMeta.fieldName();
        final String fieldName = beanCodeElements.fieldNameFromPropertyName(propertyName);
        final String objectSerializationExpression =
                "add($S, " + CONTEXT_PARAM_NAME + ".serialize($N))";
        final String addMethodCallRaw = WRITE_EXPRESSION_MAP.getOrDefault(fieldMeta.fieldType(),
                objectSerializationExpression);
        final String addMethodCall =
                CodeBlock.of(addMethodCallRaw, propertyName, fieldName).toString();
        final boolean isFieldValueNullable = !fieldMeta.fieldType().isPrimitive();
        final CodeBlock.Builder code = CodeBlock.builder();
        if (isFieldValueNullable) {
            code.beginControlFlow("if ($N == null)", fieldName);
            code.addStatement("$N.add($S, $T.INSTANCE)", JSON_OBJECT_PARAM_NAME, propertyName,
                    JsonNull.class);
            code.nextControlFlow("else");
        }
        code.addStatement("$N.$L", JSON_OBJECT_PARAM_NAME, addMethodCall);
        if (isFieldValueNullable) {
            code.endControlFlow();
        }
        return code.build();
    }
}
