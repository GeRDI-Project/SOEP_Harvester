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
package de.gerdiproject.harvest.soep.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.opencsv.bean.CsvToBean;

import com.opencsv.bean.CsvToBeanBuilder;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;

import de.gerdiproject.harvest.soep.dataset_mapping.DatasetMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A util-like class to support repository- and GeRDI harvester-based operations. *
 * @author Fidan Limani
 */
public class SoepIO
{
    // A Map<String, String> field that stores descriptions for files
    private Map<String, DatasetMetadata> fileDescriptions;
    private static final Logger LOGGER = LoggerFactory.getLogger(SoepIO.class);

    /**
     * Constructor
     */
    public SoepIO()
    {
        this.fileDescriptions = new HashMap<>();
    }

    /**
     *  Create a local directory to contain the cloned GitHub repository.
     *  @return File The file for the local GitHub repo., or null if the exception is thrown from this method
     *  @throws IOException if the local Git directory cannot be created
     */
    public File createWorkingDir() throws IOException
    {
        File dir = new File(SoepConstants.GIT_HUB_PATH);

        if (dir.exists()) {
            LOGGER.info(dir + SoepLoggingConstants.DIR_EXISTS);
            return dir;
        } else if (dir.mkdirs()) {
            LOGGER.info(dir + SoepLoggingConstants.DIR_CREATED);
            return dir;
        } else {
            LOGGER.info(dir + SoepLoggingConstants.DIR_NOT_CREATED);
            return null;
        }
    }

    /**
     * Load dataset file descriptions from a CSV spreadsheet to a Map
     */
    public void loadDatasetMetadata()
    {
        try
            (Reader reader = Files.newBufferedReader(Paths.get(SoepConstants.FILE_TITLE_DATASET))) {
            CsvToBean<DatasetMetadata> csvMapper = new CsvToBeanBuilder<DatasetMetadata>(reader)
            .withType(DatasetMetadata.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

            // Read records one by one in a Map<String, DatasetMetadata>
            DatasetMetadata ds;
            Iterator<DatasetMetadata> csvIterator = csvMapper.iterator();

            while (csvIterator.hasNext()) {
                ds = csvIterator.next();
                fileDescriptions.put(ds.getDatasetName(), ds);
            }
        } catch (IOException e) {
            LOGGER.error(SoepLoggingConstants.ERROR_READING_FILE, e);
        }
    }

    /** Does a repo already exist?
     * @param repoName Path to the local SOEP repo. (SOEP-core, in this case)
     * @return boolean Returns whether the repository exists
     * @throws IOException if a local Git directory cannot be created
     */
    public boolean repoExists(String repoName) throws IOException
    {
        File file = new File(SoepConstants.GIT_HUB_PATH + repoName);

        return file.exists();
    }

    /** List all files from a given dataset
     * @param folderPath Directory path of the dataset (GitHub repo)
     */
    public List<File> listFiles(String folderPath)
    {
        File[] files = new File(folderPath).listFiles();

        if (files != null)
            return Arrays.asList(files);

        return Collections.emptyList();
    }

    /**
     * @return Map<String, String> file descriptions
     */
    public Map<String, DatasetMetadata> getFileDescriptions()
    {
        return this.fileDescriptions;
    }
}