/*-
 * #%L
 * GarethHealy :: Camel MapStruct
 * %%
 * Copyright (C) 2013 - 2018 Gareth Healy
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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Table;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapStructTypeConverter<M> extends TypeConverterSupport {

    private static final Logger LOG = LoggerFactory.getLogger(MapStructTypeConverter.class);

    private final MapStructMapperConfiguration<M> configuration;
    private final Map<String, String> cache = new ConcurrentHashMap<String, String>();

    private M mapper;

    public MapStructTypeConverter(MapStructMapperConfiguration<M> configuration) {
        this.configuration = configuration;

        try {
            this.mapper = Mappers.getMapper(configuration.getMapperInterface());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        T answer;
        try {
            String mapperMethodName = getFromCacheOrFindMethodName(type, value);

            answer = (T)MethodUtils.invokeMethod(mapper, mapperMethodName, value);
        } catch (NoSuchMethodException e) {
            throw new TypeConversionException(value, type, e);
        } catch (IllegalAccessException e) {
            throw new TypeConversionException(value, type, e);
        } catch (InvocationTargetException e) {
            throw new TypeConversionException(value, type, e);
        }

        return answer;
    }

    private String getFromCacheOrFindMethodName(Class<?> type, Object value) throws IllegalAccessException {
        Class<?> from = value.getClass();
        Class<?> to = type;
        String key = String.format("%s->%s", from.getCanonicalName(), to.getCanonicalName());

        LOG.trace("Cached method key: {}", key);
        LOG.debug("Looking for mapping method for {} -> {}", from.getCanonicalName(), to.getCanonicalName());

        String methodName;
        if (cache.containsKey(key)) {
            methodName = cache.get(key);

            LOG.debug("Found method in cache: {}", methodName);
        } else {
            methodName = findMethodName(from, to);

            LOG.debug("Adding method into cache: {}", methodName);

            cache.put(key, methodName);
        }

        return methodName;
    }

    private String findMethodName(Class<?> from, Class<?> to) throws IllegalAccessException {
        String methodName;

        Table<Class<?>, Class<?>, String> resolved = configuration.getResolvedMappingMethods();
        if (resolved.containsRow(from) && resolved.containsColumn(to)) {
            methodName = resolved.get(from, to);
        } else {
            String innerMessage = "Did'nt find method on mapper "
                                  + mapper.getClass().getCanonicalName() + " that container a parameter of "
                                  + from.getCanonicalName() + "; Found possible matches: "
                                  + StringUtils.join(resolved.values(), ", ");

            LOG.error(innerMessage);

            throw new IllegalAccessException(innerMessage);
        }

        return methodName;
    }
}
