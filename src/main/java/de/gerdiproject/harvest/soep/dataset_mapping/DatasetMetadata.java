/**
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
package de.gerdiproject.harvest.soep.dataset_mapping;

import com.opencsv.bean.CsvBindByName;

/**
 *  This class retrieves SOEP dataset file attributes, to be used during harvesting.
 *  @author Fidan Limani
 */
public class DatasetMetadata
{
    @CsvBindByName(column = "study_name", required = true)
    private String studyName;

    @CsvBindByName(column = "dataset_name", required = true)
    private String datasetName;

    @CsvBindByName(column = "period_name", required = true)
    private String periodName;

    @CsvBindByName(column = "analysis_unit_name")
    private String analysisUnitName;

    @CsvBindByName(column = "conceptual_dataset_name")
    private String conceptualDatasetName;

    @CsvBindByName(column = "label")
    private String label;

    @CsvBindByName(column = "description")
    private String description;

    // Getters
    public String getStudyName()
    {
        return studyName;
    }

    public String getDatasetName()
    {
        return datasetName;
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public String getAnalysisUnitName()
    {
        return analysisUnitName;
    }

    public String getConceptualDatasetName()
    {
        return conceptualDatasetName;
    }

    public String getLabel()
    {
        return label;
    }

    public String getDescription()
    {
        return description;
    }

    // Setters
    public void setStudyName(String studyName)
    {
        this.studyName = studyName;
    }

    public void setDatasetName(String datasetName)
    {
        this.datasetName = datasetName;
    }

    public void setPeriodName(String periodName)
    {
        this.periodName = periodName;
    }

    public void setAnalysisUnitName(String analysisUnitName)
    {
        this.analysisUnitName = analysisUnitName;
    }

    public void setConceptualDatasetName(String conceptualDatasetName)
    {
        this.conceptualDatasetName = conceptualDatasetName;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return getStudyName() + "\n"
               + getDatasetName() + "\n"
               + getPeriodName() + "\n"
               + getAnalysisUnitName() + "\n"
               + getConceptualDatasetName() + "\n"
               + getLabel() + "\n"
               + getDescription();
    }
}