package de.gerdiproject.harvest.soep.csv;

import lombok.Data;

/**
 *  This class models and maps SOEP concepts from a repository file, to be retrieved during harvesting.
 *  @author Fidan Limani
 */
@Data
public class ConceptsMetadata
{
    private String conceptName;
    private String topic;
    private String topicPrefix;
    private String labelDE;
    private String label;

    /**
     * Creates a SOEP concept based on a metadata set
     * @param datasetAttributes read from "concepts.csv".
     */
    public ConceptsMetadata(String[] datasetAttributes)
    {
        this.conceptName = datasetAttributes[0];
        this.topic = datasetAttributes[1];
        this.topicPrefix = datasetAttributes[2];
        this.labelDE = datasetAttributes[3];
        this.label = datasetAttributes[4];
    }
}