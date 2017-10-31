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

/**
 * @author alex@coliper.org
 *
 */
public interface PrimitivesBeanClassic {
//@formatter:off 
    void setByte(byte b);
    byte getByte();
    
    void setShort(short s);
    short getShort();
    
    void setInt(int i);
    int getInt();
    
    void setLong(long l);
    long getLong();
    
    void setFloat(float f);
    float getFloat();
    
    void setDouble(double d);
    double getDouble();
    
    void setBoolean(boolean b);
    boolean isBoolean();
    
    void setChar(char c);
    char getChar();

    void setByteObject(Byte b);
    Byte getByteObject();
    
    void setShortObject(Short s);
    Short getShortObject();
    
    void setIntObject(Integer i);
    Integer getIntObject();
    
    void setLongObject(Long l);
    Long getLongObject();
    
    void setFloatObject(Float f);
    Float getFloatObject();
    
    void setDoubleObject(Double d);
    Double getDoubleObject();
    
    void setBooleanObject(Boolean b);
    Boolean getBooleanObject();
    
    void setCharObject(Character c);
    Character getCharObject();
//@formatter:on    

}
