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
package com.garethahealy.camelmapstruct.mappers;

public class ColourMapper {

    public String toString(Integer[] colour) {
        if (colour == null) {
            return null;
        }

        Integer red = colour[0];
        Integer green = colour[1];
        Integer blue = colour[2];

        return String.format("%s,%s,%s", String.valueOf(red), String.valueOf(green), String.valueOf(blue));
    }

    public Integer[] toInteger(String colour) {
        String[] colourSplit = colour.split(",");
        String red = colourSplit[0];
        String green = colourSplit[1];
        String blue = colourSplit[2];

        return new Integer[] {Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue)};
    }
}
