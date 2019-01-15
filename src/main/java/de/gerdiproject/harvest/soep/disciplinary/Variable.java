package de.gerdiproject.harvest.soep.disciplinary;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class models the SOEP-specific metadata: Variables
 * @author Fidan Limani
 **/
@AllArgsConstructor
@Data
public class Variable
{
    // Initial set of attributes (new use cases might dictate an extension/change)
    private String variableName;
    private String source;
    private Concept concept;
}