/*-
 * #%L
 * GarethHealy :: Camel MapStruct
 * %%
 * Copyright (C) 2013 - 2017 Gareth Healy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.garethahealy.camelmapstruct.converter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.apache.camel.CamelContext;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapStructMapperConfiguration<T> {

    private static final Logger LOG = LoggerFactory.getLogger(MapStructMapperConfiguration.class);

    private final Class<T> mapperInterface;
    private final Table<Class<?>, Class<?>, String> resolvedMappingMethods = HashBasedTable.create();

    public MapStructMapperConfiguration(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void init() {
        if (resolvedMappingMethods.size() <= 0) {
            for (Method current : mapperInterface.getMethods()) {
                Class<?>[] params = current.getParameterTypes();
                if (params.length == 1 && current.getReturnType() != Void.TYPE) {
                    Class<?> to = current.getReturnType();
                    Class<?> from = params[0];

                    resolvedMappingMethods.put(from, to, current.getName());

                    LOG.debug("Possible mapping: {} -> {} via {}->{}",
                              from.getCanonicalName(), to.getCanonicalName(), mapperInterface.getCanonicalName(), current.getName());
                }
            }
        }
    }

    public Table<Class<?>, Class<?>, String> getResolvedMappingMethods() {
        if (resolvedMappingMethods.size() <= 0) {
            init();
        }

        return resolvedMappingMethods;
    }

    public List<ClassLoader> getClassLoaders(CamelContext camelContext) {
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>(4);
        classLoaders.add(mapperInterface.getClassLoader());

        if (camelContext.getApplicationContextClassLoader() != null) {
            Thread.currentThread().setContextClassLoader(camelContext.getApplicationContextClassLoader());
        }

        if (Thread.currentThread().getContextClassLoader() != null) {
            classLoaders.add(Thread.currentThread().getContextClassLoader());
        }

        classLoaders.add(Mappers.class.getClassLoader());

        return classLoaders;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }
}
