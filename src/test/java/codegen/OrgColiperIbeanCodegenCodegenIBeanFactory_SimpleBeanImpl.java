package codegen;

import java.lang.CloneNotSupportedException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.coliper.ibean.codegen.CodegenIBeanFactory;
import org.coliper.ibean.extension.CloneableBean;

public final class OrgColiperIbeanCodegenCodegenIBeanFactory_SimpleBeanImpl implements CodegenIBeanFactory.SimpleBean {
  public CodegenIBeanFactory factory;

  private int _int;

  private String _str;

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(_str).append(_int).toHashCode();
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
    OrgColiperIbeanCodegenCodegenIBeanFactory_SimpleBeanImpl other =
        (OrgColiperIbeanCodegenCodegenIBeanFactory_SimpleBeanImpl)obj;
    return new EqualsBuilder().append(this._str, other._str).append(this._int, other._int)
        .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, this.factory.toStringStyle()).append(_str).append(_int)
        .toString();
  }

  @Override
  public int getInt() {
    int value = _int;
    return value;
  }

  @Override
  public String getStr() {
    String value = _str;
    return value;
  }

  @Override
  public void setInt(int newValue) {
    int value = newValue;
    _int = value;
  }

  @Override
  public void setStr(String newValue) {
    String value = newValue;
    _str = value;
  }

  @Override
  public CodegenIBeanFactory.SimpleBean clone() {
    try {
      return (CodegenIBeanFactory.SimpleBean)super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeException("unexpected", ex);
    }
  }

  @Override
  public CodegenIBeanFactory.SimpleBean deepClone() {
    final OrgColiperIbeanCodegenCodegenIBeanFactory_SimpleBeanImpl clone;
    try {
      clone = (OrgColiperIbeanCodegenCodegenIBeanFactory_SimpleBeanImpl)super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeException("unexpected", ex);
    }
    Object tmpVal;
    tmpVal = this._str;
    if (tmpVal != null && tmpVal instanceof CloneableBean) {
      tmpVal = ((CloneableBean)tmpVal).deepClone();
      clone._str = (String)tmpVal;
    } else {
      clone._str = (String)ObjectUtils.cloneIfPossible(this._str);
    }
    return clone;
  }
}
