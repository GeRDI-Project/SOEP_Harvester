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

    private String harvesterName; // Required to ID GeRDI harvester instances
    private String datasetPath; // The dataset location in a GitHub repo
    private ArrayList<File> soepFiles;
    private SoepIO soepIO;

    // As suggested, the constructor should be in a "default" style
    public SoepHarvester(){
        super(1);
        this.harvesterName = "SOEP Harvester";
        soepIO = new SoepIO();
        this.datasetPath = "SOEP-core/local/ddionrails/datasets/";
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

        String datasetPath = soepIO.getGitHubPath() + this.datasetPath;
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
        document.setPublisher(SoepDataCiteConstants.PROVIDER);
        document.setFormats(SoepDataCiteConstants.FORMATS);
        document.setResourceType(SoepDataCiteConstants.RESOURCE_TYPE);
        document.setResearchDisciplines(SoepDataCiteConstants.DISCIPLINES);
        document.setRepositoryIdentifier(SoepDataCiteConstants.REPOSITORY_ID);


        /* "Dynamic" SOEP metadata */

        // language {german, english}
        // version

        // GeRDI Extension
        /* 1. WebLink: {linkName, linkURI, linkType}
            - WebLink: (name, URI) pair value
            - linkName: name of the link, most likely file name or sth similar;
            - linkURI: access URI of the resource, i.e., GitHub path + dataset file;
            - linkType: "SourceURL" for all SOEP cases
        * */
        List<WebLink> links = new ArrayList();

        // View SOEP dataset file on GitHub
        WebLink pageLink = new WebLink(String.format(BASE_URL, "tree", soepFile.getName()));
        pageLink.setName("View file: " + soepFile.getName());
        pageLink.setType(WebLinkType.ViewURL);
        links.add(pageLink);

        // View SOEP dataset file source ("raw" representation) on GitHub
        WebLink sourceLink = new WebLink(String.format(BASE_URL, "blob", soepFile.getName()));
        pageLink.setName("View raw file contents");
        pageLink.setType(WebLinkType.SourceURL);
        links.add(sourceLink);

        // The logo link
        WebLink logoLink = SoepDataCiteConstants.LOGO_WEB_LINK;
        logoLink.setName("Logo");
        logoLink.setType(WebLinkType.ProviderLogoURL);
        links.add(logoLink);

        /* 3. ResearchData{dataIdentifier, dataURL, dataLabel, dataType}
            - ResearchData (M): could be equal to <linkURI> property, or the raw version of the file from GitHub;
            - dataIdentifier (M): same as above?
         */

        // 4. ResearchDiscipline

        document.setWebLinks(links); // Add all the links to the document;
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

        // 2. Invoke harvestEntry()
        test.harvestEntry(soepFile);
    }
}