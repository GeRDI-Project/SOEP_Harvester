/**
 * Copyright © 2019 Fidan Limani (http://www.gerdi-project.de)
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
package de.gerdiproject.harvest.etls.extractors;

import de.gerdiproject.harvest.github.json.GitHubContent;
import de.gerdiproject.harvest.soep.csv.ConceptMetadata;
import de.gerdiproject.harvest.soep.csv.DatasetMetadata;
import de.gerdiproject.harvest.soep.csv.VariableMetadata;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * This value object holds a SOEP file and corresponding metadata.
 *
 * @author Robin Weiss, Fidan Limani
 */
@Data
public class SoepFileVO
{
    private final GitHubContent content;
    private final DatasetMetadata datasetMetadata;

    // VariableMetadata records from the CSV file that describe the dataset
    private final List<VariableMetadata> variableMetadataRecords;

    // Map variable name (as the key) to its concept metadata (as value)
    private final Map<String, ConceptMetadata> variableConceptRecordMap;
}