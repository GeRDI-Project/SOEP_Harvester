/*
 *  Copyright © 2019 Robin Weiss (http://www.gerdi-project.de/)
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
package de.gerdiproject.harvest.etls;

import de.gerdiproject.harvest.etls.extractors.SoepExtractor;
import de.gerdiproject.harvest.etls.extractors.SoepFileVO;
import de.gerdiproject.harvest.etls.transformers.SoepTransformer;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.json.datacite.DataCiteJson;

/**
 * This ETL is able to harvest the SOEP GitHub repository.
 *
 * @author Robin Weiss
 */
public class SoepETL extends StaticIteratorETL<SoepFileVO, DataCiteJson>
{
    /**
     * Simple Constructor.
     */
    public SoepETL()
    {
        super(SoepConstants.SOEP_ETL_NAME, new SoepExtractor(), new SoepTransformer());
    }
}
