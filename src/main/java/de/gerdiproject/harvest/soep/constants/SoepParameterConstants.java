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

package de.gerdiproject.harvest.soep.constants;

/**
 * This static class contains constants of SOEP de.gerdiproject.harvest.harvester parameters.
 *
 * @author Fidan Limani
 */
public class SoepParameterConstants
{
    public static final String VERSION_KEY = "version";
    public static final String LANGUAGE_KEY = "language";
    public static final String VERSION_DEFAULT = "v1";
    public static final String LANGUAGE_DEFAULT = "en"; // How to treat the support for 2 languages?


    /**
     * Private constructor, because this is a static class.
     */
    private SoepParameterConstants() {}
}
