/*
 * Copyright © 2017 Fidan Limani (http://www.gerdi-project.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gerdiproject.harvest.soep.csv;

import lombok.Data;

/**
 *  This class models and maps SOEP concepts from a repository file, to be retrieved during harvesting.
 *  @author Fidan Limani
 */
@Data
public class ConceptMetadata
{
    private String conceptName;
    private String topic;
    private String topicName;
    private String labelDE;
    private String label;

    /**
     * Creates a SOEP concept based on a metadata set
     * @param datasetAttributes read from "concepts.csv".
     */
    public ConceptMetadata(String[] datasetAttributes)
    {
        this.conceptName = datasetAttributes[0];
        this.topic = datasetAttributes[1];
        this.topicName = datasetAttributes[2];
        this.labelDE = datasetAttributes[3];
        this.label = datasetAttributes[4];
    }
}