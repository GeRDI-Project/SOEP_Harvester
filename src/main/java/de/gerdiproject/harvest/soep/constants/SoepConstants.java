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
 * This static class contains constants that are specific to SOEP's GitHub repository.
 * @author Fidan Limani
 */
public class SoepConstants
{
    /**
     * GitHub-related constants
     * */
    public static final String TREE = "tree";
    public static final String VIEW_TREE = "View tree file";
    public static final String VIEW_RAW = "View raw file";

    public static final String SOEP_REMOTE_REPO_URL = "https://github.com/paneldata/soep-core";

    private static final String API_BASE_URL = "https://api.github.com/repos/paneldata/soep-core/";
    private static final String DATASETS_PATH = "ddionrails/datasets/";

    // GitHub "tree" and "blob" access URL
    public static final String ACCESS_FILE_URL =
        SOEP_REMOTE_REPO_URL + "/%s/master/" + DATASETS_PATH + "%s";

    // Local repository dataset path
    public static final String BASE_PATH =
        "GitHub/SOEP-core/local/" + DATASETS_PATH  + "%s";

    public static final String DATASET_COMMITS_URL =
        API_BASE_URL
        + "commits?sha=master&path="
        + DATASETS_PATH;

    public static final String DATASETS_CONTENT_URL =
        API_BASE_URL
        + "contents/"
        + DATASETS_PATH;

    public static final String DATASETS_CSV_DOWNLOAD_URL = "https://raw.githubusercontent.com/paneldata/soep-core/master/ddionrails/datasets.csv";
    public static final String VARIABLES_CSV_DOWNLOAD_URL = "https://raw.githubusercontent.com/paneldata/soep-core/master/ddionrails/variables.csv";
    public static final String CONCEPTS_CSV_DOWNLOAD_URL = "https://raw.githubusercontent.com/paneldata/soep-core/master/ddionrails/concepts.csv";

    public static final String SOEP_ETL_NAME = "SoepETL";
}