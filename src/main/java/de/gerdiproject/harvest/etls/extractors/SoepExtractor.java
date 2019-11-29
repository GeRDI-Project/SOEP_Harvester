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
package de.gerdiproject.harvest.etls.extractors;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.ETLPreconditionException;
import de.gerdiproject.harvest.github.json.GitHubCommit;
import de.gerdiproject.harvest.github.json.GitHubContent;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;
import de.gerdiproject.harvest.soep.csv.ConceptMetadata;
import de.gerdiproject.harvest.soep.csv.DatasetMetadata;
import de.gerdiproject.harvest.soep.csv.VariableMetadata;
import de.gerdiproject.harvest.utils.CsvRequester;
import de.gerdiproject.json.GsonUtils;

/**
 * This extractor retrieves SOEP datasets from a GitHub repository.
 *
 * @author Fidan Limani, Robin Weiss
 */
public class SoepExtractor extends AbstractIteratorExtractor<SoepFileVO>
{
    private final CsvRequester csvRequester = new CsvRequester();
    protected Map<String, DatasetMetadata> datasetDescriptions;
    protected Map<String, List<VariableMetadata>> variableDescriptions;
    protected Map<String, ConceptMetadata> conceptDescriptions;
    protected Iterator<GitHubContent> datasetIterator;

    private String commitHash;
    private int datasetCount = -1;


    @Override
    public void init(final AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.commitHash = getLatestCommitHash();

        // Get metadata from CSV, including datasets, variables that describe them, and concepts of these variables
        try {
            this.datasetDescriptions = loadDatasetMetadata();
            this.variableDescriptions = loadVariableMetadata();
            this.conceptDescriptions = loadConceptMetadata();
        } catch (final IOException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.ERROR_READING_CSV_FILE, e);
        }

        // Get list of datasets
        final List<GitHubContent> datasetContents = csvRequester.getObjectFromUrl(
                                                        SoepConstants.DATASETS_CONTENT_URL,
                                                        SoepConstants.CONTENT_LIST_TYPE);

        // Set size and iterator
        this.datasetCount = datasetContents.size();
        this.datasetIterator = datasetContents.iterator();
    }


    /**
     * Sends a "commits" request to the GitHub REST API
     * and retrieves the commit hash of the latest commit that changed
     * the datasets folder.
     *
     * @return the commit hash of the latest commit that changed
     * the datasets folder
     */
    private String getLatestCommitHash()
    {
        final List<GitHubCommit> datasetCommits = csvRequester.getObjectFromUrl(
                                                      SoepConstants.DATASET_COMMITS_URL,
                                                      SoepConstants.COMMIT_LIST_TYPE);

        // get sha of latest commit
        return datasetCommits.isEmpty() ? null : datasetCommits.get(0).getSha();
    }


    @Override
    public String getUniqueVersionString()
    {
        return commitHash;
    }


    @Override
    public int size()
    {
        return datasetCount;
    }


    @Override
    protected Iterator<SoepFileVO> extractAll() throws ExtractorException
    {
        return new SoepFileIterator();
    }


    /**
     * Load dataset file descriptions from a CSV file to a Map.
     *
     * @return a Map of dataset names to {@linkplain DatasetMetadata}
     * @throws IOException if the CSV file could not be read
     */
    public Map<String, DatasetMetadata> loadDatasetMetadata() throws IOException
    {
        final Map<String, DatasetMetadata> metadataMap = new HashMap<>();

        final Consumer<String[]> addFunction = (final String... row) -> {
            final DatasetMetadata dm = new DatasetMetadata(row);
            metadataMap.put(dm.getDatasetName(), dm);
        };

        csvRequester.parseCsv(
            SoepConstants.DATASETS_CSV_DOWNLOAD_URL,
            addFunction);

        return metadataMap;
    }


    /**
     * Load concept file descriptions from a CSV file to a List.
     *
     * @return a List of concept names to {@linkplain DatasetMetadata}
     * @throws IOException if the CSV file could not be read
     */
    public Map<String, ConceptMetadata> loadConceptMetadata() throws IOException
    {
        final Map<String, ConceptMetadata> conceptsDescription = new HashMap<>();

        final Consumer<String[]> addFunction = (final String... row) -> {
            final ConceptMetadata cm = new ConceptMetadata(row);
            conceptsDescription.put(cm.getConceptName(), cm);
        };

        // Parse "concepts" CSV file
        csvRequester.parseCsv(
            SoepConstants.CONCEPTS_CSV_DOWNLOAD_URL,
            addFunction);

        return conceptsDescription;
    }


    /**
     * Load the variable descriptions for every dataset.
     *
     * @throws IOException if the CSV file could not be read
     * @return a Map of datasets and their corresponding variable metadata {@linkplain VariableMetadata}
     */
    public Map<String, List<VariableMetadata>> loadVariableMetadata() throws IOException
    {
        final Map<String, List<VariableMetadata>> variableMap = new HashMap<>();

        final Consumer<String[]> addFunction = (final String... row) -> {
            final VariableMetadata vm = new VariableMetadata(row);
            final String key = vm.getDatasetName();
            final List<VariableMetadata> variableMetadata = variableMap.computeIfAbsent(key, k -> new LinkedList<>());

            variableMetadata.add(vm);
        };

        // Parse "variables" CSV file
        csvRequester.parseCsv(
            SoepConstants.VARIABLES_CSV_DOWNLOAD_URL,
            addFunction);

        return variableMap;
    }


    /**
     * This iterator uses a {@linkplain DirectoryStream} to iterate through local SOEP datasets and generates a
     * {@linkplain SoepFileVO} for each file.
     *
     * @author Robin Weiss
     */
    private class SoepFileIterator implements Iterator<SoepFileVO>
    {
        @Override
        public boolean hasNext()
        {
            return datasetIterator.hasNext();
        }


        @Override
        public SoepFileVO next()
        {
            final GitHubContent content = datasetIterator.next();
            final String datasetName = getDatasetName(content);

            final DatasetMetadata datasetMetadata = datasetDescriptions.get(datasetName);

            // Abort if there is no metadata
            if (datasetMetadata == null)
                return null;

            final List<VariableMetadata> variableMetadataRecords = variableDescriptions.get(datasetName);
            final Map<String, ConceptMetadata> variableConceptMetadataRecords = getVariableConceptMap(variableMetadataRecords);

            System.out.println(GsonUtils.createGerdiDocumentGsonBuilder().create().toJson(new SoepFileVO(content, datasetMetadata, variableMetadataRecords, variableConceptMetadataRecords)));
            return new SoepFileVO(content, datasetMetadata, variableMetadataRecords, variableConceptMetadataRecords);
        }


        private String getDatasetName(final GitHubContent content)
        {
            return content.getName().substring(0, content.getName().lastIndexOf('.'));
        }


        /**
         * Associate variable names of a dataset with ConceptMetadata.
         *
         * @param variableMetadata List of VariableMetadata elements that describe the dataset at hand.
         * @return List<VariableMetadata> A map of variable name - ConceptMetadata "records"
         **/
        private Map<String, ConceptMetadata> getVariableConceptMap(final List<VariableMetadata> variableMetadata)
        {
            // input: Map<String, VariableMetadata> variableDescriptions
            final Map<String, ConceptMetadata> conceptMetadataRecords = new HashMap<>();

            // 1. Loop through VariableMetadata from the input, and add the ones matching the dataset name
            for (final VariableMetadata vm : variableMetadata) {
                final String key = vm.getConceptName();

                if (!key.isEmpty())
                    conceptMetadataRecords.put(key, conceptDescriptions.get(key));
            }

            return conceptMetadataRecords;
        }
    }


    @Override
    public void clear()
    {
        // nothing to clean up
    }
}