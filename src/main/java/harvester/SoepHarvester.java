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

package harvester;

import de.gerdiproject.harvest.IDocument;
import de.gerdiproject.harvest.harvester.AbstractListHarvester;

import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Title;
import de.gerdiproject.json.datacite.Date;
import de.gerdiproject.json.datacite.abstr.AbstractDate;
import de.gerdiproject.json.datacite.enums.DateType;
import de.gerdiproject.json.datacite.extension.ResearchData;
import de.gerdiproject.json.datacite.extension.WebLink;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;
import org.eclipse.jgit.api.errors.GitAPIException;
import soep.constants.SoepDataCiteConstants;
import soep.constants.SoepParameterConstants;
import soep.utils.JGitUtil;
import soep.utils.SoepIO;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
    The main harvester
* */
public class SoepHarvester extends AbstractListHarvester<File> {
    private static final String BASE_URL = "https://github.com/paneldata/soep-core/%s/master/ddionrails/datasets/%s"; // tree
    private static final String RAW_FILE_URI = "https://github.com/paneldata/soep-core/%s/master/ddionrails/datasets/%s"; // blob
    private static final String BASE_PATH = System.getProperty("user.home") +
                                                "%sGitHub%sSOEP-core%slocal%sddionrails%sdatasets%s%s"; // Local repo. dataset

    private String harvesterName; // Required to ID GeRDI harvester instances
    private ArrayList<File> soepFiles;
    private SoepIO soepIO;

    // As suggested, a default constructor
    public SoepHarvester(){
        super(1);
        this.harvesterName = "SOEP Harvester";
        soepIO = new SoepIO();
        soepFiles = new ArrayList();
    }

    @Override
    protected Collection<File> loadEntries() {
        // Repo-related operations based on JGit library.
        try {
            JGitUtil.collect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        String datasetPath = String.format(BASE_PATH, File.separator, File.separator, File.separator,
                File.separator, File.separator, File.separator, "");
        soepFiles = soepIO.listFiles(datasetPath);

        return soepFiles;
    }

    @Override
    /*  This method is to be invoked after loadEntries()
    * */
    protected List<IDocument> harvestEntry(File soepFile) {
        // Create the document to contain SOEP metadata for every given file from its dataset
        DataCiteJson document = new DataCiteJson();

        // "Static" SOEP metadata
        document.setVersion(SoepParameterConstants.VERSION_KEY); // What would the version be for SOEP considering it is a longitudinal study?
        document.setLanguage(SoepParameterConstants.LANGUAGE_DEFAULT); // There are also some MD elements in German...
        document.setFormats(SoepDataCiteConstants.FORMATS);

        /* GeRDI DataCite Mandatory properties
        */
        // #1 Identifier: How to handle this element?

        // #2 Creator
        document.setCreators(SoepDataCiteConstants.CREATORS);

        // #3 Title: The file name, for the time being...
        Title title = new Title(soepFile.getName());
        document.setTitles(Arrays.asList(title));

        // #4 Publisher
        document.setPublisher(SoepDataCiteConstants.PROVIDER);

        // #5 PublicationYear: Under development! Set 1984 as (the first ever) publication year, and refine later on.
        List<AbstractDate> dates = new LinkedList<>();
        Date publicationYear = new Date("1984", DateType.Other);
        dates.add(publicationYear);
        document.setDates(dates);

        // #10 ResourceType
        document.setResourceType(SoepDataCiteConstants.RESOURCE_TYPE);

        /* GeRDI Extension */
        List<WebLink> links = new LinkedList<>();

        // View SOEP dataset file on GitHub
        WebLink pageLink = new WebLink(String.format(BASE_URL, "tree", soepFile.getName()));
        pageLink.setName("View file: " + soepFile.getName());
        pageLink.setType(WebLinkType.ViewURL);
        links.add(pageLink);

        // View SOEP dataset file source ("raw" representation) on GitHub
        WebLink sourceLink = new WebLink(String.format(RAW_FILE_URI, "blob", soepFile.getName()));
        pageLink.setName("View raw file contents");
        pageLink.setType(WebLinkType.SourceURL);
        links.add(sourceLink);

        // The logo link
        WebLink logoLink = SoepDataCiteConstants.LOGO_WEB_LINK;
        logoLink.setName("Logo");
        logoLink.setType(WebLinkType.ProviderLogoURL);
        links.add(logoLink);

        document.setWebLinks(links); // Add all the links to the document;

        /* E2: RepositoryIdentifier */
        document.setRepositoryIdentifier(SoepDataCiteConstants.REPOSITORY_ID);

        /* E3. ResearchData{dataIdentifier, dataURL, dataLabel, dataType} */
        List<ResearchData> files = new LinkedList<>();
        ResearchData researchData = new ResearchData(String.format(BASE_PATH, File.separator, File.separator,
                                        File.separator, File.separator, File.separator, File.separator,
                                        soepFile.getName()), "JSON");
        researchData.setUrl(pageLink.getUrl());
        researchData.setType("JSON");
        files.add(researchData);
        document.setResearchDataList(files);

        /* E4: ResearchDiscipline */
        document.setResearchDisciplines(SoepDataCiteConstants.DISCIPLINES);

        return Arrays.asList(document);
    }

    @Override
    /*
        From the utils-like class, provide access to a collection of SOEP dataset files;
        The overridden implementation should add documents to the search index by calling the addDocument() or
        addDocuments() methods;
    * */
    protected boolean harvestInternal(int i, int i1) throws Exception {
        boolean status = false;

        return false;
    }

    @Override
    protected int initMaxNumberOfDocuments() {
        return 0;
    }

    @Override
    protected String initHash() throws NoSuchAlgorithmException, NullPointerException {
        return null;
    }

    @Override
    public void abortHarvest() { // its access was "protected" by default, but there was a compiler complaint...

    }

    // Demo the app
    public static void main(String[] args){
        // Test harvester reference and path to SOEP exampl file
        File soepFile = new File("C:/Users/limani fidan/GitHub/SOEP Harvester/abroad.json");
        SoepHarvester test = new SoepHarvester();

        // 1. Invoke loadEntries()
        Collection<File> list = new ArrayList<>();
        list = test.loadEntries();
        System.out.printf("# of files harvested: " + list.size());

        // 2. Invoke harvestEntry()
    }
}