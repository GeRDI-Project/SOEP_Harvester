/*
 *  Copyright © 2018 Robin Weiss (http://www.gerdi-project.de/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package de.gerdiproject.harvest.etls.extractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.util.*;

import com.google.gson.reflect.TypeToken;
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
import de.gerdiproject.harvest.soep.csv.ConceptsMetadata;
import de.gerdiproject.harvest.soep.csv.DatasetMetadata;
import de.gerdiproject.harvest.soep.csv.VariablesMetadata;
import de.gerdiproject.harvest.soep.disciplinary.Variable;
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
    private Map<String, DatasetMetadata> metadataMap;
    private Iterator<GitHubContent> datasetIterator;
    private String commitHash = null;
    private int size;


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.commitHash = getLatestCommitHash();

        // get metadata from CSV
        try {
            this.metadataMap = loadDatasetMetadata();
        } catch (IOException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.ERROR_READING_CSV_FILE, e);
        }

        // Get list of datasets
        final Type listType = new TypeToken<List<GitHubContent>>() {} .getType();
        final List<GitHubContent> datasetContents = httpRequester.getObjectFromUrl(SoepConstants.DATASETS_CONTENT_URL, listType);

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
        final Type listType = new TypeToken<List<GitHubCommit>>() {} .getType();
        final List<GitHubCommit> datasetCommits = httpRequester.getObjectFromUrl(SoepConstants.DATASET_COMMITS_URL, listType);

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
     * @throws IOException if the CSV file could not be read
     *
     * @return a Map of dataset names to {@linkplain DatasetMetadata}
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

        try (Reader reader = new BufferedReader(new StringReader(csvContent)))
        {
            CsvToBean<DatasetMetadata> csvMapper = new CsvToBeanBuilder<DatasetMetadata>(reader)
            .withType(DatasetMetadata.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

            // Read records one by one in a Map<String, DatasetMetadata> instance

            for (DatasetMetadata metadata : (Iterable<DatasetMetadata>) csvMapper) {
                metadataMap.put(metadata.getDatasetName(), metadata);
            }
        }

        return metadataMap;
    }

    /**
     * Load concept file descriptions from a CSV file to a List.
     *
     * @throws IOException if the CSV file could not be read
     *
     * @return a List of concept names to {@linkplain DatasetMetadata}
     */
    public List<ConceptsMetadata> loadConceptsMetadata() throws IOException {
        // Download concepts CSV file content
        LOGGER.info("Loading SOEP concepts...");
        final String csvContent = httpRequester.getRestResponse(
                                        RestRequestType.GET,
                                        SoepConstants.CONCEPTS_CSV_DOWNLOAD_URL,
                                        null);

        // Parse "concepts" CSV file
        final List<ConceptsMetadata> conceptsDescription = new LinkedList<>();

        // When reading CSV content, skip table header, hence: withSkipLines(1)
        try (Reader reader = new BufferedReader(new StringReader(csvContent));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build())
        {
            // Read records one by one; put them in a List<ConceptsMetadata>
            ConceptsMetadata cm;
            String[] nextRecord;
            while((nextRecord = csvReader.readNext()) != null){
                cm = new ConceptsMetadata(nextRecord);
                conceptsDescription.add(cm);
            }
        } catch (IOException e) {
            LOGGER.error(String.format(SoepLoggingConstants.ERROR_READING_CSV_FILE,
                                        SoepLoggingConstants.ERROR_READING_CONCEPTS_FILES), e);
        }

        return conceptsDescription;
    }

    /**
     * Load concept file descriptions from a CSV file to a List.
     *
     * @throws IOException if the CSV file could not be read
     *
     * @return a List of concept names to {@linkplain DatasetMetadata}
     */
    public List<VariablesMetadata> loadVariablesMetadata() throws IOException
    {
        // Download variables CSV file content
        LOGGER.info("Loading SOEP variables...");
        final String csvContent = httpRequester.getRestResponse(
                                        RestRequestType.GET,
                                        SoepConstants.VARIABLES_CSV_DOWNLOAD_URL,
                                        null);

        // Parse "variables" CSV file
        final List<VariablesMetadata> variablesDescription = new LinkedList<>();

        // When reading CSV content, skip table header, hence: withSkipLines(1)
        try (Reader reader = new BufferedReader(new StringReader(csvContent));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build())
        {
            // Read records one by one; put them in a List<VariablesMetadata>
            VariablesMetadata vm;
            String[] nextRecord;
            while((nextRecord = csvReader.readNext()) != null){
                vm = new VariablesMetadata(nextRecord);
                variablesDescription.add(vm);
            }
        } catch (IOException e) {
            LOGGER.error(String.format(SoepLoggingConstants.ERROR_READING_CSV_FILE,
                                        SoepLoggingConstants.ERROR_READING_VARIABLES_FILES), e);
        }

        return variablesDescription;
    }


    /**
     * Returns metadata for a specified file.
     *
     * @param content information about the dataset
     *
     * @return the metadata of the file
     */
    public DatasetMetadata getDatasetMetadata(final GitHubContent content)
    {
        final String datasetName = content.getName().substring(0, content.getName().lastIndexOf('.'));
        return metadataMap.get(datasetName);
    }


    /**
     * This iterator uses a {@linkplain DirectoryStream} to iterate through local SOEP datasets
     * and generates a {@linkplain SoepFileVO} for each file.
     *
     * @author Robin Weiss
     *
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
            return new SoepFileVO(content, getDatasetMetadata(content));
        }
    }
}