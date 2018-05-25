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
package de.gerdiproject.harvest.harvester;

import de.gerdiproject.harvest.IDocument;

import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;
import de.gerdiproject.harvest.soep.constants.SoepDataCiteConstants;
import de.gerdiproject.harvest.soep.utils.JGitUtil;
import de.gerdiproject.harvest.soep.utils.SoepIO;

import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Identifier;
import de.gerdiproject.json.datacite.Title;
import de.gerdiproject.json.datacite.Rights;
import de.gerdiproject.json.datacite.Date;
import de.gerdiproject.json.datacite.Description;
import de.gerdiproject.json.datacite.abstr.AbstractDate;
import de.gerdiproject.json.datacite.extension.ResearchData;
import de.gerdiproject.json.datacite.extension.WebLink;

import de.gerdiproject.json.datacite.enums.IdentifierType;
import de.gerdiproject.json.datacite.enums.DateType;
import de.gerdiproject.json.datacite.enums.DescriptionType;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *  The main de.gerdiproject.harvest.harvester for SOEP
 *  This harvester harvests SOEP datasets, stored in their GitHub repository.
 *  @author Fidan Limani
*/
public class SoepHarvester extends AbstractListHarvester<File>
{
    private final SoepIO soepIO;
    private final JGitUtil soepGitHub;

    /**
     * Constructor As suggested, the constructor should be in a "default" style
     */
    public SoepHarvester() throws IOException {
        super("SOEP Harvester", 1);
        soepIO = new SoepIO();
        soepGitHub = new JGitUtil(SoepConstants.SOEP_REMOTE_REPO_NAME, SoepConstants.SOEP_REMOTE_REPO_URL);
    }

    @Override
    protected Collection<File> loadEntries()
    {
        // Repo-related operations based on JGit library.
        try {
            soepGitHub.collect(soepGitHub);
            soepIO.loadDatasetMetadata();
        } catch (IOException e) {
            logger.error(SoepLoggingConstants.IO_EXCEPTION_ERROR, e);
        } catch (GitAPIException e) {
            logger.error(SoepLoggingConstants.GIT_API_EXCEPTION_ERROR, e);
        }

        String datasetPath = String.format(SoepConstants.BASE_PATH, "/", "");

        return soepIO.listFiles(datasetPath);
    }

    /**
     * This method is to be invoked after loadEntries()
    */
    @Override
    protected List<IDocument> harvestEntry(File soepFile)
    {
        // Specify source ID for harvested file
        String sourceTitle = soepIO.getFileDescriptions().get(soepFile).getLabel();

        // Create the document to contain SOEP metadata for every given file from its dataset
        DataCiteJson document = new DataCiteJson(sourceTitle);

        // "Static" SOEP metadata
        document.setFormats(SoepDataCiteConstants.FORMATS);

        /*
         * GeRDI DataCite Mandatory properties
         * (ID 1) Identifier: This is the DOI identifier for v33 of the dataset
         */
        Identifier soepID = new Identifier(SoepDataCiteConstants.IDENTIFIER, IdentifierType.DOI);
        document.setIdentifier(soepID);

        // (ID 2) Creators
        document.setCreators(SoepDataCiteConstants.CREATORS);

        /**
         * (ID 3 Title) Individual file descriptions
         */
        Title title = new Title(sourceTitle);
        document.setTitles(Arrays.asList(title));

        // (ID 4) Publisher
        document.setPublisher(SoepDataCiteConstants.PROVIDER);

        // (ID 5) PublicationYear: 2017
        List<AbstractDate> dates = new LinkedList<>();
        dates.add(SoepDataCiteConstants.PUBLICATION_YEAR);
        document.setDates(dates);

        // (ID 7) Contributor
        document.setContributors(Arrays.asList(SoepDataCiteConstants.COLLECTOR_CONTRIBUTOR));

        /** (ID 8) Date: dateType="Collected" with individual data collection dates. PublicationYear is too "matchy" ;)
         *  If year=0 or "long", set the "1984-2016" range.
         */
        String tempPeriod = soepIO.getFileDescriptions().get(soepFile).getPeriodName();
        AbstractDate dateCollected;
        dateCollected = tempPeriod.equals("0") || tempPeriod.equals("long") ?
                        SoepDataCiteConstants.PUBLICATION_RANGE : new Date(tempPeriod, DateType.Collected);
        document.setDates(Arrays.asList(dateCollected));

        // (ID 10) ResourceType
        document.setResourceType(SoepDataCiteConstants.RESOURCE_TYPE);

        // (ID 15) Dataset version
        document.setVersion(SoepDataCiteConstants.VERSION);

        // (ID 16) Rights
        Rights soepRights = new Rights(SoepDataCiteConstants.RIGHTS_VALUE);
        document.setRightsList(Arrays.asList(soepRights));

        // (ID 17) Description, type "Abstract"
        Description soepDescription = new Description(SoepDataCiteConstants.DESCRIPTION_VALUE, DescriptionType.Abstract, SoepDataCiteConstants.DESCRIPTION_LANGUAGE);
        document.setDescriptions(Arrays.asList(soepDescription));

        // GeRDI Extension
        List<WebLink> links = new LinkedList<>();

        // View SOEP dataset file on GitHub
        String soepFileName = soepFile.getName();
        WebLink pageLink = new WebLink(String.format(SoepConstants.ACCESS_FILE_URL, SoepConstants.TREE, soepFileName));
        pageLink.setName(SoepConstants.VIEW_TREE);
        pageLink.setType(WebLinkType.ViewURL);
        links.add(pageLink);

        // View SOEP dataset file source ("raw" representation) on GitHub
        WebLink sourceLink = new WebLink(String.format(SoepConstants.ACCESS_FILE_URL, SoepConstants.BLOB, soepFileName));
        pageLink.setName(SoepConstants.VIEW_RAW);
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
        ResearchData researchData = new ResearchData(String.format(SoepConstants.BASE_PATH, "/", soepFileName),
                                                                    "JSON");
        researchData.setUrl(pageLink.getUrl());
        researchData.setType("JSON");
        files.add(researchData);
        document.setResearchDataList(files);

        // E4: ResearchDiscipline
        document.setResearchDisciplines(SoepDataCiteConstants.DISCIPLINES);

        return Arrays.asList(document);
    }
}