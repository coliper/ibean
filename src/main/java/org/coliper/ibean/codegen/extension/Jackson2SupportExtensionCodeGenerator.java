package org.coliper.ibean.codegen.extension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.codegen.JavaPoetUtil;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.Jackson2Support;
import org.coliper.ibean.proxy.ExtensionHandler;
import org.coliper.ibean.util.ReflectionUtil;

import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link Freezable}.
 *
 * @author alex@coliper.org
 */
public class Jackson2SupportExtensionCodeGenerator implements ExtensionCodeGenerator {

    private static final Method READ_PROPERTY_VALUE_FROM_JSON_PARSER_METHOD =
            ReflectionUtil.lookupFailableInterfaceMethod(Jackson2Support.class,
                    s -> s.readPropertyValueFromJsonParser(null, null, null));
    private static final Method SERIALIZE_METHOD = ReflectionUtil
            .lookupFailableInterfaceMethod(Jackson2Support.class, s -> s.serialize(null, null));
    private static final Method SERIALIZE_WITH_TYPE_METHOD =
            ReflectionUtil.lookupFailableInterfaceMethod(Jackson2Support.class,
                    s -> s.serializeWithType(null, null, null));
    private static final String PROPERTY_NAME_PARAM_NAME = "propertyName";
    private static final String JSON_PARSER_PARAM_NAME = "jsonParser";
    private static final String DESERIALIZATION_CONTEXT_PARAM_NAME = "deserializationContext";
    private static final String JSON_GENERATOR_PARAM_NAME = "jsonGenerator";
    private static final String SERIALIZER_PROVIDER_PARAM_NAME = "serializerProvider";
    private static final String TYPE_SERIALIZER_PARAM_NAME = "typeSerializer";
    private static final String IS_NULL_VAR = "isNullValue";

    private static String nullsafe(String expression) {
        return IS_NULL_VAR + " ? null : " + expression;
    }

    private static final Map<Class<?>, String> READ_EXPRESSION_MAP;
    static {
        Map<Class<?>, String> m;
        m = new HashMap<>();
        m.put(boolean.class, "$N.getBooleanValue()");
        m.put(char.class, "$N.getValueAsString().charAt(0)");
        m.put(byte.class, "$N.getByteValue()");
        m.put(short.class, "$N.getShortValue()");
        m.put(int.class, "$N.getIntValue()");
        m.put(long.class, "$N.getLongValue()");
        m.put(float.class, "$N.getFloatValue()");
        m.put(double.class, "$N.getDoubleValue()");
        m.put(Boolean.class, nullsafe("Boolean.valueOf($N.getBooleanValue())"));
        m.put(Character.class, nullsafe("Character.valueOf($N.getValueAsString().charAt(0))"));
        m.put(Byte.class, nullsafe("Byte.valueOf($N.getByteValue())"));
        m.put(Short.class, nullsafe("Short.valueOf($N.getShortValue())"));
        m.put(Integer.class, nullsafe("Integer.valueOf($N.getIntValue())"));
        m.put(Long.class, nullsafe("Long.valueOf($N.getLongValue())"));
        m.put(Float.class, nullsafe("Float.valueOf($N.getFloatValue())"));
        m.put(Double.class, nullsafe("Double.valueOf($N.getDoubleValue())"));
        m.put(String.class, nullsafe("$N.getValueAsString()"));
        READ_EXPRESSION_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<Class<?>, String> WRITE_EXPRESSION_MAP;
    static {
        Map<Class<?>, String> m;
        m = new HashMap<>();
        m.put(boolean.class, "Boolean.valueOf($N)");
        m.put(char.class, "Character.valueOf($N)");
        m.put(byte.class, "Byte.valueOf($N)");
        m.put(short.class, "Short.valueOf($N)");
        m.put(int.class, "Integer.valueOf($N)");
        m.put(long.class, "Long.valueOf($N)");
        m.put(float.class, "Float.valueOf($N)");
        m.put(double.class, "Double.valueOf($N)");
        WRITE_EXPRESSION_MAP = Collections.unmodifiableMap(m);
    }

    @Override
    public List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        return List.of(
                this.createReadPropertyValueFromJsonParserImplementation(beanCodeElements,
                        beanMeta),
                this.createSerializeImplementation(beanCodeElements, beanMeta),
                this.createSerializeWithTypeImplementation(beanCodeElements, beanMeta));
    }

    private MethodSpec createReadPropertyValueFromJsonParserImplementation(
            BeanCodeElements beanCodeElements, IBeanTypeMetaInfo<?> beanMeta) {
        MethodSpec.Builder methodSpec = JavaPoetUtil.methodSpecBuilderFromOverride(
                READ_PROPERTY_VALUE_FROM_JSON_PARSER_METHOD, PROPERTY_NAME_PARAM_NAME,
                JSON_PARSER_PARAM_NAME, DESERIALIZATION_CONTEXT_PARAM_NAME);
        methodSpec.addStatement("boolean $N = $T.VALUE_NULL.equals($N.getCurrentToken())",
                IS_NULL_VAR, JsonToken.class, JSON_PARSER_PARAM_NAME);
        for (IBeanFieldMetaInfo fieldMeta : beanMeta.fieldMetaInfos()) {
            methodSpec.addCode(createReadCodeForField(beanCodeElements, fieldMeta));
        }
        methodSpec.addStatement("throw new $T(\"unknown property '\" + $N + \"'\")",
                IllegalArgumentException.class, PROPERTY_NAME_PARAM_NAME);
        return methodSpec.build();
    }

    private CodeBlock createReadCodeForField(BeanCodeElements beanCodeElements,
            IBeanFieldMetaInfo fieldMeta) {
        final String propertyName = fieldMeta.fieldName();
        final String fieldName = beanCodeElements.fieldNameFromPropertyName(propertyName);
        final String valueExpression = READ_EXPRESSION_MAP.get(fieldMeta.fieldType());
        final CodeBlock setFieldBlock;
        if (valueExpression != null) {
            setFieldBlock =
                    CodeBlock.of("this.$N = " + valueExpression, fieldName, JSON_PARSER_PARAM_NAME);
        } else {
            final String recursiveRead = nullsafe("($T)$N.readValue($N, $T.class)");
            setFieldBlock = CodeBlock.of("this.$N = " + recursiveRead, fieldName,
                    fieldMeta.fieldType(), DESERIALIZATION_CONTEXT_PARAM_NAME,
                    JSON_PARSER_PARAM_NAME, fieldMeta.fieldType());
        }
        final CodeBlock.Builder code = CodeBlock.builder();
        code.beginControlFlow("if ($S.equals($N))", propertyName, PROPERTY_NAME_PARAM_NAME);
        code.addStatement(setFieldBlock);
        code.addStatement("return");
        code.endControlFlow();
        return code.build();
    }

    private MethodSpec createSerializeImplementation(BeanCodeElements beanCodeElements,
            IBeanTypeMetaInfo<?> beanMeta) {
        MethodSpec.Builder methodSpec = JavaPoetUtil.methodSpecBuilderFromOverride(SERIALIZE_METHOD,
                JSON_GENERATOR_PARAM_NAME, SERIALIZER_PROVIDER_PARAM_NAME);
        methodSpec.addStatement("$N.writeStartObject()", JSON_GENERATOR_PARAM_NAME);
        for (IBeanFieldMetaInfo fieldMeta : beanMeta.fieldMetaInfos()) {
            methodSpec.addStatement(createWriteCodeForField(beanCodeElements, fieldMeta));
        }
        methodSpec.addStatement("$N.writeEndObject()", JSON_GENERATOR_PARAM_NAME);
        return methodSpec.build();
    }

    private CodeBlock createWriteCodeForField(BeanCodeElements beanCodeElements,
            IBeanFieldMetaInfo fieldMeta) {
        final String propertyName = fieldMeta.fieldName();
        final String fieldName = beanCodeElements.fieldNameFromPropertyName(propertyName);
        final String valueExpression =
                WRITE_EXPRESSION_MAP.getOrDefault(fieldMeta.fieldType(), "$N");
        return CodeBlock.of("$N.defaultSerializeField($S, " + valueExpression + ", $N)",
                SERIALIZER_PROVIDER_PARAM_NAME, propertyName, fieldName, JSON_GENERATOR_PARAM_NAME);
    }

    private MethodSpec createSerializeWithTypeImplementation(BeanCodeElements beanCodeElements,
            IBeanTypeMetaInfo<?> beanMeta) {
        MethodSpec.Builder methodSpec = JavaPoetUtil.methodSpecBuilderFromOverride(
                SERIALIZE_WITH_TYPE_METHOD, JSON_GENERATOR_PARAM_NAME,
                SERIALIZER_PROVIDER_PARAM_NAME, TYPE_SERIALIZER_PARAM_NAME);
        methodSpec.addStatement("$N.writeTypePrefixForObject(this, $N, $T.class)",
                TYPE_SERIALIZER_PARAM_NAME, JSON_GENERATOR_PARAM_NAME, beanMeta.beanType());
        methodSpec.addStatement("this.serialize($N, $N)", JSON_GENERATOR_PARAM_NAME,
                SERIALIZER_PROVIDER_PARAM_NAME);
        methodSpec.addStatement("$N.writeTypeSuffixForObject(this, $N)", TYPE_SERIALIZER_PARAM_NAME,
                JSON_GENERATOR_PARAM_NAME);
        return methodSpec.build();
    }
}
