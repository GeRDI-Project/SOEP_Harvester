/*
 * Copyright © 2017 Fidan Limani (http://www.gerdi-project.de)
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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * This JSON object is part of a GitHub API response.
 * It contains details about a GitHub commit.
 *
 * @author Robin Weiss
 */
@Data
public class GitHubCommitDetails
{
    private String url;
    private String message;

    @SerializedName("comment_count")
    private int commentCount;

    private GitHubCommitter author;
    private GitHubCommitter committer;

    // seemingly unimportant information for now:
    private JsonObject verification;
    private JsonObject tree;
}