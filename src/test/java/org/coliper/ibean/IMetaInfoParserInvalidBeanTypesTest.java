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

package org.coliper.ibean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class IMetaInfoParserInvalidBeanTypesTest {

    private final IBeanMetaInfoParser parser;

    /**
     * @param parser
     */
    public IMetaInfoParserInvalidBeanTypesTest() {
        this.parser = new IBeanMetaInfoParser();
    }

    // @formatter:off
    public static abstract class AbstractClass {
        abstract void setX(int i);
        abstract int getX();
    } 
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testAbstractClass() {
        this.parser.parse(AbstractClass.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static class RegularClass {
        void setX(int i) {};
        int getX() { return 0; }
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testRegularClass() {
        this.parser.parse(RegularClass.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface IncompatibleTypes {
        void setX(Boolean b);
        boolean getX();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testIncompatibleTypes() {
        this.parser.parse(IncompatibleTypes.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface IncompatibleDerivedTypes {
        void x(Collection<?> b);
        List<?> x();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testIncompatibleDerivedTypes() {
        this.parser.parse(IncompatibleDerivedTypes.class, BeanStyle.MODERN,
                Collections.emptyList());
    }

    // @formatter:off
    public static interface IncompatibleNames {
        void setX(int i);
        int getY();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testIncompatibleNames() {
        this.parser.parse(IncompatibleNames.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface IncompatibleCapitalization {
        void somefield(int i);
        int someField();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testIncompatibleCapitalization() {
        this.parser.parse(IncompatibleCapitalization.class, BeanStyle.MODERN,
                Collections.emptyList());
    }

    public static interface OnlySetter {
        void setX(int i);
    }

    @Test(expected = InvalidIBeanTypeException.class)
    public void testOnlySetter() {
        this.parser.parse(OnlySetter.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    public static interface OnlyGetter {
        int getX();
    }

    @Test(expected = InvalidIBeanTypeException.class)
    public void testOnlyGetter() {
        this.parser.parse(OnlyGetter.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface ShortNames {
        void set(String s);
        String get();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testShortNames() {
        this.parser.parse(ShortNames.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface IncompatibleSubtype {
        void setX(List<String> l);
        ArrayList<String> getX();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testIncompatibleSubtype() {
        this.parser.parse(IncompatibleSubtype.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface SetterWithMoreParams {
        void setX(int x, int y);
        int getX();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testSetterWithMoreParams() {
        this.parser.parse(SetterWithMoreParams.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface GetterWithParam {
        void setX(byte b);
        byte getX(byte b);
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testGetterWithParam() {
        this.parser.parse(GetterWithParam.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface WrongSetterPrefix {
        void SetX(String x);
        String getX();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testWrongSetterPrefix() {
        this.parser.parse(WrongSetterPrefix.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    // @formatter:off
    public static interface WrongGetterPrefix {
        void setX(Boolean b);
        Boolean GetX();
    }
    // @formatter:on

    @Test(expected = InvalidIBeanTypeException.class)
    public void testWrongGetterPrefix() {
        this.parser.parse(WrongGetterPrefix.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    @Test(expected = InvalidIBeanTypeException.class)
    public void testWrongStyle1() {
        this.parser.parse(SampleBeanModern.class, BeanStyle.CLASSIC, Collections.emptyList());
    }

    @Test(expected = InvalidIBeanTypeException.class)
    public void testWrongStyle2() {
        this.parser.parse(SampleBeanClassic.class, BeanStyle.MODERN, Collections.emptyList());
    }
}
