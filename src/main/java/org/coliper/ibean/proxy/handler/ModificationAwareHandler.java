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

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.extension.ModificationAwareExt;
import org.coliper.ibean.proxy.IBeanContext;
import org.coliper.ibean.proxy.ExtensionSupport;

/**
 * @author alex@coliper.org
 *
 */
public class ModificationAwareHandler extends StatefulExtensionHandler implements ModificationAwareExt {
    private boolean modified = false;
    private boolean[] fieldModified = null;
    private IBeanTypeMetaInfo<?> beanMetaInfo = null;
    public static final ExtensionSupport SUPPORT =
    new ExtensionSupport(ModificationAwareExt.class,
            ModificationAwareHandler.class, true/*stateful*/);
    
    

    /* (non-Javadoc)
     * @see org.coliper.ibean.proxy.IBeanInvocationHandler#onInit(org.coliper.ibean.IBeanTypeMetaInfo)
     */
    @Override
    public void onInitStateful(Object proxyInstance, IBeanTypeMetaInfo<?> metaInfo) {
        this.beanMetaInfo = metaInfo;
        if (beanTypeIncludesModificationAwareExt(metaInfo)) {
            this.fieldModified = new boolean[metaInfo.noOfFields()];
        }
    }
    
    private boolean beanTypeIncludesModificationAwareExt(IBeanTypeMetaInfo<?> metaInfo) {
        return ModificationAwareExt.class.isAssignableFrom(metaInfo.beanType());
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.extension.ModificationAware#isModified()
     */
    @Override
    public boolean isModified() {
        return this.modified;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.extension.ModificationAware#resetModified()
     */
    @Override
    public void resetModified() {
        this.modified = false;
        if (this.fieldModified != null) {
            Arrays.fill(this.fieldModified, false);
        }
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.proxy.handler.StatefulExtendedInterfaceHandler#interceptSetterCall(org.coliper.ibean.proxy.IBeanContext, org.coliper.ibean.IBeanFieldMetaInfo, java.lang.Object)
     */
    @Override
    public Object interceptSetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object newValue, Object proxyInstance) {
        this.modified = true;
        if (this.fieldModified != null) {
            this.fieldModified[fieldMeta.ordinal()] = true;
        }
        return super.interceptSetterCall(context, fieldMeta, newValue, proxyInstance);
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.extension.ModificationAwareExt#getModifiedFieldNames()
     */
    @Override
    public String[] getModifiedFieldNames() {
        if (!this.modified) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        checkState(this.fieldModified != null, "unexpected method call of extended interface");
        List<String> nameList = new ArrayList<>(this.fieldModified.length);
        for (int i=0; i<fieldModified.length; i++) {
            if (fieldModified[i]) {
                nameList.add(this.beanMetaInfo.fieldMetaInfos().get(i).fieldName());
            }
        }
        String[] names = nameList.toArray(new String[nameList.size()]);
        return names;
    }

    /* (non-Javadoc)
     * @see org.coliper.ibean.extension.ModificationAwareExt#allFieldsModified()
     */
    @Override
    public boolean allFieldsModified() {
        checkState(this.fieldModified != null, "unexpected method call of extended interface");
        return !ArrayUtils.contains(fieldModified, false);
    }


}
