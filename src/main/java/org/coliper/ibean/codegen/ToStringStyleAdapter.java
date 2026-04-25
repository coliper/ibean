package org.coliper.ibean.codegen;

import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Preconditions;

public class ToStringStyleAdapter extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    protected ToStringStyle delegate;

    protected ToStringStyleAdapter(ToStringStyle delegate) {
        Preconditions.checkNotNull(delegate, "delegate");
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.commons.lang3.builder.ToStringStyle#appendSuper(java.lang.
     * StringBuffer, java.lang.String)
     */
    @Override
    public void appendSuper(StringBuffer buffer, String superToString) {
        this.delegate.appendSuper(buffer, superToString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.commons.lang3.builder.ToStringStyle#appendToString(java.lang.
     * StringBuffer, java.lang.String)
     */
    @Override
    public void appendToString(StringBuffer buffer, String toString) {
        this.delegate.appendToString(buffer, toString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.commons.lang3.builder.ToStringStyle#appendStart(java.lang.
     * StringBuffer, java.lang.Object)
     */
    @Override
    public void appendStart(StringBuffer buffer, Object object) {
        this.delegate.appendStart(buffer, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#appendEnd(java.lang.
     * StringBuffer, java.lang.Object)
     */
    @Override
    public void appendEnd(StringBuffer buffer, Object object) {
        this.delegate.appendEnd(buffer, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, java.lang.Object, java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, value, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, long)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, long value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, int)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, int value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, short)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, short value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, byte)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, byte value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, char)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, char value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, double)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, double value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, float)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, float value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, boolean value) {
        this.delegate.append(buffer, fieldName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, java.lang.Object[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, long[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, int[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, short[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, byte[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, char[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, double[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, float[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.lang3.builder.ToStringStyle#append(java.lang.
     * StringBuffer, java.lang.String, boolean[], java.lang.Boolean)
     */
    @Override
    public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail) {
        this.delegate.append(buffer, fieldName, array, fullDetail);
    }

}
