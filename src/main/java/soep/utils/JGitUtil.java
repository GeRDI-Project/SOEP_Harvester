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

/** Interface with SOEP's GitHub repo: create, clone, update (if required) operations
 * @author Fidan Limani
 */
import java.io.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.lib.ProgressMonitor;

/* Notes
    + 0. Create local repository to hold SOEP dataset; in case there already is one, just get back the reference to it
    in order to support the next step - repo. cloning.
    + 1. Clone SOEP locally:
    + 2. Check whether a repo. with certain name already exists
    + 3. Fetch changes in remote repo: return a boolean value after comparing local and remote repos.
    + 4. Update local repo from remote URI
* */

public class JGitUtil {
    private File gitDir; // local base GitHub dir
    private SoepIO sIO;
    private String repoName;
    private String repoRemoteUri;
    private String datasetPath;
    private File localFileRepo;
    private File remoteFileRepo;
    private Git remoteGit;
    private Git localGit;
    private final String MASTER = "refs/heads/master"; // HEAD of local (cloned) repo
    private final String ORIGIN_MASTER = "refs/remotes/origin/master"; // HEAD in remote repo

    // Constructor
    public JGitUtil(String repoName, String repoRemoteUri, String datasetPath) throws IOException {
        this.sIO = new SoepIO();
        this.gitDir = sIO.createWorkingDir(); // The base GitHub directory created on [user.home] path
        this.repoName = repoName;
        this.datasetPath = datasetPath;
        this.repoRemoteUri = repoRemoteUri;
        localFileRepo = new File(gitDir + File.separator + repoName + File.separator + "local");
        remoteFileRepo = new File(gitDir + File.separator + repoName + File.separator + "remote");
        remoteGit = localGit = null;
    }

    public static void collect() throws IOException, GitAPIException {
        // SOEP-core GitHub project attributes
        String soepRemoteRepo = "https://github.com/paneldata/soep-core";
        String soepDatasetPath = "ddionrails/datasets"; // SOEP directory of interest

        // Init & clone a repository: ElasticSearch porject
        JGitUtil gHubSoep = new JGitUtil("SOEP-core", soepRemoteRepo, soepDatasetPath);

        // Setup, initialize and clone repository
        gHubSoep.setUp();

        // Synchronize local repository (when out of sync.)
        if(gHubSoep.fetchRepo(gHubSoep.ORIGIN_MASTER)) {
            gHubSoep.updateRepo();
        }
    }

    // Set up the local repository: initialize and clone, if local repo does not exist
    public void setUp() throws GitAPIException, IOException {
        if(repoExists()) {
            System.out.println("Repo <" + repoName + "> exists.");
            setLocalGit(); // A repo exists during setUp()
        } else {
            remoteGit = initRepo(); // init and clone repo
            localGit = cloneRepo();
            // checkOut(); // check out only certain directory (future work);
        }
    }

    // 0. Initialize repository: in case it exists, get a reference to it
    public Git initRepo() throws GitAPIException, IOException {
        System.out.println("\nInitializing repository <" + repoName + ">");
        return Git.init().setDirectory(remoteFileRepo).call();
    }

    // 1. Clone (SOEP) repository
    public Git cloneRepo() throws GitAPIException, IOException {
        System.out.println("\nCloning remote repository from <" + repoRemoteUri + ">");
        return Git.cloneRepository()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setURI(repoRemoteUri)
                .setDirectory(localFileRepo)
                .call();
    }

    /* Future development
    // 1.2 Checkout
    public void checkOut() throws GitAPIException {
        // repoBranch: ORIGIN_MASTER; ElasticSearch project path: "origin/dataset"
        System.out.printf("%nCheck out path <%s> on branch <%s>.", datasetPath, ORIGIN_MASTER);
        getLocalGit().checkout().setStartPoint(ORIGIN_MASTER).addPath(datasetPath).call();
    }
    */

    /* 2. Does the repo exists? This method could take a parameter, in case there are more repositories available
            locally: gitDir field is assigned the root GitHub directory! */
    public boolean repoExists() {
        boolean status = false;
        try {
            Git tempGit = Git.open(remoteFileRepo);
            status = true;
        } catch (RepositoryNotFoundException e){
            System.err.println("Repository does not exist. To be created next.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    // 3. Fetch changes in remote repo
    public boolean fetchRepo(String repoBranch){
        boolean status = false;
        System.out.printf("Repository updates from <%s> available?",  repoBranch);
        ProgressMonitor monitor = new TextProgressMonitor(new PrintWriter(System.out));
        try{
            FetchResult fetch = getLocalGit().fetch().setCheckFetchedObjects(true).setProgressMonitor(monitor).call();
            TrackingRefUpdate refUpdate = fetch.getTrackingRefUpdate(repoBranch);
            if(refUpdate != null) {
                RefUpdate.Result result = refUpdate.getResult();
                System.out.printf("%n\tUpdates available. Pull changes!"); // We represent this information via the
                status = true;                                          // return value in <status> of the method.
            } else {
                System.out.printf("%n\t(Local) Repository up to date.");
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return status;
    }

    // 4. Update local repo from remote URI
    public void updateRepo()throws GitAPIException {
        System.out.println("\nUpdating local repository <" + localFileRepo.getAbsolutePath() + ">");
        ProgressMonitor monitor = new TextProgressMonitor(new PrintWriter(System.out));

        // checkOut(); // Check out the dataset of interest before pulling resources to local repo. (future work)

        try (Git git = getLocalGit()){
            PullResult pullRes = git.pull()
                    .setProgressMonitor(monitor)        // URI: http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/merge/MergeStrategy.html
                    .setStrategy(MergeStrategy.THEIRS) // Simple strategy that sets the output tree to the second input tree.
                    .call();

            if(pullRes.isSuccessful()){
                String head = git.reset()   // field <head> used for testing purposes
                        .setMode(ResetCommand.ResetType.HARD)
                        .setRef(ORIGIN_MASTER)
                        .call()
                        .getName();
                System.out.println("%nRepository successfully updated.");
            }
        }
    }

    /*
        Multiple methods that were used to assess different aspects of Git repositories; they could be used in the
        future development of the harvester, hence their inclusion at this point.
    * */

    // Exploring the remote repository
    public void exploreRepo(String repoBranch) throws IOException {
        Repository repo = getLocalGit().getRepository();
        System.out.printf("\nExploring branch: %s%n%n", repoBranch);
        try(RevWalk revWalk = new RevWalk(repo)){
            ObjectId commitId = repo.resolve(repoBranch);

            revWalk.markStart(revWalk.parseCommit(commitId));
            for(RevCommit commit : revWalk){
                System.out.println("Commit message: " + commit.getFullMessage());
            }
        }
    }

    // Repository information
    public void repoInfo(){
        Map<String, Ref> refs = getLocalGit().getRepository().getAllRefs();
        System.out.printf("%nAll Refs (%d)%n", refs.size());

        Ref head = refs.get(Constants.HEAD);
        if (head == null) {
            System.out.println("HEAD ref is dead and/or non-existent?");
            return;
        }

        Map<String, Ref> printRefs = new TreeMap();
        String current = head.getLeaf().getName(); // Start from the HEAD ref of the repository;
        if (current.equals(Constants.HEAD)) {
            printRefs.put("(no branch)", head);
        }

        for (Ref ref : RefComparator.sort(refs.values())) {
            String name = ref.getName();
            if (name.startsWith(Constants.R_HEADS) || name.startsWith(Constants.R_REMOTES)) {
                printRefs.put(name, ref);
            }
        }

        int maxLength = 0; // Find the longest key in Map<String, Ref> printRefs; to be used during print out.
        for (String name : printRefs.keySet()) {
            maxLength = Math.max(maxLength, name.length());
        }

        System.out.printf("Refs (Heads/Remotes) (%d)%n", printRefs.size());
        for (Map.Entry<String, Ref> e : printRefs.entrySet()) {
            Ref ref = e.getValue();
            ObjectId objectId = ref.getObjectId();
            System.out.printf("%c %-" + maxLength + "s %s%n", (current.equals(ref.getName()) ? '*' : ' '), e.getKey(),
                    objectId.abbreviate(8).name()); // Could be abbreviated to any lenght;
        }
    }

    public void listBranches() throws GitAPIException {
        System.out.println("\nListing remote branches for: " + getLocalGit().getRepository().toString());
        List<Ref> branches = localGit.branchList().call();
        for (Ref ref : branches) {
            System.out.printf("<Branch> %s %n<Branch name> %s %n<Branch Object ID> %s",
                    ref, ref.getName(), ref.getObjectId().getName());
        }
    }

    // Find SOEP dataset from cloned repository: maybe return SHA-1value(s) of the files that comprise the dataset
    public void idDatasetFiles(Repository repo, String searchTerm) throws IOException {
        ObjectId lastCommitId = repo.resolve(Constants.HEAD);

        try(RevWalk revWalk = new RevWalk(repo)){
            RevCommit commit = revWalk.parseCommit(lastCommitId);

            // Use the tree of the latest commit to find files of interest
            RevTree tree = commit.getTree();

            // Find a specific file
            try(TreeWalk tWalk = new TreeWalk(repo)){
                tWalk.addTree(tree);
                tWalk.setRecursive(true);
                tWalk.setFilter(PathFilter.create(searchTerm)); // E.g.: datasets/abroad.json

                /*
                if(!tWalk.next()){
                    throw new IllegalStateException("Did not find" + searchTerm);
                }
                */

                ObjectId objId = tWalk.getObjectId(0);
                ObjectLoader loader = repo.open(objId);
                loader.copyTo(System.out);
            }

            revWalk.dispose();
        }
    }

    /*
        Getters, setters, and utility methods
    * */
    public Git getLocalGit() {
        return localGit;
    }
    public Git getRemoteGit() {
        return remoteGit;
    }
    public String getRepoRemoteUri() { return repoRemoteUri; }

    public void setRepoName(String repoName){
        System.out.println("\nSetting the repository name to: " + repoName);
        this.repoName = repoName;
    }
    public void setRepoRemoteUri(String repoRemoteUri){
        System.out.println("\nSetting the remote repository URI to: " + repoRemoteUri);
        this.repoRemoteUri = repoRemoteUri;
    }

    // If field <localGit> is null, assign it a Git reference;
    public void setLocalGit(){
        if(localGit == null){
            try {
                localGit = Git.open(localFileRepo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Demo the application
    */
    public static void main(String[] args) throws IOException, GitAPIException {
        // ElasticSearch GitHub project
        String esRemoteRepo = "https://github.com/fidanLimani/ElasticSearch";
        String esDatasetPath = "dataset"; // ElasticSearch GitHub path: master/dataset

        JGitUtil.collect();

        System.out.printf("%n%nApplication completed.");
    }
}