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
import java.nio.charset.StandardCharsets;

import de.gerdiproject.harvest.SoepContextListener;
import de.gerdiproject.harvest.application.ContextListener;
import de.gerdiproject.harvest.etls.AbstractIteratorETL;
import de.gerdiproject.harvest.etls.StaticIteratorETL;
import de.gerdiproject.harvest.etls.transformers.SoepTransformer;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.utils.data.DiskIO;
import de.gerdiproject.json.GsonUtils;
import de.gerdiproject.json.datacite.DataCiteJson;

/**
 * This class provides Unit Tests for the {@linkplain SoepExtractor}.
 *
 * @author Robin Weiss
 */
public class SoepExtractorTest extends AbstractIteratorExtractorTest<SoepFileVO>
{
    final DiskIO diskReader = new DiskIO(GsonUtils.createGerdiDocumentGsonBuilder().create(), StandardCharsets.UTF_8);


    @Override
    protected ContextListener getContextListener()
    {
        return new SoepContextListener();
    }


    @Override
    protected AbstractIteratorETL<SoepFileVO, DataCiteJson> getEtl()
    {
        return new StaticIteratorETL<>(SoepConstants.SOEP_ETL_NAME, new SoepExtractor(), new SoepTransformer());
    }


    @Override
    protected File getConfigFile()
    {
        return getResource("config.json");
    }


    @Override
    protected File getMockedHttpResponseFolder()
    {
        return getResource("mockedHttpResponses");
    }


    @Override
    protected SoepFileVO getExpectedOutput()
    {
        final File resource = getResource("output.json");
        return diskReader.getObject(resource, SoepFileVO.class);
    }
}
