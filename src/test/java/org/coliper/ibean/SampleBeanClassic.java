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

import java.util.Date;

/**
 * @author alex@coliper.org
 *
 */
public interface SampleBeanClassic {

//@formatter:off 
    void setIntObject(Integer i);
    Integer getIntObject();

    void setBooleanPrimitive(boolean b);
    boolean isBooleanPrimitive();

    void setDate(Date d);
    Date getDate();

    void setString(String s);
    String getString();

    SampleBeanClassic getSelf();
    void setSelf(SampleBeanClassic s);
//@formatter:on    
}
