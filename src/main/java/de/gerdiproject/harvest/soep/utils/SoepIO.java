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

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A util-like class to support repo- and de.gerdiproject.harvest.harvester-based operations.
 *
 * @author Fidan Limani
 */
public class SoepIO
{
    private String gitHubPath; // Required in SoepHarvester class
    private static final Logger logger = LoggerFactory.getLogger(SoepIO.class);

    public SoepIO()
    {
        this.gitHubPath = System.getProperty("user.home") + File.separator + "GitHub" + File.separator;
    }

    public File createWorkingDir() throws IOException
    {
        File dir = new File(getGitHubPath());

        if (dir.exists()) {
            logger.info(dir + " already exists");
            // System.out.println("Canonical path: " + dir.getCanonicalPath());
            // System.out.println("dir toString(): " + dir.toString());
            return dir;
        } else if (dir.mkdirs()) {
            logger.info(dir + " was created");
            return dir;
        } else {
            logger.info(dir + " was not created");
            return null;
        }
    }

    /** Does a repo already exist?
     * @param repoName Path to the local SOEP repo (SOEP-core, in this case)
     * @return
     * @throws IOException
     */
    public boolean repoExists(String repoName) throws IOException
    {
        File file = new File(this.getGitHubPath() + repoName);

        return file.exists();
    }

    /** IO operations to support the de.gerdiproject.harvest.harvester
     * @param folderPath The dataset of a GitHub repo
     */
    public List<File> listFiles(String folderPath)
    {
        File[] files = new File(folderPath).listFiles();
        List<File> fileList = new ArrayList<>();

        if(files != null){
            fileList = new ArrayList<>(Arrays.asList(files));
        }

        return fileList;
    }

    // Getter method for
    public String getGitHubPath()
    {
        return this.gitHubPath;
    }

    // Demo the app.
    public static void main(String[] args) throws IOException
    {
        SoepIO test = new SoepIO();
        String repo = "SOEP-core";
        /* Simple method tests */
        test.repoExists(repo);

        List<File> myList = test.listFiles(test.getGitHubPath() + repo);
        logger.info("# of files in the dataset: " + myList.size());
    }
}