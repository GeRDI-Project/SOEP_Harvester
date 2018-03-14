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

package de.gerdiproject.harvest.soep.utils;

import java.io.*;

import de.gerdiproject.harvest.MainContext;
import de.gerdiproject.harvest.soep.constants.SoepLoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;

/**
 * Interface with SOEP's GitHub repo: create, clone, update (if required)
 * @author Fidan Limani
 */
public class JGitUtil
{
    // Local base GitHub dir
    private File gitDir;

    private SoepIO sIO;
    private String repoName;
    private String repoRemoteUri;
    private File localFileRepo;
    private File remoteFileRepo;
    private Git remoteGit;
    private Git localGit;

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitUtil.class);

    /**
     *  Constructor
     *  @param repoName Repository name
     *  @param repoRemoteUri The URI of the remote repository
     *  @throws IOException thrown in case there are issues with creating the local repository
     *  @see IOException
    */
    public JGitUtil(String repoName, String repoRemoteUri) throws IOException
    {
        this.sIO = new SoepIO();

        // The base GitHub directory created on [user.home] path
        this.gitDir = sIO.createWorkingDir();

        this.repoName = repoName;
        this.repoRemoteUri = repoRemoteUri;
        localFileRepo = new File(gitDir + "/" + repoName + "/" + "local");
        remoteFileRepo = new File(gitDir + "/" + repoName + "/" + "remote");
        remoteGit = localGit = null;
    }

    /**
     * This method sets up a local repository, then fetches and updates it from a remote repo. URI
     * @throws IOException An issue while creating the local repo.
     * @throws GitAPIException Issue accessing the remote repo.
     */
    public static void collect() throws IOException, GitAPIException
    {
        // Init a repository: setup, initialize and clone
        JGitUtil gHubSoep = new JGitUtil("SOEP-core", SoepLoggingConstants.SOEP_REMOTE_REPO);
        gHubSoep.setUp();

        // Synchronize local repository (if out of sync.)
        if (gHubSoep.fetchRepo(SoepLoggingConstants.ORIGIN_MASTER))
            gHubSoep.updateRepo();
    }

    /**
     * Set the local repository: initialize and clone if local repo does not exist.
     * @throws IOException An issue while creating the local repo.
     * @throws GitAPIException Issue accessing the remote repo.
     */
    public void setUp() throws GitAPIException, IOException
    {
        if (repoExists()) {
            LOGGER.info(String.format(SoepLoggingConstants.REPO_EXISTS, repoName));

            // A repo. exists during setUp()
            setLocalGit();
        } else {
            // Init and clone repo.
            remoteGit = initRepo();
            localGit = cloneRepo();

            // Check out only certain directory (future work!);
            // checkOut();
        }
    }

    /**
     * Initialize repository: in case it exists, get a reference to it
     * @return Git Reference to initialized local repo.
     * @throws IOException An issue while creating the local repo.
     * @throws GitAPIException Issue accessing the remote repo.
     */
    public Git initRepo() throws GitAPIException, IOException
    {
        LOGGER.info(String.format(SoepLoggingConstants.INIT_REPO, repoName));
        return Git.init().setDirectory(remoteFileRepo).call();
    }

    /**
     * Clone (SOEP) repository
     * @return Git Reference to local repo. after cloning
     * @throws IOException An issue while creating the local repo.
     * @throws GitAPIException Issue accessing the remote repo.
     */
    public Git cloneRepo() throws GitAPIException, IOException
    {
        LOGGER.info(String.format(SoepLoggingConstants.CLONE_REPO, repoRemoteUri));
        return Git.cloneRepository()
               .setProgressMonitor(new TextProgressMonitor(new PrintWriter(new OutputStreamWriter(System.out, MainContext.getCharset()))))
               .setURI(repoRemoteUri)
               .setDirectory(localFileRepo)
               .call();
    }

    /**
     * Future development: Checkout certain GitHub project subdirectory. This might be required especially if we have
     * research communities that use GitHub to share their datasets.
     * public void checkOut() throws GitAPIException {
            // repoBranch: ORIGIN_MASTER; ElasticSearch project path: "origin/dataset"
            System.out.printf("%nCheck out path <%s> on branch <%s>.", datasetPath, ORIGIN_MASTER);
            getLocalGit().checkout().setStartPoint(ORIGIN_MASTER).addPath(datasetPath).call();
        }
    */

    /**
     *  Checks whether a certain local repository already exists, or needs to be set up.
     *  @return boolean If a certain local repo. exists or not
     */
    public boolean repoExists()
    {
        boolean status = false;

        try {
            Git.open(remoteFileRepo);
            status = true;
        } catch (RepositoryNotFoundException e) {
            LOGGER.error(SoepLoggingConstants.REPO_MISSING_ERROR, e);
        } catch (IOException e) {
            LOGGER.error(SoepLoggingConstants.IO_EXCEPTION_ERROR, e);
        }

        return status;
    }

    /**
     *  Fetch changes from remote repo.
     *  @param repoBranch The repo. branch of interest
     *  @return boolean Denotes whether there were any updates from the remote repo. fetched
     */
    public boolean fetchRepo(String repoBranch)
    {
        boolean status = false;
        LOGGER.info(String.format(SoepLoggingConstants.REPO_BRANCH_UPDATE, repoBranch));
        ProgressMonitor monitor = new TextProgressMonitor(new PrintWriter(new OutputStreamWriter(System.out, MainContext.getCharset())));

        try {
            FetchResult fetch = getLocalGit().fetch().setCheckFetchedObjects(true).setProgressMonitor(monitor).call();
            TrackingRefUpdate refUpdate = fetch.getTrackingRefUpdate(repoBranch);

            if (refUpdate != null) {
                RefUpdate.Result result = refUpdate.getResult();
                LOGGER.info(String.format(SoepLoggingConstants.UPDATES_AVAILABLE, result.toString()));
                status = true;
            } else
                LOGGER.info(SoepLoggingConstants.LOCAL_REPO_UPDATED);
        } catch (GitAPIException e) {
            LOGGER.error(SoepLoggingConstants.GIT_API_EXCEPTION_ERROR, e);
        }

        return status;
    }

    /**
     *  Update local repo.
     *  @throws GitAPIException That stems from unsupported encoding while tracking the repo. update
     *
     */
    public void updateRepo()throws GitAPIException
    {
        LOGGER.info(String.format(SoepLoggingConstants.UPDATE_LOCAL_REPO, localFileRepo.getAbsolutePath()));
        ProgressMonitor monitor = new TextProgressMonitor(new PrintWriter(new OutputStreamWriter(System.out, MainContext.getCharset())));

        // Check out the dataset of interest before pulling resources to local repo. (Future work!!!)
        // checkOut();

        try
            (Git git = getLocalGit()) {
            PullResult pullRes = git.pull()
                                 .setProgressMonitor(monitor)
                                 .setStrategy(MergeStrategy.THEIRS)
                                 .call();

            if (pullRes.isSuccessful()) {
                LOGGER.info(SoepLoggingConstants.UPDATE_COMPLETE);
            }
        }
    }

    /**
     * If field <localGit> is null, assign it a Git reference;
     * In case <b>localGit</b> field is {@code null}, assign it a Git reference
     */
    public void setLocalGit()
    {
        if (localGit == null) {
            try {
                localGit = Git.open(localFileRepo);
            } catch (IOException e) {
                LOGGER.error(SoepLoggingConstants.IO_EXCEPTION_ERROR, e);
            }
        }
    }

    /**
     * @return Git Getter for local git repo.
     */
    public Git getLocalGit()
    {
        return localGit;
    }

    /**
     * @return Git Getter for remote git repo.
     */
    public Git getRemoteGit()
    {
        return remoteGit;
    }

    /**
     * @return String Getter for remote repo. URI
     */
    public String getRepoRemoteUri() { return repoRemoteUri; }

    /**
     * @param repoName Set repo. name
     */
    public void setRepoName(String repoName)
    {
        LOGGER.info(String.format(SoepLoggingConstants.SET_REPO_NAME, repoName));
        this.repoName = repoName;
    }

    /**
     * @param repoRemoteUri Set remote repo. URI
     */
    public void setRepoRemoteUri(String repoRemoteUri)
    {
        LOGGER.info(String.format(SoepLoggingConstants.SET_REMOTE_REPO_URL, repoRemoteUri));
        this.repoRemoteUri = repoRemoteUri;
    }
}