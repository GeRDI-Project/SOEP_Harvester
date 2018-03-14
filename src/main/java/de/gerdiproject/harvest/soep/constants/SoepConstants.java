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

package de.gerdiproject.harvest.soep.constants;

import de.gerdiproject.harvest.soep.utils.SoepIO;
import de.gerdiproject.json.datacite.Date;
import de.gerdiproject.json.datacite.enums.DateType;

/**
 * This static class contains constants that are specific to SOEP's GitHub repository.
 * @author Fidan Limani
 */
public class SoepConstants
{
    /**
     * GitHub-related constants
     * */

    public static final String TREE = "trees";
    public static final String BLOB = "blob";
    public static final String VIEW_TREE = "View tree file";
    public static final String VIEW_RAW = "View raw file";

    // GitHub "tree" and "blob" access URL
    public static final String ACCESS_FILE_URL =
                        "https://github.com/paneldata/de.gerdiproject.harvest.soep-core/%s/master/ddionrails/datasets/%s";

    // Local repo. dataset path
    public static final String BASE_PATH = SoepIO.USER_HOME
                                            + "%1$sGitHub%1$sSOEP-core%1$slocal%1$sddionrails%1$sdatasets%1$s%2$s";

    // Publication year: needs to be "refined"; here used only used provisory at this point to complete metadata schema
    public static final Date PUBLICATION_YEAR = new Date("1984", DateType.Other);
}
