package de.gerdiproject.harvest.soep.csv;

import lombok.Data;

/**
 *  This class models and maps SOEP variables from a repository file, to be retrieved during harvesting.
 *  @author Fidan Limani
 */
@Data
public class VariablesMetadata
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
     * @param variablesAttributes read from "variable.csv".
     */
    public VariablesMetadata(String[] variablesAttributes)
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