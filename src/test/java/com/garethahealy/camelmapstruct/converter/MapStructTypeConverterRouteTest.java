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

import com.garethahealy.camelmapstruct.entities.Bitter;
import com.garethahealy.camelmapstruct.entities.Lager;
import com.garethahealy.camelmapstruct.mappers.BeerMapper;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

public class MapStructTypeConverterRouteTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                new MapStructTypeConverterLoader<BeerMapper>(getContext(), BeerMapper.class);

                from("direct:input")
                    .convertBodyTo(Bitter.class)
                    .log("${body}")
                    .to("mock:output");
            }
        };
    }

    @Test
    public void canConvertBodyTo() throws InvalidPayloadException {
        Lager lager = new Lager(101L, "Stella", "InBev", 4.9, new Integer[] {255, 194, 0}, "Standard");

        MockEndpoint mock = getMockEndpoint("mock:output");

        template.sendBody("direct:input", lager);

        Assert.assertNotNull(mock.getExchanges());
        Assert.assertEquals(1, mock.getExchanges().size());

        Bitter bitter = mock.getExchanges().get(0).getIn().getMandatoryBody(Bitter.class);

        Assert.assertNotNull(bitter);
        Assert.assertNotNull(bitter.getId());
        Assert.assertNotNull(bitter.getName());
        Assert.assertNotNull(bitter.getBrewery());
        Assert.assertNotNull(bitter.getStrength());
        Assert.assertNotNull(bitter.getColour());
        Assert.assertNotNull(bitter.getTaste());

        Assert.assertEquals(String.valueOf(lager.getId()), bitter.getId());
        Assert.assertEquals(lager.getName(), bitter.getName());
        Assert.assertEquals(lager.getBreweryId(), bitter.getBrewery());
        Assert.assertEquals(String.valueOf(lager.getPercentage()), bitter.getStrength());
        Assert.assertEquals("255,194,0", bitter.getColour());
        Assert.assertEquals(lager.getTastingNote(), bitter.getTaste());
    }
}
