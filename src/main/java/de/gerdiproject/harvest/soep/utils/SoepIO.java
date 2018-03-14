/**
 * Copyright © 2017 Fidan Limani (http://www.gerdi-project.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.*;

import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A util-like class to support repo- and GeRDI harvester-based operations.
 *
 * @author Fidan Limani
 */
public class SoepIO
{
    // Required in SoepHarvester class
    private String gitHubPath;

    // User home path based on which the local repository will be created
    public static final String USER_HOME = System.getProperty("user.home");

    private static final Logger LOGGER = LoggerFactory.getLogger(SoepIO.class);

    /**
     * Constructor
     */
    public SoepIO()
    {
        this.gitHubPath = USER_HOME + File.separator + "GitHub" + File.separator;
    }

    /**
     *  Create a local directory to contain the cloned GitHub repository.
     *  @return File The file for the local GitHub repo., or null if the exception is thrown from this method
     *  @throws IOException if the local Git directory cannot be created
     */
    public File createWorkingDir() throws IOException
    {
        File dir = new File(getGitHubPath());

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

    /** Does a repo already exist?
     * @param repoName Path to the local SOEP repo. (SOEP-core, in this case)
     * @return boolean Returns whether the repository exists
     * @throws IOException if a local Git directory cannot be created
     */
    public boolean repoExists(String repoName) throws IOException
    {
        File file = new File(this.getGitHubPath() + repoName);

        return file.exists();
    }

    /** List all files from a given dataset
     * @param folderPath Directory path of the dataset (GitHub repo)
     */
    public List<File> listFiles(String folderPath)
    {
        File[] files = new File(folderPath).listFiles();
        // List<File> fileList = new ArrayList<>();

        if(files != null){
            return Arrays.asList(files);
        }

        return Collections.emptyList();
    }

    /**
     * @return String GitHub path
     */
    public String getGitHubPath()
    {
        return this.gitHubPath;
    }
}