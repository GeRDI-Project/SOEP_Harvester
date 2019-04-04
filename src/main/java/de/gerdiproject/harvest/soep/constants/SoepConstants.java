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
package de.gerdiproject.harvest.soep.constants;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import de.gerdiproject.harvest.github.json.GitHubCommit;
import de.gerdiproject.harvest.github.json.GitHubContent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This static class contains constants that are specific to SOEP's GitHub repository.
 * @author Fidan Limani
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    // The concept label language
    public static final String CONCEPT_LABEL_EN = "en";
    public static final String CONCEPT_LABEL_DE = "de";

    // Type constant
    public static final Type CONTENT_LIST_TYPE = new TypeToken<List<GitHubContent>>() {} .getType();
    public static final Type COMMIT_LIST_TYPE = new TypeToken<List<GitHubCommit>>() {} .getType();
    public static final String LOADING_FILE_INFO = "Loading SOEP %s...";
}