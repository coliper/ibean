package codegen;

import java.lang.CloneNotSupportedException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.coliper.ibean.codegen.CodegenIBeanFactory;
import org.coliper.ibean.extension.CloneableBean;
import org.coliper.ibean.factory.AbstractCloningTest;

public final class OrgColiperIbeanFactoryAbstractCloningTest_NestedBeanImpl
        implements AbstractCloningTest.NestedBean {
    public CodegenIBeanFactory factory;

    private List _list;

    private AbstractCloningTest.SampleBeanClassicCloneable _sample;

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_list).append(_sample).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        OrgColiperIbeanFactoryAbstractCloningTest_NestedBeanImpl other =
                (OrgColiperIbeanFactoryAbstractCloningTest_NestedBeanImpl) obj;
        return new EqualsBuilder().append(this._list, other._list)
                .append(this._sample, other._sample).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, this.factory.toStringStyle()).append(_list).append(_sample)
                .toString();
    }

    @Override
    public List getList() {
        List value = _list;
        return value;
    }

    @Override
    public AbstractCloningTest.SampleBeanClassicCloneable getSample() {
        AbstractCloningTest.SampleBeanClassicCloneable value = _sample;
        return value;
    }

    @Override
    public void setList(List newValue) {
        List value = newValue;
        _list = value;
    }

    @Override
    public void setSample(AbstractCloningTest.SampleBeanClassicCloneable newValue) {
        AbstractCloningTest.SampleBeanClassicCloneable value = newValue;
        _sample = value;
    }

    @Override
    public AbstractCloningTest.NestedBean clone() {
        try {
            return (AbstractCloningTest.NestedBean) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("unexpected", ex);
        }
    }

    @Override
    public AbstractCloningTest.NestedBean deepClone() {
        final OrgColiperIbeanFactoryAbstractCloningTest_NestedBeanImpl clone;
        try {
            clone = (OrgColiperIbeanFactoryAbstractCloningTest_NestedBeanImpl) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("unexpected", ex);
        }
        if (this._list != null && this._list instanceof CloneableBean) {
            clone._list = List.class.cast(CloneableBean.class.cast(this._list).deepClone());
        } else {
            clone._list = ObjectUtils.cloneIfPossible(this._list);
        }
        if (this._sample != null && this._sample instanceof CloneableBean) {
            clone._sample = AbstractCloningTest.SampleBeanClassicCloneable.class
                    .cast(CloneableBean.class.cast(this._sample).deepClone());
        } else {
            clone._sample = ObjectUtils.cloneIfPossible(this._sample);
        }
        return clone;
    }
}
