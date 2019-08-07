/**
 * Copyright © 2017 Fidan Limani, Robin Weiss (http://www.gerdi-project.de)
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.extractors.SoepFileVO;
import de.gerdiproject.harvest.github.json.GitHubContent;
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
    public void init(final AbstractETL<?, ?> etl)
    {
        // nothing to retrieve from the ETL
    }


    @Override
    protected DataCiteJson transformElement(final SoepFileVO vo) throws TransformerException
    {
        // Specify source ID for harvested file
        final DatasetMetadata metadata = vo.getDatasetMetadata();

        final String sourceTitle = metadata.getLabel();
        final GitHubContent content = vo.getContent();

        // Create the document to contain SOEP metadata for every given file from its dataset
        final DataCiteJson document = new DataCiteJson(content.getPath());

        document.addFormats(SoepDataCiteConstants.FORMATS);

        // (ID  1) Identifier: This is the DOI identifier for v33 of the dataset
        document.setIdentifier(new Identifier(SoepDataCiteConstants.IDENTIFIER, IdentifierType.DOI));

        // (ID  2) Creators
        document.addCreators(SoepDataCiteConstants.CREATORS);

        // (ID  3 Title) Individual file descriptions
        document.addTitles(Arrays.asList(new Title(sourceTitle)));

        // (ID  4) Publisher
        document.setPublisher(SoepDataCiteConstants.PROVIDER);

        // (ID  5) PublicationYear: 2017
        document.setPublicationYear(SoepDataCiteConstants.PUBLICATION_YEAR);

        // (ID  6) Subjects
        document.addSubjects(SoepDataCiteConstants.SUBJECTS);

        // (ID  7) Contributor
        document.addContributors(Arrays.asList(SoepDataCiteConstants.COLLECTOR_CONTRIBUTOR));

        // (ID  8) Date: dateType="Collected" with individual data collection dates.
        document.addDates(getDates(metadata));

        // (ID 10) ResourceType
        document.setResourceType(SoepDataCiteConstants.RESOURCE_TYPE);

        // (ID 13) Size
        document.addSizes(Arrays.asList(String.format(SoepDataCiteConstants.SIZE_BYTES, content.getSize())));

        // (ID 15) Dataset version
        document.setVersion(SoepDataCiteConstants.VERSION);

        // (ID 16) Rights
        document.addRights(Arrays.asList(new Rights(SoepDataCiteConstants.RIGHTS_VALUE)));

        // (ID 17) Description, type "Abstract"
        document.addDescriptions(getDescriptions(metadata));

        // GeRDI Extension

        // (E 1) WebLinks
        document.addWebLinks(getWebLinks(content));

        // (E 2) RepositoryIdentifier
        document.setRepositoryIdentifier(SoepDataCiteConstants.REPOSITORY_ID);

        // (E 3) ResearchData
        document.addResearchData(getResearchData(content));

        // (E 4) ResearchDiscipline
        document.addResearchDisciplines(SoepDataCiteConstants.DISCIPLINES);

        // (E 5) Extensions
        final SoepDataCiteExtension extension = new SoepDataCiteExtension();
        extension.addSoepDatasetVariables(getDatasetVariables(vo));
        document.addExtension(extension);

        return document;
    }


    /**
     * Retrieves dates of the SOEP document.
     *
     * @param metadata metadata that contains relevant information
     *
     * @return a list of {@linkplain AbstractDate}s
     */
    private Collection<AbstractDate> getDates(final DatasetMetadata metadata)
    {
        final List<AbstractDate> dates = new LinkedList<>();

        dates.add(SoepDataCiteConstants.PUBLICATION_DATE);

        /* (ID 8) Date: dateType="Collected" with individual data collection dates. PublicationYear is too "matchy" ;)
         *  If year=0 or "long", set the "1984-2016" range.
         */
        final String tempPeriod = metadata.getPeriodName();

        if ("0".equals(tempPeriod) || "long".equals(tempPeriod))
            dates.add(SoepDataCiteConstants.PUBLICATION_RANGE);
        else
            dates.add(new Date(tempPeriod, DateType.Collected));

        return dates;
    }


    /**
     * Retrieves descriptions of the SOEP document.
     *
     * @param metadata metadata that contains relevant information
     *
     * @return a list of {@linkplain Description}s
     */
    private List<Description> getDescriptions(final DatasetMetadata metadata)
    {
        final List<Description> descriptions = new LinkedList<>();

        // add default description
        descriptions.add(
            new Description(
                SoepDataCiteConstants.DESCRIPTION_VALUE,
                DescriptionType.Abstract,
                SoepDataCiteConstants.DESCRIPTION_LANGUAGE));

        // add optional description from metadata
        final String metadataDesc = metadata.getDescription();

        if (metadataDesc != null && !metadataDesc.isEmpty())
            descriptions.add(new Description(
                                 metadataDesc,
                                 DescriptionType.Other,
                                 SoepDataCiteConstants.DESCRIPTION_LANGUAGE));

        return descriptions;
    }


    /**
     * Retrieves research data related to the SOEP document.
     *
     * @param content GitHub content that contains relevant information
     *
     * @return a list of {@linkplain ResearchData}
     */
    private List<ResearchData> getResearchData(final GitHubContent content)
    {
        final List<ResearchData> researchDataList = new LinkedList<>();

        final String downloadUrl = content.getDownloadUrl();
        final String fileType = downloadUrl
                                .substring(downloadUrl.lastIndexOf('.') + 1)
                                .toUpperCase(Locale.ENGLISH);

        researchDataList.add(new ResearchData(downloadUrl, fileType, fileType));
        return researchDataList;
    }


    /**
     * Retrieves links related to the SOEP document.
     *
     * @param content GitHub content that contains relevant information
     *
     * @return a list of {@linkplain WebLink}s
     */
    private List<WebLink> getWebLinks(final GitHubContent content)
    {
        final List<WebLink> links = new LinkedList<>();

        // View SOEP dataset file on GitHub
        final String pageUrl = String.format(
                                   SoepConstants.ACCESS_FILE_URL,
                                   SoepConstants.TREE,
                                   content.getName());

        links.add(new WebLink(pageUrl, SoepConstants.VIEW_TREE, WebLinkType.ViewURL));
        links.add(new WebLink(content.getHtmlUrl(), SoepConstants.VIEW_RAW, WebLinkType.SourceURL));
        links.add(SoepDataCiteConstants.LOGO_WEB_LINK);

        return links;
    }


    /**
     * Retrieve the concept associated to a variable in DE and EN versions.
     *
     * @param conceptMetadata The list of VariableMetadata records that describe the dataset.
     * @return Concept The target concept associated to the variable
     * @author Robin Weiss, Fidan Limani
     */
    private Set<SoepConcept> getSoepConcepts(final ConceptMetadata conceptMetadata)
    {
        if (conceptMetadata == null)
            return null;

        final Set<SoepConcept> conceptSet = new HashSet<>();

        conceptSet.add(new SoepConcept(
                           conceptMetadata.getConceptName(),
                           conceptMetadata.getLabelDE(),
                           SoepConstants.CONCEPT_LABEL_DE));

        conceptSet.add(new SoepConcept(
                           conceptMetadata.getConceptName(),
                           conceptMetadata.getLabel(),
                           SoepConstants.CONCEPT_LABEL_EN));

        return conceptSet;
    }


    /**
     * Retrieve variables associated to a dataset.
     * @param soepFileVO The name of the dataset for which variables are used in SOEP collection
     * @return List<SoepVariable> A list of SOEP-transformed variables
     */
    private List<SoepVariable> getDatasetVariables(final SoepFileVO soepFileVO)
    {
        /* We decided to store a concept both in DE and EN labels, effectively creating two SoepConcepts per SOEP
            concept entry. */
        Set<SoepConcept> conceptSet;
        final List<SoepVariable> soepVariableList = new LinkedList<>();

        /* For every VariableMetadata record for the dataset, convert it to SOEP variable and assign it
        (a set of) SOEP concepts */
        for (final VariableMetadata vm : soepFileVO.getVariableMetadataRecords()) {
            /* The concept contains both DE and EN concept labels, as present in the CSV. We need to "reformat" it
               and store it */
            conceptSet = getSoepConcepts(soepFileVO.getVariableConceptRecordMap().get(vm.getConceptName()));

            /* Create and add a SOEP variable instance to the list */
            soepVariableList.add(new SoepVariable(vm.getVariableName(), vm.getSource(), conceptSet));
        }

        return soepVariableList;
    }


    @Override
    public void clear()
    {
        // nothing to clean up
    }
}