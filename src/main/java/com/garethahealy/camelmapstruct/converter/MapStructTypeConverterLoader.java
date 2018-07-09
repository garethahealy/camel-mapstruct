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

import java.util.Map;

import com.google.common.collect.Table;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.support.ServiceSupport;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapStructTypeConverterLoader<M> extends ServiceSupport implements CamelContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(MapStructTypeConverterLoader.class);

    private CamelContext camelContext;
    private MapStructMapperConfiguration<M> configuration;

    public MapStructTypeConverterLoader() {

    }

    public MapStructTypeConverterLoader(CamelContext camelContext, Class<M> mapperInterface) {
        this(camelContext, new MapStructMapperConfiguration<M>(mapperInterface));
    }

    public MapStructTypeConverterLoader(CamelContext camelContext, MapStructMapperConfiguration<M> configuration) {
        setCamelContext(camelContext);
        this.configuration = configuration;
    }

    public void init() {
        if (camelContext == null) {
            throw new IllegalArgumentException("CamelContext == null");
        }

        TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
        MapStructTypeConverter converter = new MapStructTypeConverter<M>(configuration);

        Table<Class<?>, Class<?>, String> resolved = configuration.getResolvedMappingMethods();
        for (Map.Entry<Class<?>, Map<Class<?>, String>> row : resolved.rowMap().entrySet()) {
            for (Map.Entry<Class<?>, String> col : row.getValue().entrySet()) {
                Class<?> from = row.getKey();
                Class<?> to = col.getKey();

                LOG.debug("{} -> {} via {}->{}", from, to, configuration.getMapperInterface().getName(), col.getValue());
                LOG.info("Added MapStruct as Camel type converter: {} -> {}", from, to);

                registry.addTypeConverter(to, from, converter);
            }
        }
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        if (this.camelContext == null) {
            this.camelContext = camelContext;
            try {
                camelContext.addService(this);
            } catch (Exception e) {
                throw ObjectHelper.wrapRuntimeCamelException(e);
            }
        }
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    protected void doStart() throws Exception {
        init();
    }

    @Override
    protected void doStop() throws Exception {
        // noop
    }
}
