/**
 * Copyright © 2017 Fidan Limani, Robin Weiss (http://www.gerdi-project.de)
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
 * This class models and maps SOEP variables from a repository file, to be retrieved during harvesting.
 *
 * @author Fidan Limani
 */
@Data
public class VariableMetadata
{
    private String studyName;
    private String datasetName;
    private String variableName;
    private String conceptName;
    private String source;
    private String itemID;
    private String id;

    /**
     * Creates a SOEP variable based on a metadata set
     *
     * @param variablesAttributes read from "variable.csv".
     */
    public VariableMetadata(final String[] variablesAttributes)
    {
        this.studyName = variablesAttributes[0];
        this.datasetName = variablesAttributes[1];
        this.variableName = variablesAttributes[2];
        this.conceptName = variablesAttributes[3];
        this.source = variablesAttributes[4];
        this.itemID = variablesAttributes[5];
        this.id = variablesAttributes[6];
    }
}