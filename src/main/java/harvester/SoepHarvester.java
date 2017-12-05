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

package harvester;

import de.gerdiproject.harvest.IDocument;
import de.gerdiproject.harvest.harvester.AbstractListHarvester;

import org.eclipse.jgit.api.errors.GitAPIException;
import soep.utils.JGitUtil;
import soep.utils.SoepIO;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
    The main harvester
* */
public class SoepHarvester extends AbstractListHarvester<File> {
    private String harvesterName;
    private String datasetPath; // The dataset location in a GitHub repo

    // As suggested, the constructor should be in a "default" style
    public SoepHarvester(){
        super(1);
        this.harvesterName = "SOEP Harvester";
        this.datasetPath = "SOEP-core/local/ddionrails/datasets";
    }

    @Override
    protected Collection<File> loadEntries() {
        // Repo-related operations based on JGit library.
        try {
            JGitUtil.collect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        // Provide access to SOEP dataset files
        SoepIO sIO = new SoepIO();
        String datasetPath = sIO.gitHubPath + this.datasetPath;
        ArrayList<File> soepFiles = sIO.listFiles(datasetPath);

        return soepFiles;
    }

    @Override
    protected List<IDocument> harvestEntry(File soepFile) {
        // Which metadata elements from the dataset make sense for end users?
        return null;
    }

    @Override
    /*
        From the utils-like class, provide access to a collection of SOEP dataset files;
        The overridden implementation should add documents to the search index by calling the addDocument() or
        addDocuments() methods;
    * */
    protected boolean harvestInternal(int i, int i1) throws Exception {
        boolean status = false;

        return false;
    }

    @Override
    protected int initMaxNumberOfDocuments() {
        return 0;
    }

    @Override
    protected String initHash() throws NoSuchAlgorithmException, NullPointerException {
        return null;
    }

    @Override
    public void abortHarvest() { // its access was "protected" by default, but there was a compiler complaint...

    }

    // Demo the app
    public static void main(String[] args){
        // 1. Invoke JGitUtil methods to conduct SOEP dataset retrieval from GitHub

        // 2. Map and index SOEP dataset files retrieved by the previous step
    }
}
