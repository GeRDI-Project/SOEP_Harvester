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

package soep.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;

public class JsonReader {
    private JsonParser jsonParser;

    // Important SOEP Metadata elements
    private String title;
    private Date publicationDate;

    public JsonReader(){
        jsonParser = new JsonParser();
    }

    /* This method returns SOEP metadata elements of interest from a dataset file.
       The method should return the results, or assign every metadata element to the appropriate GeRDI Schema element.
    */
    public void getElements() throws FileNotFoundException {
        Object obj = jsonParser.parse(new FileReader("e1.json"));
        JsonObject jsonObject = (JsonObject) obj;

        String study = jsonObject.get("study").getAsString();
        System.out.printf("%nStudy: %s", study);

        String questionnaire = jsonObject.get("questionnaire").getAsString();
        System.out.printf("%nStudy: %s", questionnaire);

        JsonObject questions = jsonObject.get("questions").getAsJsonObject();
        JsonObject q0Label = questions.get("0").getAsJsonObject();
        System.out.printf("%nQuestion 0: %s", q0Label.get("label").getAsString());
    }

    public static void main(String[] args) throws FileNotFoundException {
        JsonReader reader = new JsonReader();
        reader.getElements();
    }
}