/**
 * Copyright © 2017 Fidan Limani (http://www.gerdi-project.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gerdiproject.harvest.soep.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.gerdiproject.json.datacite.Creator;
import de.gerdiproject.json.datacite.ResourceType;
import de.gerdiproject.json.datacite.enums.NameType;
import de.gerdiproject.json.datacite.enums.ResourceTypeGeneral;
import de.gerdiproject.json.datacite.extension.WebLink;
import de.gerdiproject.json.datacite.extension.abstr.AbstractResearch;
import de.gerdiproject.json.datacite.extension.constants.ResearchDisciplineConstants;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;
import de.gerdiproject.json.datacite.nested.PersonName;

/**
 * This static class contains constants that are used for creating DataCite documents of SOEP.
 * @author Fidan Limani
 */
public class SoepDataCiteConstants
{
    // Resource type
    public static final ResourceType RESOURCE_TYPE = createResourceType();

    // CREATOR
    public static final List<Creator> CREATORS = createCreators();

    // SOURCE
    public static final String PROVIDER = "German Socio-Economic Panel Study (SOEP)";
    public static final String PROVIDER_URI = "https://github.com/paneldata/de.gerdiproject.harvest.soep-core";
    public static final String REPOSITORY_ID = "SOEP";
    public static final List<AbstractResearch> DISCIPLINES =  createResearchDisciplines();

    // CONTRIBUTORS
    public static final String CONTRIBUTOR_COLLECTOR = "Kantar Deutschland GmbH";
    public static final String METADATA_CONTACT_NAME = "Contact name";
    public static final String METADATA_CONTACT_ORGANISATION = "Contact organisation";
    public static final short EARLIEST_PUBLICATION_YEAR = 1984;

    // WEB LINKS
    public static final String VIEW_URL = "https://github.com/paneldata/de.gerdiproject.harvest.soep-core/tree/master/ddionrails/datasets";
    public static final WebLink LOGO_WEB_LINK = createLogoWebLink();
    public static final String TEMPLATE_DOCUMENT_NAME = "About";

    // DATES
    public  static final String DATE_COLLECTED = "1984/2016";
    public static final String META_DATA_TIME_COVERAGE = "Time coverage";
    public static final String META_DATA_LAST_UPDATE = "Metadata last update";

    // Dataset version
    public static final String VERSION = "33";

    // DESCRIPTIONS
    public static final String DESCRIPTION_FORMAT = "%s:%n%s";
    public static final String DESCRIPTION_VALUE = "The German Socio-Economic Panel (SOEP) study is a wide-ranging representative " +
                                                    "longitudinal study of private households, located at the German Institute for " +
                                                    "Economic Research, DIW Berlin. Every year, there were nearly 15,000 households, " +
                                                    "and more than 25,000 persons sampled by the fieldwork organization TNS Infratest " +
                                                    "Sozialforschung. The data provide information on all household members, consisting " +
                                                    "of Germans living in the Eastern and Western German States, foreigners, and immigrants " +
                                                    "to Germany. The Panel was started in 1984. Some of the many topics include household composition, " +
                                                    "occupational biographies, employment, earnings, health and satisfaction indicators. As early " +
                                                    "as June 1990—even before the Economic, Social and Monetary Union—SOEP expanded to include the " +
                                                    "states of the former German Democratic Republic (GDR), thus seizing the rare opportunity to observe " +
                                                    "the transformation of an entire society. Also immigrant samples were added in 1994/95 and 2013/2015 to " +
                                                    "account for the changes that took place in Germany society. Two samples of refugees were introduced in 2016. " +
                                                    "Further new samples were added in 1998, 2000, 2002, 2006, 2009, 2010, 2011, and 2012. The survey is constantly " +
                                                    "being adapted and developed in response to current social developments. The international version contains 95% of " +
                                                    "all cases surveyed (see 10.5684/soep.v33i).";
    public static final String DESCRIPTION_LANGUAGE = "EN";

    // Rights
    public static final String RIGHTS_VALUE = "The SOEP micro data which we make available for scientific research can " +
                                                "only be interpreted using statistical software. Direct use of SOEP data " +
                                                "is subject to the high standards for lawful data protection in the " +
                                                "Federal Republic of Germany. Signing a contract on data distribution " +
                                                "with the DIW Berlin is therefore a precondition for working with SOEP " +
                                                "data. After signing the contract, the data of every new wave will be " +
                                                "available on request.";

    // FORMATS
    public static final List<String> FORMATS = Collections.unmodifiableList(Arrays.asList("JSON"));

    /**
     * Private constructor, because this is a static class.
     */
    private SoepDataCiteConstants() {}

    /**
     * Initializes a WebLink that leads to SOEP logo.
     *
     * @return a link to the SOEP logo
     */
    private static WebLink createLogoWebLink()
    {
        WebLink logoLink = new WebLink("https://www.diw.de/documents/bildarchiv/37/diw_02.c.239717.de/de.gerdiproject.harvest.soep-logo.jpg");
        logoLink.setName("Logo");
        logoLink.setType(WebLinkType.ProviderLogoURL);
        return logoLink;
    }

    /**
     * Initializes a Creator dummy for all SOEP documents.
     *
     * @return a Creator that has "SOEP" as name
     */
    private static List<Creator> createCreators()
    {
        Creator creator = new Creator(new PersonName(PROVIDER, NameType.Organisational));
        return Arrays.asList(creator);
    }

    /**
     * Initializes the only ResourceType of all SOEP documents;
     * @return a ResourceType representing JSON datasets;
     */
    private static ResourceType createResourceType()
    {
        return  new ResourceType("JSON", ResourceTypeGeneral.Dataset);
    }

    /**
     * This method assign the two closest matching research disciplines for SOEP
     * @return A collection of research disciplines for SOEP
     */
    private static List<AbstractResearch> createResearchDisciplines(){
        return Collections.unmodifiableList(Arrays.asList(
                                                ResearchDisciplineConstants.EMPIRICAL_SOCIAL_RESEARCH,
                                                ResearchDisciplineConstants.STATISTICS_AND_ECONOMETRICS));
    }
}