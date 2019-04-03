package org.coliper.ibean.codegen;

import java.util.List;

import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

public interface ExtensionCodeGenerator {

    List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements);

    CodeBlock createGetterCodeBlock(IBeanFieldMetaInfo fieldMeta);

    CodeBlock createSetterCodeBlock(IBeanFieldMetaInfo fieldMeta);
}
