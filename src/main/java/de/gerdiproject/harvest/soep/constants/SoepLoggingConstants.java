package de.gerdiproject.harvest.soep.utils;

/**
 * Constants used for logging for SOEP harvester classes
 *
 * @author Fidan Limani
 */
public class SoepLoggingConstants
{
    public static final String SOEP_REMOTE_REPO = "https://github.com/paneldata/de.gerdiproject.harvest.soep-core";
    public static final String ORIGIN_MASTER = "refs/remotes/origin/master";

    public static final String DIR_EXISTS = " already exists";
    public static final String DIR_CREATED = " was created";
    public static final String DIR_NOT_CREATED = " was not created";

    public static final String SET_REPO_NAME = "Setting the repository name to <%s>";
    public static final String SET_REMOTE_REPO_URL = "Setting the remote repository URI to <%s>";

    public static final String REPO_EXISTS = "Repo <%s> exists.";
    public static final String INIT_REPO = "Initializing repository <%s>";
    public static final String CLONE_REPO = "Cloning remote repository from <%s>";

    public static final String REPO_BRANCH_UPDATE = "Repository updates from <%s> available?";
    public static final String UPDATES_AVAILABLE = "Updates available. Pulling changes: %n%s";
    public static final String UPDATE_COMPLETE = "Repository successfully updated.";
    public static final String LOCAL_REPO_UPDATED = "Local repository up to date.";
    public static final String UPDATE_LOCAL_REPO = "Updating local repository <%s>";

    public static final String REPO_MISSING_ERROR = "Repository does not exist. To be created next.";
    public static final String IO_EXCEPTION_ERROR = "IOException";
    public static final String GIT_API_EXCEPTION_ERROR = "GitAPIException";
}