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
 *  This class retrieves SOEP dataset file attributes, to be used during harvesting.
 *  @author Fidan Limani
 */
@Data
public class DatasetMetadata
{
    private String studyName;
    private String datasetName;
    private String periodName;
    private String analysisUnitName;
    private String conceptualDatasetName;
    private String label;

    private String description;


    /**
     * Creates a SOEP variable based on a metadata set.
     *
     * @param row a row read from "datasets.csv"
     */
    public DatasetMetadata(final String... row)
    {
        this.studyName = row[0];
        this.datasetName = row[1];
        this.periodName = row[2];
        this.analysisUnitName = row[3];
        this.conceptualDatasetName = row[4];
        this.label = row[5];
        this.description = row[6];
    }
}