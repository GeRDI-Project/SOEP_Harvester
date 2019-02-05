/*
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
package de.gerdiproject.harvest.etls.transformers;

import java.util.*;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.extractors.SoepFileVO;
import de.gerdiproject.harvest.soep.constants.SoepConstants;
import de.gerdiproject.harvest.soep.constants.SoepDataCiteConstants;
import de.gerdiproject.harvest.soep.csv.ConceptMetadata;
import de.gerdiproject.harvest.soep.csv.DatasetMetadata;
import de.gerdiproject.harvest.soep.csv.VariableMetadata;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Date;
import de.gerdiproject.json.datacite.Description;
import de.gerdiproject.json.datacite.Identifier;
import de.gerdiproject.json.datacite.Rights;
import de.gerdiproject.json.datacite.Title;
import de.gerdiproject.json.datacite.abstr.AbstractDate;
import de.gerdiproject.json.datacite.enums.DateType;
import de.gerdiproject.json.datacite.enums.DescriptionType;
import de.gerdiproject.json.datacite.enums.IdentifierType;
import de.gerdiproject.json.datacite.extension.generic.ResearchData;
import de.gerdiproject.json.datacite.extension.generic.WebLink;
import de.gerdiproject.json.datacite.extension.generic.enums.WebLinkType;
import de.gerdiproject.json.datacite.extension.soep.SoepConcept;
import de.gerdiproject.json.datacite.extension.soep.SoepDataCiteExtension;
import de.gerdiproject.json.datacite.extension.soep.SoepVariable;

/**
 * This transformer transforms Soep {@linkplain SoepFileVO}s to {@linkplain DataCiteJson} objects.
 *
 * @author Fidan Limani, Robin Weiss
 */
public class SoepTransformer extends AbstractIteratorTransformer<SoepFileVO, DataCiteJson>
{
    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);
    }

    @Override
    protected DataCiteJson transformElement(SoepFileVO vo) throws TransformerException
    {
        // Specify source ID for harvested file
        final DatasetMetadata metadata = vo.getDatasetMetadata();

        // Abort if there is no metadata
        if (metadata == null)
            return null;

        String sourceTitle = metadata.getLabel();

        // Create the document to contain SOEP metadata for every given file from its dataset
        final DataCiteJson document = new DataCiteJson(vo.getContent().getPath());

        // "Static" SOEP metadata
        document.addFormats(SoepDataCiteConstants.FORMATS);

        /*
         * GeRDI DataCite Mandatory properties
         * (ID 1) Identifier: This is the DOI identifier for v33 of the dataset
         */
        Identifier soepID = new Identifier(SoepDataCiteConstants.IDENTIFIER, IdentifierType.DOI);
        document.setIdentifier(soepID);

        // (ID 2) Creators
        document.addCreators(SoepDataCiteConstants.CREATORS);

        /*
         * (ID 3 Title) Individual file descriptions
         */
        Title title = new Title(sourceTitle);
        document.addTitles(Arrays.asList(title));

        // (ID 4) Publisher
        document.setPublisher(SoepDataCiteConstants.PROVIDER);

        // (ID 5) PublicationYear: 2017
        List<AbstractDate> dates = new LinkedList<>();
        dates.add(SoepDataCiteConstants.PUBLICATION_YEAR);
        document.addDates(dates);

        // (ID 7) Contributor
        document.addContributors(Arrays.asList(SoepDataCiteConstants.COLLECTOR_CONTRIBUTOR));

        /* (ID 8) Date: dateType="Collected" with individual data collection dates. PublicationYear is too "matchy" ;)
         *  If year=0 or "long", set the "1984-2016" range.
         */
        String tempPeriod = metadata.getPeriodName();
        AbstractDate dateCollected;
        dateCollected = tempPeriod.equals("0") || tempPeriod.equals("long") ?
                        SoepDataCiteConstants.PUBLICATION_RANGE : new Date(tempPeriod, DateType.Collected);
        document.addDates(Arrays.asList(dateCollected));

        // (ID 10) ResourceType
        document.setResourceType(SoepDataCiteConstants.RESOURCE_TYPE);

        // (ID 15) Dataset version
        document.setVersion(SoepDataCiteConstants.VERSION);

        // (ID 16) Rights
        Rights soepRights = new Rights(SoepDataCiteConstants.RIGHTS_VALUE);
        document.addRights(Arrays.asList(soepRights));

        // (ID 17) Description, type "Abstract"
        final List<Description> descriptions = new LinkedList<>();
        descriptions.add(
            new Description(
                SoepDataCiteConstants.DESCRIPTION_VALUE,
                DescriptionType.Abstract,
                SoepDataCiteConstants.DESCRIPTION_LANGUAGE));

        // add optional description from metadata
        if (metadata.getDescription() != null && !metadata.getDescription().isEmpty())
            descriptions.add(
                new Description(
                    metadata.getDescription(),
                    DescriptionType.Other,
                    SoepDataCiteConstants.DESCRIPTION_LANGUAGE));

        document.addDescriptions(descriptions);

        // GeRDI Extension
        List<WebLink> links = new LinkedList<>();

        // View SOEP dataset file on GitHub
        String soepFileName = vo.getContent().getName();
        WebLink pageLink = new WebLink(String.format(SoepConstants.ACCESS_FILE_URL, SoepConstants.TREE, soepFileName));
        pageLink.setName(SoepConstants.VIEW_TREE);
        pageLink.setType(WebLinkType.ViewURL);
        links.add(pageLink);

        // View SOEP dataset file source ("raw" representation) on GitHub
        WebLink sourceLink = new WebLink(vo.getContent().getHtmlUrl());
        sourceLink.setName(SoepConstants.VIEW_RAW);
        sourceLink.setType(WebLinkType.SourceURL);
        links.add(sourceLink);

        // The logo link
        links.add(SoepDataCiteConstants.LOGO_WEB_LINK);

        // Add all the links to the document;
        document.addWebLinks(links);

        // E2: RepositoryIdentifier
        document.setRepositoryIdentifier(SoepDataCiteConstants.REPOSITORY_ID);

        // E3. ResearchData{dataIdentifier, dataURL, dataLabel, dataType}
        final List<ResearchData> files = new LinkedList<>();
        final String fileType = vo.getContent().getDownloadUrl().substring(vo.getContent().getDownloadUrl()
                                                                           .lastIndexOf('.') + 1).toUpperCase();
        final ResearchData researchData = new ResearchData(vo.getContent().getDownloadUrl(), fileType);
        researchData.setType(fileType);
        files.add(researchData);
        document.addResearchData(files);

        // E4: ResearchDiscipline
        document.addResearchDisciplines(SoepDataCiteConstants.DISCIPLINES);

        // Subjects
        document.addSubjects(SoepDataCiteConstants.SUBJECTS);

        // Sizes
        document.addSizes(Arrays.asList(String.format(SoepDataCiteConstants.SIZE_BYTES, vo.getContent().getSize())));

        // Add SOEP variables and concepts
        final SoepDataCiteExtension extension = new SoepDataCiteExtension();
        extension.addSoepDatasetVariables(getDatasetVariables(vo));
        document.addExtension(extension);

        return document;
    }


    /**
     * Retrieve the concept associated to a variable in DE and EN versions.
     *
     * @param conceptMetadata The list of VariableMetadata records that describe the dataset.
     * @return Concept The target concept associated to the variable
     * @author Robin Weiss, Fidan Limani
     */
    private Set<SoepConcept> getSoepConcepts(ConceptMetadata conceptMetadata){
        final Set<SoepConcept> conceptSet = new HashSet<>();

            conceptSet.add(new SoepConcept(
                                conceptMetadata.getConceptName(),
                                conceptMetadata.getLabelDE(),
                                SoepConstants.CONCEPT_LABEL_DE));

            conceptSet.add(new SoepConcept(
                                conceptMetadata.getConceptName(),
                                conceptMetadata.getLabel(),
                                SoepConstants.CONCEPT_LABEL_EN));

        return conceptSet.isEmpty() ? null : conceptSet;
    }


    /**
     * Retrieve variables associated to a dataset.
     * @param soepFileVO The name of the dataset for which variables are used in SOEP collection
     * @return List<SoepVariable> A list of SOEP-transformed variables
     */
    private List<SoepVariable> getDatasetVariables(SoepFileVO soepFileVO)
    {
        /* We decided to store a concept both in DE and EN labels, effectively creating two SoepConcepts per SOEP
            concept entry. */
        Set<SoepConcept> conceptSet;
        List<SoepVariable> soepVariableList = new LinkedList<>();

        /* For every VariableMetadata record for the dataset, convert it to SOEP variable and assign it
        (a set of) SOEP concepts */
        for (VariableMetadata vm : soepFileVO.getVariableMetadataRecords()) {
        /* The concept contains both DE and EN concept labels, as present in the CSV. We need to "reformat" it
           and store it */
            conceptSet = getSoepConcepts(soepFileVO.getVariableConceptRecordMap().get(vm.getConceptName()));

            /* Create and add a SOEP variable instance to the list */
            soepVariableList.add(new SoepVariable(vm.getVariableName(), vm.getSource(), conceptSet));
        }

        return soepVariableList;
    }
}