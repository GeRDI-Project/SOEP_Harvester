/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
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

package de.gerdiproject.harvest.harvester;

import de.gerdiproject.harvest.IDocument;

import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Title;
import de.gerdiproject.json.datacite.abstr.AbstractDate;
import de.gerdiproject.json.datacite.extension.ResearchData;
import de.gerdiproject.json.datacite.extension.WebLink;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;

import org.eclipse.jgit.api.errors.GitAPIException;

import de.gerdiproject.harvest.soep.constants.SoepDataCiteConstants;
import de.gerdiproject.harvest.soep.utils.JGitUtil;
import de.gerdiproject.harvest.soep.utils.SoepIO;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *  The main de.gerdiproject.harvest.harvester for SOEP
 *  This harvester harvests SOEP datasets, stored in their GitHub repository.
 *  @author Fidan Limani
*/
public class SoepHarvester extends AbstractListHarvester<File>
{
    private final SoepIO soepIO;

    /**
     * Constructor As suggested, the constructor should be in a "default" style
     */
    public SoepHarvester()
    {
        super("SOEP Harvester", 1);
        soepIO = new SoepIO();
    }

    @Override
    protected Collection<File> loadEntries()
    {
        // Repo-related operations based on JGit library.
        try {
            JGitUtil.collect();
        } catch (IOException e) {
            logger.error("Exception: ", e);
        } catch (GitAPIException e) {
            logger.error("Exception: ", e);
        }

        String datasetPath = String.format(SoepConstants.BASE_PATH, File.separator, File.separator, File.separator,
                                            File.separator, File.separator, File.separator, "");

        return soepIO.listFiles(datasetPath);
    }

    @Override
    /**
     * This method is to be invoked after loadEntries()
    */
    protected List<IDocument> harvestEntry(File soepFile)
    {
        // Create the document to contain SOEP metadata for every given file from its dataset
        DataCiteJson document = new DataCiteJson();

        // "Static" SOEP metadata
        document.setFormats(SoepDataCiteConstants.FORMATS);

        /**
         * GeRDI DataCite Mandatory properties
         * (ID 1) Identifier: example, <identifier identifierType="DOI">10.5072/example-full</identifier> ...
         */

        // (ID 2) Creator
        document.setCreators(SoepDataCiteConstants.CREATORS);

        // (ID 3 Title) The file name, for the time being...
        String soepFileName = soepFile.getName();
        Title title = new Title(soepFileName);
        document.setTitles(Arrays.asList(title));

        // (ID 4) Publisher
        document.setPublisher(SoepDataCiteConstants.PROVIDER);

        // (ID 5) PublicationYear: Under development! Set 1984 as (the first ever) publication year, and refine later on.
        List<AbstractDate> dates = new LinkedList<>();
        dates.add(SoepConstants.PUBLICATION_YEAR);
        document.setDates(dates);

        // (ID 10) ResourceType
        document.setResourceType(SoepDataCiteConstants.RESOURCE_TYPE);

        // GeRDI Extension
        List<WebLink> links = new LinkedList<>();

        // View SOEP dataset file on GitHub
        WebLink pageLink = new WebLink(String.format(SoepConstants.BASE_URL, "tree", soepFileName));
        pageLink.setName("View file: " + soepFileName);
        pageLink.setType(WebLinkType.ViewURL);
        links.add(pageLink);

        // View SOEP dataset file source ("raw" representation) on GitHub
        WebLink sourceLink = new WebLink(String.format(SoepConstants.RAW_FILE_URI, "blob", soepFileName));
        pageLink.setName("View raw file contents");
        pageLink.setType(WebLinkType.SourceURL);
        links.add(sourceLink);

        // The logo link
        WebLink logoLink = SoepDataCiteConstants.LOGO_WEB_LINK;
        links.add(logoLink);

        // Add all the links to the document;
        document.setWebLinks(links);

        // E2: RepositoryIdentifier
        document.setRepositoryIdentifier(SoepDataCiteConstants.REPOSITORY_ID);

        // E3. ResearchData{dataIdentifier, dataURL, dataLabel, dataType}
        List<ResearchData> files = new LinkedList<>();
        ResearchData researchData = new ResearchData(String.format(SoepConstants.BASE_PATH, File.separator, File.separator,
                                                                    File.separator, File.separator, File.separator, File.separator,
                                                                    soepFileName), "JSON");
        researchData.setUrl(pageLink.getUrl());
        researchData.setType("JSON");
        files.add(researchData);
        document.setResearchDataList(files);

        // E4: ResearchDiscipline
        document.setResearchDisciplines(SoepDataCiteConstants.DISCIPLINES);

        return Arrays.asList(document);
    }
}