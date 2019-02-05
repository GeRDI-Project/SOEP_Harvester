/**
 * Copyright © 2019 Fidan Limani (http://www.gerdi-project.de)
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
package de.gerdiproject.harvest.github.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * This class represents a JSON object of a GitHub commits request.<br>
 * e.g. https://api.github.com/repos/paneldata/soep-core/commits
 * @author Robin Weiss
 *
 */
@Data
public class GitHubCommit
{
    private String sha;
    private String url;
    private GitHubCommitDetails commit;
    private GitHubUser author;
    private GitHubUser committer;
    private List<GitHubCommit> parents;

    @SerializedName("node_id")
    private String nodeId;

    @SerializedName("html_url")
    private String htmlUrl;

    @SerializedName("comments_url")
    private String commentsUrl;

}
