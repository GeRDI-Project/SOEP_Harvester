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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.ETLPreconditionException;
import de.gerdiproject.harvest.github.json.GitHubCommit;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;
import de.gerdiproject.harvest.soep.dataset_mapping.DatasetMetadata;
import de.gerdiproject.harvest.soep.utils.JGitUtil;
import de.gerdiproject.harvest.utils.data.HttpRequester;

/**
 * This extractor retrieves SOEP datasets from a GitHub repository.
 *
 * @author Fidan Limani, Robin Weiss
 */
public class SoepExtractor extends AbstractIteratorExtractor<SoepFileVO>
{
    private HttpRequester httpRequester = new HttpRequester(new Gson(), StandardCharsets.UTF_8);
    private JGitUtil soepGitHub;
    private Map<String, DatasetMetadata> metadataMap;
    private DirectoryStream<Path> datasetStream;
    private String commitHash = null;


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.commitHash = getLatestCommitHash();

        // set up git reader if it does not exist yet
        if (this.soepGitHub == null) {
            try {
                this.soepGitHub = new JGitUtil(SoepConstants.SOEP_REMOTE_REPO_NAME, SoepConstants.SOEP_REMOTE_REPO_URL);
            } catch (IOException e) {
                throw new ETLPreconditionException(SoepLoggingConstants.IO_EXCEPTION_ERROR, e);
            }
        }

        // get data from remote git repository
        try {
            soepGitHub.collect();
        } catch (IOException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.IO_EXCEPTION_ERROR, e);

        } catch (GitAPIException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.GIT_API_EXCEPTION_ERROR, e);
        }

        // get metadata from CSV
        try {
            this.metadataMap = loadDatasetMetadata();
        } catch (IOException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.ERROR_READING_CSV_FILE, e);
        }

        // open stream to dataset folder
        try {
            final Path datasetDir = new File(String.format(SoepConstants.BASE_PATH, "")).toPath();
            this.datasetStream = Files.newDirectoryStream(datasetDir);
        } catch (IOException e) {
            throw new ETLPreconditionException(SoepLoggingConstants.ERROR_READING_DATASET_FILES, e);
        }
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
        // TODO Auto-generated method stub
        return super.size();
    }


    @Override
    protected Iterator<SoepFileVO> extractAll() throws ExtractorException
    {
        return new SoepFileIterator();
    }


    @Override
    public void clear()
    {
        super.clear();

        if (datasetStream != null) {
            try {
                datasetStream.close();
            } catch (IOException e) { // NOPMD - Nothing we can do. The open stream will not interfere with what we want to do
            }
        }

        datasetStream = null;
    }


    /**
     * Load dataset file descriptions from a CSV spreadsheet to a Map
     *
     * @throws IOException if the CSV file could not be read
     *
     * @return a Map of dataset names to {@linkplain DatasetMetadata}
     */
    public Map<String, DatasetMetadata> loadDatasetMetadata() throws IOException
    {
        final Map<String, DatasetMetadata> metadataMap = new HashMap<>();

        try
            (Reader reader = Files.newBufferedReader(Paths.get(SoepConstants.FILE_TITLE_DATASET))) {
            CsvToBean<DatasetMetadata> csvMapper = new CsvToBeanBuilder<DatasetMetadata>(reader)
            .withType(DatasetMetadata.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

            // Read records one by one in a Map<String, DatasetMetadata>
            Iterator<DatasetMetadata> csvIterator = csvMapper.iterator();

            while (csvIterator.hasNext()) {
                final DatasetMetadata metadata = csvIterator.next();
                metadataMap.put(metadata.getDatasetName(), metadata);
            }
        }

        return metadataMap;
    }


    /**
     * Returns metadata for a specified file.
     *
     * @param file the file for which metadata is to be retrieved
     *
     * @return the metadata of the file
     */
    public DatasetMetadata getFileMetadata(final File file)
    {
        final String datasetName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        return metadataMap.get(datasetName);
    }


    /**
     * This iterator uses a {@linkplain DirectoryStream} to iterate through local Soep datasets
     * and generates a {@linkplain SoepFileVO} for each file.
     *
     * @author Robin Weiss
     *
     */
    private class SoepFileIterator implements Iterator<SoepFileVO>
    {
        final Iterator<Path> sourceStreamIterator = datasetStream.iterator();


        @Override
        public boolean hasNext()
        {
            return sourceStreamIterator.hasNext();
        }


        @Override
        public SoepFileVO next()
        {
            final File file = sourceStreamIterator.next().toFile();
            return new SoepFileVO(file, getFileMetadata(file));
        }
    }
}
