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
package com.garethahealy.camelmapstruct.entities;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Lager {

    private Long id;
    private String name;
    private String breweryId;
    private Double percentage;
    private Integer[] colour;
    private String tastingNote;

    public Lager(Long id, String name, String breweryId, Double percentage, Integer[] colour, String tastingNote) {
        this.id = id;
        this.name = name;
        this.breweryId = breweryId;
        this.percentage = percentage;
        this.colour = colour.clone();
        this.tastingNote = tastingNote;
    }

    public Lager() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreweryId() {
        return breweryId;
    }

    public void setBreweryId(String breweryId) {
        this.breweryId = breweryId;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Integer[] getColour() {
        if (colour != null) {
            return colour.clone();
        }
        
        return new Integer[0];
    }

    public void setColour(Integer[] colour) {
        this.colour = colour.clone();
    }

    public String getTastingNote() {
        return tastingNote;
    }

    public void setTastingNote(String tastingNote) {
        this.tastingNote = tastingNote;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("breweryId", breweryId)
            .append("percentage", percentage)
            .append("colour", colour)
            .append("tastingNote", tastingNote)
            .toString();
    }
}
