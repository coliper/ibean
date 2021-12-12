package org.coliper.ibean.codegen.extension;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.codegen.JavaPoetUtil;
import org.coliper.ibean.extension.BeanIncompleteException;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.proxy.ExtensionHandler;

import com.squareup.javapoet.MethodSpec;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link Completable}.
 *
 * @author alex@coliper.org
 */
public class CompletableExtensionCodeGenerator implements ExtensionCodeGenerator {

    private final static Method IS_COMPLETE_METHOD =
            MethodUtils.getAccessibleMethod(Completable.class, "isComplete");
    private final static Method ASSERT_COMPLETE_METHOD =
            MethodUtils.getAccessibleMethod(Completable.class, "assertComplete");

    @Override
    public List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        return List.of(this.createAssertCompleteImplementation(beanMeta),
                this.createIsCompleteImplementation(beanMeta, beanCodeElements));
    }

    private MethodSpec createIsCompleteImplementation(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        MethodSpec.Builder methodSpec =
                JavaPoetUtil.methodSpecBuilderFromOverride(IS_COMPLETE_METHOD);
        methodSpec.addStatement("boolean complete = true");
        for (IBeanFieldMetaInfo fieldMeta : beanMeta.fieldMetaInfos()) {
            if (!fieldMeta.hasOptionalSupport() && !fieldMeta.fieldType().isPrimitive()) {
                methodSpec.addStatement("complete &= ($N != null)",
                        beanCodeElements.fieldNameFromPropertyName(fieldMeta.fieldName()));
            }
        }
        methodSpec.addStatement("return complete");
        return methodSpec.build();
    }

    private MethodSpec createAssertCompleteImplementation(IBeanTypeMetaInfo<?> beanMeta) {
        MethodSpec.Builder methodSpec =
                JavaPoetUtil.methodSpecBuilderFromOverride(ASSERT_COMPLETE_METHOD);
        methodSpec.returns(beanMeta.beanType());
        //@formatter:off
        methodSpec
            .beginControlFlow("if (!this.isComplete())")
            .addStatement("throw new $T()", BeanIncompleteException.class)
            .endControlFlow()
            .addStatement("return this");
        //@formatter:on
        return methodSpec.build();
    }
}
