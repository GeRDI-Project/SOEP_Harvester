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

package de.gerdiproject.harvest.soep.utils;

/*
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
*/

public class JsonReader
{
    // private JsonParser jsonParser;

    /* Important SOEP Metadata elements: TO be determined with SOEP community manager
        private String title;
        private Date publicationDate;
    */

    /* public JsonReader()
    {
        jsonParser = new JsonParser();
    }
    */

    /* Retrieve specific elements from a SOPE file (Future work)
    public List<String> getSoepMetadata(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> list = new ArrayList();
        // Test: retrieve a certain element from the JSON file
            Object obj = jsonParser.parse(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
            JsonObject jsonObject = (JsonObject) obj;
            JsonObject study = jsonObject.get("hhnr").getAsJsonObject();
            String label = study.get("label").getAsString();
            System.out.printf("%nStudy: %s", label); // The study "wave" information: "hhnrakt" -> "label": "Current Wave HH Number (=AHHNR)".

        return list;
    }
    */
}