package org.coliper.ibean.codegen;

import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Preconditions;

class ToStringStyleWrapper extends ToStringStyleAdapter {

    private static final long serialVersionUID = 1L;

    private final Class<?> interfaceClass;

    public ToStringStyleWrapper(ToStringStyle delegate, Class<?> interfaceClass) {
        super(delegate);
        Preconditions.checkNotNull(interfaceClass, "interfaceClass");
        this.interfaceClass = interfaceClass;
    }

    @Override
    public void appendStart(StringBuffer buffer, Object object) {
        final int startIndex = buffer.length();
        this.delegate.appendStart(buffer, object);
        this.replaceAllInStringBufferStartingFromIndex(buffer, startIndex,
                object.getClass().getName(), this.interfaceClass.getName());
        this.replaceAllInStringBufferStartingFromIndex(buffer, startIndex,
                this.getShortClassName(object.getClass()), this.getShortClassName(interfaceClass));
    }

    private void replaceAllInStringBufferStartingFromIndex(StringBuffer buffer,
            final int startIndex, String searchString, String replaceString) {
        int pos = -1;
        while ((pos = buffer.indexOf(searchString, startIndex)) >= 0) {
            buffer.delete(pos, pos + searchString.length());
            buffer.insert(pos, replaceString);
        }
    }

}
