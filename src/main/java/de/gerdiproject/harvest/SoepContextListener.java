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
package de.gerdiproject.harvest;

import de.gerdiproject.harvest.harvester.SoepHarvester;

import javax.servlet.annotation.WebListener;

/**
 * This class initializes the SOEP de.gerdiproject.harvest.harvester and all objects that are required.
 */

@WebListener
public class SoepContextListener extends ContextListener<SoepHarvester>
{
    @Override
    protected String getServiceName()
    {
        return "SOEP Harvester Service";
    }
}