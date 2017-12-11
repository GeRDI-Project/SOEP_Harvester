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

package soep.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.gerdiproject.json.datacite.Creator;
import de.gerdiproject.json.datacite.ResourceType;
import de.gerdiproject.json.datacite.enums.NameType;
import de.gerdiproject.json.datacite.enums.ResourceTypeGeneral;
import de.gerdiproject.json.datacite.extension.WebLink;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;
import de.gerdiproject.json.datacite.nested.PersonName;

/**
 * This static class contains constants that are used for creating DataCite documents of SOEP.
 */
public class SoepDataCiteConstants {
    // Resource type
    public static final ResourceType RESOURCE_TYPE = createResourceType();

    // CREATOR
    public static final List<Creator> CREATORS = createCreators();

    // SOURCE
    public static final String PROVIDER = "German Socio-Economic Panel Study (SOEP)";
    public static final String PROVIDER_URI = "https://github.com/paneldata/soep-core";
    public static final String REPOSITORY_ID = "SOEP";
    public static final List<String> DISCIPLINES = Collections.unmodifiableList(Arrays.asList("Socio-Economic"));

    // CONTRIBUTORS
    public static final String METADATA_CONTACT_NAME = "Contact name";
    public static final String METADATA_CONTACT_ORGANISATION = "Contact organisation";
    public static final short EARLIEST_PUBLICATION_YEAR = 1984;

    // WEB LINKS
    public static final String VIEW_URL = "https://github.com/paneldata/soep-core/tree/master/ddionrails/datasets";
    public static final WebLink LOGO_WEB_LINK = createLogoWebLink();
    public static final String TEMPLATE_DOCUMENT_NAME = "About";

    // DATES
    public static final String META_DATA_TIME_COVERAGE = "Time coverage";
    public static final String META_DATA_LAST_UPDATE = "Metadata last update";

    // DESCRIPTIONS
    public static final String DESCRIPTION_FORMAT = "%s:%n%s";

    // FORMATS
    public static final List<String> FORMATS = Collections.unmodifiableList(Arrays.asList("JSON"));

    /**
     * Private constructor, because this is a static class.
     */
    private SoepDataCiteConstants(){}

    /**
     * Initializes a WebLink that leads to SOEP logo.
     *
     * @return a link to the SOEP logo
     */
    private static WebLink createLogoWebLink(){
        WebLink logoLink = new WebLink("https://www.diw.de/documents/bildarchiv/37/diw_02.c.239717.de/soep-logo.jpg");
        logoLink.setType(WebLinkType.ProviderLogoURL);
        return logoLink;
    }

    /**
     * Initializes a Creator dummy for all SOEP documents.
     *
     * @return a Creator that has "SOEP" as name
     */
    private static List<Creator> createCreators(){
        Creator creator = new Creator(new PersonName(PROVIDER, NameType.Organisational));
        return Arrays.asList(creator);
    }

    /**
     * Initializes the only ResourceType of all SOEP documents;
     * @return a ResourceType representing JSON datasets;
     */
    private static ResourceType createResourceType()
    {
        ResourceType resType = new ResourceType("JSON", ResourceTypeGeneral.Dataset);
        return resType;
    }
}