/**
 * Copyright © 2019 ${owner} (http://www.gerdi-project.de)
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

import lombok.Data;

/**
 * This class represents a JSON object as part of a GitHub API
 * response. It contains a little information about a committer.
 *
 * @author Robin Weiss
 */
@Data
public class GitHubCommitter
{
    private String name;
    private String email;
    private String date;
}