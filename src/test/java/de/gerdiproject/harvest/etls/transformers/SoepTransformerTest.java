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
package de.gerdiproject.harvest.etls.transformers;

import java.io.File;
import java.nio.charset.StandardCharsets;

import de.gerdiproject.harvest.SoepContextListener;
import de.gerdiproject.harvest.application.ContextListener;
import de.gerdiproject.harvest.application.MainContextUtils;
import de.gerdiproject.harvest.etls.AbstractIteratorETL;
import de.gerdiproject.harvest.etls.SoepETL;
import de.gerdiproject.harvest.etls.extractors.SoepFileVO;
import de.gerdiproject.harvest.utils.data.DiskIO;
import de.gerdiproject.harvest.utils.data.constants.DataOperationConstants;
import de.gerdiproject.harvest.utils.file.FileUtils;
import de.gerdiproject.json.GsonUtils;
import de.gerdiproject.json.datacite.DataCiteJson;

/**
 * This class provides Unit Tests for the {@linkplain SoepTransformer}.
 *
 * @author Robin Weiss
 */
public class SoepTransformerTest extends AbstractIteratorTransformerTest<SoepFileVO, DataCiteJson>
{
    final DiskIO diskReader = new DiskIO(GsonUtils.createGerdiDocumentGsonBuilder().create(), StandardCharsets.UTF_8);


    @Override
    protected ContextListener getContextListener()
    {
        return new SoepContextListener();
    }


    @Override
    protected AbstractIteratorTransformer<SoepFileVO, DataCiteJson> setUpTestObjects()
    {
        // copy mocked HTTP responses to the cache folder to drastically speed up the testing
        final File httpResourceFolder = getResource("mockedHttpResponses");
        final File httpCacheFolder = new File(
            MainContextUtils.getCacheDirectory(getClass()),
            DataOperationConstants.CACHE_FOLDER_PATH);

        FileUtils.copyFile(httpResourceFolder, httpCacheFolder);

        return super.setUpTestObjects();
    }


    @Override
    protected AbstractIteratorETL<SoepFileVO, DataCiteJson> getEtl()
    {
        return new SoepETL();
    }


    @Override
    protected SoepFileVO getMockedInput()
    {
        final File resource = getResource("input.json");
        return diskReader.getObject(resource, SoepFileVO.class);
    }


    @Override
    protected DataCiteJson getExpectedOutput()
    {
        final File resource = getResource("output.json");
        return diskReader.getObject(resource, DataCiteJson.class);
    }
}
