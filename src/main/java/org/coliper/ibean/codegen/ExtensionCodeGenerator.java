package org.coliper.ibean.codegen;

import java.util.Collections;
import java.util.List;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

public interface ExtensionCodeGenerator {

    public static final CodeBlock EMPTY_BLOCK = CodeBlock.of("");

    default List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        return Collections.emptyList();
    }

    default CodeBlock createGetterCodeBlock(IBeanFieldMetaInfo fieldMeta) {
        return EMPTY_BLOCK;
    }

    default CodeBlock createSetterCodeBlock(IBeanFieldMetaInfo fieldMeta) {
        return EMPTY_BLOCK;
    }

    default List<FieldSpec> createExtensionSpecificFields(IBeanTypeMetaInfo<?> beanMeta) {
        return Collections.emptyList();
    }

}
