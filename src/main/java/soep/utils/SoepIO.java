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

import java.io.File;
import java.io.IOException;

public class SoepIO {
    public static File createWorkingDir() throws IOException {
        String path = System.getProperty("user.home") + File.separator + "GitHub" + File.separator;
        // System.out.println("Path string: " + path);
        File dir = new File(path);
        if (dir.exists()) {
            System.out.println(dir + " already exists");
            // System.out.println("Canonical path: " + dir.getCanonicalPath());
            // System.out.println("dir toString(): " + dir.toString());
            return dir;
        } else if (dir.mkdirs()) {
            System.out.println(dir + " was created");
            return dir;
        } else {
            System.out.println(dir + " was not created");
            return null;
        }
    }

    // Does a repo already exist?
    public static boolean repoExists(String repoName) throws IOException {
        boolean status = false;
        String[] fileList = SoepIO.createWorkingDir().list();

        for(String str : fileList){
            if(repoName.equals(str)){
                System.out.println(repoName + " already exists!");
                status = true; //return Git reference to it!
            }
        }

        return status;
    }

    public static void main(String[] args) throws IOException {
        String repoName = "ElasticSearch";
        File testFile = SoepIO.createWorkingDir();
        System.out.println("File (canonical): " + testFile); // .getCanonicalFile()
        File localFileRepo = new File(testFile + File.separator + repoName + File.separator + "local");
        System.out.println("localFileRepo: " + localFileRepo);

        System.out.println("Project exists? " + SoepIO.repoExists("ElasticSearch"));
    }
}