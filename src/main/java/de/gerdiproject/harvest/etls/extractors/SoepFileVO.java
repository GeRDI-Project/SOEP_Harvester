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

import de.gerdiproject.harvest.soep.dataset_mapping.DatasetMetadata;

/**
 * This value object holds a SOEP file and corresponding metadata.
 *
 * @author Robin Weiss
 */
public class SoepFileVO
{
    private File file;
    private DatasetMetadata metadata;


    public SoepFileVO(File file, DatasetMetadata metadata)
    {
        super();
        this.file = file;
        this.metadata = metadata;
    }


    public File getFile()
    {
        return file;
    }


    public void setFile(File file)
    {
        this.file = file;
    }


    public DatasetMetadata getMetadata()
    {
        return metadata;
    }


    public void setMetadata(DatasetMetadata metadata)
    {
        this.metadata = metadata;
    }
}
