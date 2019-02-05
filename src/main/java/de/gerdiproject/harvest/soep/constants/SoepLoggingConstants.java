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
package de.gerdiproject.harvest.soep.constants;

/**
 * Constants used for logging for SOEP harvester classes
 *
 * @author Fidan Limani
 */
public class SoepLoggingConstants
{
    public static final String DIR_EXISTS = " already exists";
    public static final String DIR_CREATED = " was created";
    public static final String DIR_NOT_CREATED = " was not created";

    public static final String SET_REPO_NAME = "Setting the repository name to <%s>";
    public static final String SET_REMOTE_REPO_URL = "Setting the remote repository URI to <%s>";

    public static final String REPO_EXISTS = "Repo <%s> exists.";
    public static final String INIT_REPO = "Initializing repository <%s>";
    public static final String CLONE_REPO = "Cloning remote repository from <%s>";

    public static final String REPO_BRANCH_UPDATE = "Repository updates from <%s> available?";
    public static final String UPDATES_AVAILABLE = "Updates available. Pulling changes: %n%s";
    public static final String UPDATE_COMPLETE = "Repository successfully updated.";
    public static final String LOCAL_REPO_UPDATED = "Local repository up to date.";
    public static final String UPDATE_LOCAL_REPO = "Updating local repository <%s>";

    public static final String REPO_MISSING_ERROR = "Repository does not exist. To be created next.";
    public static final String IO_EXCEPTION_ERROR = "Exception while initializing/accessing the local repository";
    public static final String GIT_API_EXCEPTION_ERROR = "Exception while setting up/cloning the repository.";
    public static final String ERROR_READING_CSV_FILE = "Exception while reading SOEP CSV files.";
    public static final String ERROR_READING_DATASET_FILES = "Exception while reading SOEP <dataset> files.";
    public static final String ERROR_READING_CONCEPTS_FILES = "Exception while reading SOEP <concepts> files.";
    public static final String ERROR_READING_VARIABLES_FILES = "Exception while reading SOEP <variables> files.";
}