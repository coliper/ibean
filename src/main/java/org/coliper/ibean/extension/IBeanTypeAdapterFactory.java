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

package org.coliper.ibean.extension;

import java.io.IOException;

import org.coliper.ibean.IBean;
import org.coliper.ibean.IBeanFactory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author alex@coliper.org
 *
 */
public class IBeanTypeAdapterFactory implements TypeAdapterFactory {
    
    private static IBeanFactory globalIBeanFactory = null;
    
    public void setGlobalIBeanFactory(IBeanFactory factory) {
        globalIBeanFactory = factory;
    }
    
    
    private final IBeanFactory iBeanFactory;
    
    /**
     * 
     */
    public IBeanTypeAdapterFactory() {
        this(null);
    }

    /**
     * @param iBeanFactory
     */
    public IBeanTypeAdapterFactory(IBeanFactory iBeanFactory) {
        this.iBeanFactory = iBeanFactory;
    }

    /* (non-Javadoc)
     * @see com.google.gson.TypeAdapterFactory#create(com.google.gson.Gson, com.google.gson.reflect.TypeToken)
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return new IBeanTypeAdapter<T>(type).nullSafe();
    }
    
    private class IBeanTypeAdapter<T> extends TypeAdapter<T> {
        
        private final TypeToken<T> typeToken;
        
        /**
         * @param typeToken
         */
        public IBeanTypeAdapter(TypeToken<T> typeToken) {
            this.typeToken = typeToken;
        }

        /* (non-Javadoc)
         * @see com.google.gson.TypeAdapter#write(com.google.gson.stream.JsonWriter, java.lang.Object)
         */
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            ((GsonSupport)value).jsonWrite(out);
        }

        /* (non-Javadoc)
         * @see com.google.gson.TypeAdapter#read(com.google.gson.stream.JsonReader)
         */
        @Override
        public T read(JsonReader in) throws IOException {
            final T bean = this.createBean();
            in.beginObject();
            ((GsonSupport)bean).jsonRead(in);
            in.endObject();
            return bean;
        }
        
        private T createBean() {
            if (IBeanTypeAdapterFactory.this.iBeanFactory != null) {
                return (T)IBeanTypeAdapterFactory.this.iBeanFactory.create(this.typeToken.getRawType());
            }
            if (IBeanTypeAdapterFactory.globalIBeanFactory != null) {
                return (T)IBeanTypeAdapterFactory.globalIBeanFactory.create(this.typeToken.getRawType());
            }
            return (T) IBean.newOf(this.typeToken.getRawType());
        }
        
    }

}
