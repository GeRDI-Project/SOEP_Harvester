package de.gerdiproject.harvest.soep.disciplinary;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class models the SOEP-specific metadata: Concepts
 * @author Fidan Limani
 **/

@AllArgsConstructor
@Data
public class Concept
{
    private String name;
    private String labelDE;
    private String label;
}