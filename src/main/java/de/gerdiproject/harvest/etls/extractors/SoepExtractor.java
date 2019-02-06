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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.nio.file.DirectoryStream;

import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.ETLPreconditionException;
import de.gerdiproject.harvest.github.json.GitHubCommit;
import de.gerdiproject.harvest.github.json.GitHubContent;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;
import de.gerdiproject.harvest.soep.csv.ConceptMetadata;
import de.gerdiproject.harvest.soep.csv.DatasetMetadata;
import de.gerdiproject.harvest.soep.csv.VariableMetadata;
import de.gerdiproject.harvest.utils.data.HttpRequester;
import de.gerdiproject.harvest.utils.data.enums.RestRequestType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extractor retrieves SOEP datasets from a GitHub repository.
 *
 * @author Fidan Limani, Robin Weiss
 */
public class SoepExtractor extends AbstractIteratorExtractor<SoepFileVO>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SoepExtractor.class);

    private HttpRequester httpRequester = new HttpRequester();
    private Map<String, DatasetMetadata> datasetDescriptions;
    private Map<String, VariableMetadata> variableDescriptions;
    private Map<String, ConceptMetadata> conceptDescriptions;
    private Iterator<GitHubContent> datasetIterator;
    private String commitHash = null;
    private int size = -1;


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.commitHash = getLatestCommitHash();

        // Get metadata from CSV, including datasets, variables that describe them, and concepts of these variables
        try {
            this.datasetDescriptions = loadDatasetMetadata();
            this.variableDescriptions = loadVariableMetadata();
            this.conceptDescriptions = loadConceptMetadata();
        } catch (IOException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.ERROR_READING_CSV_FILE, e);
        }

        // Get list of datasets
        final List<GitHubContent> datasetContents = httpRequester.getObjectFromUrl(SoepConstants.DATASETS_CONTENT_URL,
                                                                                   SoepConstants.LIST_TYPE);

        // Set size and iterator
        this.size = datasetContents.size();
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
        final List<GitHubCommit> datasetCommits = httpRequester.getObjectFromUrl(SoepConstants.DATASET_COMMITS_URL,
                                                                                 SoepConstants.LIST_TYPE);

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
        return size;
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
        // Download dataset CSV file content
        LOGGER.info("Loading the SOEP datasets...");
        final String csvContent = httpRequester.getRestResponse(
                                      RestRequestType.GET,
                                      SoepConstants.DATASETS_CSV_DOWNLOAD_URL,
                                      null);

        // Parse "datasets" CSV file
        final Map<String, DatasetMetadata> metadataMap = new HashMap<>();

        try
            (Reader reader = new BufferedReader(new StringReader(csvContent))) {
            CsvToBean<DatasetMetadata> csvMapper = new CsvToBeanBuilder<DatasetMetadata>(reader)
            .withType(DatasetMetadata.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

            // Read records one by one in a Map<String, DatasetMetadata> instance
            final Iterator<DatasetMetadata> csvIterator = csvMapper.iterator();

            while (csvIterator.hasNext()) {
                final DatasetMetadata metadata = csvIterator.next();
                metadataMap.put(metadata.getDatasetName(), metadata);
            }
        }

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
        // Download concepts CSV file content
        LOGGER.info("Loading SOEP concepts dataset...");
        final String csvContent = httpRequester.getRestResponse(
                                      RestRequestType.GET,
                                      SoepConstants.CONCEPTS_CSV_DOWNLOAD_URL,
                                      null);

        // Parse "concepts" CSV file
        final Map<String, ConceptMetadata> conceptDescription = new HashMap<>();

        // When reading CSV content, skip table header, hence withSkipLines(1) invoked
        try
            (Reader reader = new BufferedReader(new StringReader(csvContent));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            // Read records one by one; put them in a List<ConceptMetadata>
            ConceptMetadata cm;
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                cm = new ConceptMetadata(nextRecord);
                conceptDescription.put(cm.getConceptName().toLowerCase(), cm);
            }
        }

        return conceptDescription;
    }


    /**
     * Load concept file descriptions from a CSV file to a List.
     *
     * @throws IOException if the CSV file could not be read
     * @return a List of concept names to {@linkplain DatasetMetadata}
     */
    public Map<String, VariableMetadata> loadVariableMetadata() throws IOException
    {
        // Download variables CSV file content
        LOGGER.info("Loading SOEP variables...");
        final String csvContent = httpRequester.getRestResponse(
                                      RestRequestType.GET,
                                      SoepConstants.VARIABLES_CSV_DOWNLOAD_URL,
                                      null);

        // Parse "variables" CSV file
        final Map<String, VariableMetadata> variablesDescription = new HashMap<>();

        // When reading CSV content, skip table header, hence: withSkipLines(1)
        try
            (Reader reader = new BufferedReader(new StringReader(csvContent));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            // Read records one by one; put them in a Map<String, VariableMetadata>
            VariableMetadata vm;
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                vm = new VariableMetadata(nextRecord);
                variablesDescription.put(vm.getVariableName().toLowerCase(), vm);
            }
        }

        return variablesDescription;
    }


    /**
     * Returns metadata for a specified file.
     *
     * @param content information about the dataset
     * @return the metadata of the file
     */
    public DatasetMetadata getDatasetMetadata(final GitHubContent content)
    {
        final String datasetName = content.getName().substring(0, content.getName().lastIndexOf('.'));
        return datasetDescriptions.get(datasetName);
    }


    /**
     * Retrieve records that contain the variables associated with a dataset.
     * @param datasetName Dataset name
     * @return List<VariableMetadata> A list of VariableMetadata "records"
     **/
    private List<VariableMetadata> getVariableMetadataRecords(final String datasetName)
    {
        List<VariableMetadata> variableMetadataRecords = new LinkedList<>();

        // Loop through <variableDescriptions> and add the elements that match the dataset name
        for (VariableMetadata vm : variableDescriptions.values()) {
            if (vm.getDatasetName().equalsIgnoreCase(datasetName))
                variableMetadataRecords.add(vm);
        }

        return variableMetadataRecords;
    }


    /**
     * Associate variable names of a dataset with ConceptMetadata.
     * @param variableMetadata List of VariableMetadata elements that describe the dataset at hand.
     * @return List<VariableMetadata> A map of variable name - ConceptMetadata "records"
     **/
    private Map<String, ConceptMetadata> getVariableConceptMap(List<VariableMetadata> variableMetadata)
    {
        // input: Map<String, VariableMetadata> variableDescriptions
        Map<String, ConceptMetadata> conceptMetadataRecords = new HashMap<>();

        // 1. Loop through VariableMetadata from the input, and add the ones matching the dataset name
        for (VariableMetadata vm : variableMetadata)
            conceptMetadataRecords.put(vm.getVariableName(), conceptDescriptions.get(vm.getConceptName()));

        return conceptMetadataRecords;
    }


    /**
     * This iterator uses a {@linkplain DirectoryStream} to iterate through local SOEP datasets
     * and generates a {@linkplain SoepFileVO} for each file.
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

            final List<VariableMetadata> variableMetadataRecords = getVariableMetadataRecords(datasetName);
            final Map<String, ConceptMetadata> variableConceptMetadataRecords = getVariableConceptMap(variableMetadataRecords);

            return new SoepFileVO(content, datasetMetadata, variableMetadataRecords, variableConceptMetadataRecords);
        }

        private String getDatasetName(GitHubContent content)
        {
            return content.getName().substring(0, content.getName().lastIndexOf('.')).toLowerCase();
        }
    }
}