package org.coliper.ibean.codegen.extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.codegen.BeanCodeElements;
import org.coliper.ibean.codegen.CommonCodeSnippets;
import org.coliper.ibean.codegen.ExtensionCodeGenerator;
import org.coliper.ibean.codegen.JavaPoetUtil;
import org.coliper.ibean.extension.BeanFrozenException;
import org.coliper.ibean.extension.Freezable;
import org.coliper.ibean.extension.TempFreezable;
import org.coliper.ibean.proxy.ExtensionHandler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

/**
 * {@link ExtensionHandler} implementation for bean extension interface
 * {@link Freezable}.
 *
 * @author alex@coliper.org
 */
public class FreezableExtensionCodeGenerator implements ExtensionCodeGenerator {

    private final static Method IS_FROZEN_METHOD =
            MethodUtils.getAccessibleMethod(Freezable.class, "isFrozen");
    private final static Method FREEZE_METHOD =
            MethodUtils.getAccessibleMethod(Freezable.class, "freeze");
    private final static Method UNFREEZE_METHOD =
            MethodUtils.getAccessibleMethod(TempFreezable.class, "unfreeze");
    private final static String IS_FROZEN_FIELD_NAME =
            CommonCodeSnippets.EXTENSION_SPECIFIC_FIELD_NAME_PREFIX + "isFrozen";

    @Override
    public CodeBlock createSetterCodeBlock(IBeanFieldMetaInfo fieldMeta) {
        //@formatter:off
        return CodeBlock.builder()
                .beginControlFlow("if ($N)", IS_FROZEN_FIELD_NAME)
                .addStatement("throw new $T()", BeanFrozenException.class)
                .endControlFlow()
                .build();
        //@formatter:on
    }

    @Override
    public List<FieldSpec> createExtensionSpecificFields(IBeanTypeMetaInfo<?> beanMeta) {
        return List.of(FieldSpec.builder(TypeName.BOOLEAN, IS_FROZEN_FIELD_NAME, Modifier.PRIVATE)
                .build());
    }

    @Override
    public List<MethodSpec> createInterfaceMethodImplementations(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        List<MethodSpec> methodList = new ArrayList<>();
        methodList.add(this.createFreezeImplementation(beanMeta));
        methodList.add(this.createIsFrozenImplementation(beanMeta, beanCodeElements));
        if (TempFreezable.class.isAssignableFrom(beanMeta.beanType())) {
            methodList.add(this.createUnfreezeImplementation(beanMeta));
        }
        return methodList;
    }

    private MethodSpec createIsFrozenImplementation(IBeanTypeMetaInfo<?> beanMeta,
            BeanCodeElements beanCodeElements) {
        MethodSpec.Builder methodSpec =
                JavaPoetUtil.methodSpecBuilderFromOverride(IS_FROZEN_METHOD);
        methodSpec.addStatement("return $N", IS_FROZEN_FIELD_NAME);
        return methodSpec.build();
    }

    private MethodSpec createFreezeImplementation(IBeanTypeMetaInfo<?> beanMeta) {
        //@formatter:off
        return JavaPoetUtil.methodSpecBuilderFromOverride(FREEZE_METHOD)
                .returns(beanMeta.beanType())
                .addStatement("$N = true", IS_FROZEN_FIELD_NAME)
                .addStatement("return this")
                .build();
        //@formatter:on
    }

    private MethodSpec createUnfreezeImplementation(IBeanTypeMetaInfo<?> beanMeta) {
        //@formatter:off
        return JavaPoetUtil.methodSpecBuilderFromOverride(UNFREEZE_METHOD)
                .returns(beanMeta.beanType())
                .addStatement("$N = false", IS_FROZEN_FIELD_NAME)
                .addStatement("return this")
                .build();
        //@formatter:on
    }
}
