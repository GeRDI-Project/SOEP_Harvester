/*
 *  Copyright © 2019 Robin Weiss (http://www.gerdi-project.de/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package de.gerdiproject.harvest.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

import javax.ws.rs.core.MediaType;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import de.gerdiproject.harvest.utils.data.HttpRequester;
import de.gerdiproject.harvest.utils.data.HttpRequesterUtils;
import de.gerdiproject.harvest.utils.data.enums.RestRequestType;
import de.gerdiproject.harvest.utils.file.FileUtils;

/**
 * This class extends the {@linkplain HttpRequester} by
 * the option to parse a CSV file from web or from disk,
 * depending on the set parameters.
 *
 * @author Robin Weiss
 */
public class CsvRequester extends HttpRequester
{
    /**
     * Iterates through the rows of a CSV file that is loaded from a specified URL.
     *
     * @param url a URL that points to a CSV file
     * @param iterFunction a consumer function that accepts each row of the CSV file
     *
     * @throws IOException if there is an error reading the CSV file
     */
    public void parseCsv(final String url, final Consumer<String[]> iterFunction) throws IOException
    {
        // open a stream to a CSV file
        final InputStream csvStream;

        // is the response read from the disk cache?
        if (isReadingFromDisk()) {
            final File csvFile = HttpRequesterUtils.urlToFilePath(url, getCacheFolder());
            csvStream = Files.newInputStream(csvFile.toPath());

        } else {
            if (isWritingToDisk())
                cacheCsvStream(url);

            final HttpURLConnection csvConnection =
                webDataRetriever.sendWebRequest(
                    RestRequestType.GET,
                    url,
                    null, null, MediaType.TEXT_PLAIN, 0);
            csvStream = webDataRetriever.getInputStream(csvConnection);
        }

        parseCsvStream(csvStream, iterFunction);
    }


    /**
     * Caches a CSV file response on disk.
     * @param url the URL of the CSV file
     * @throws IOException if there is an error reading or writing the CSV file
     */
    private void cacheCsvStream(final String url) throws IOException
    {
        // oipen connection to the web
        final HttpURLConnection csvConnection =
            webDataRetriever.sendWebRequest(
                RestRequestType.GET,
                url,
                null, null, MediaType.TEXT_PLAIN, 0);

        // open stream to the cache file
        final File csvFile = HttpRequesterUtils.urlToFilePath(url, getCacheFolder());
        FileUtils.createEmptyFile(csvFile);

        try
            (InputStream csvInput = webDataRetriever.getInputStream(csvConnection);
             InputStreamReader inputStreamReader = new InputStreamReader(csvInput, StandardCharsets.UTF_8);
             BufferedReader webReader = new BufferedReader(inputStreamReader);
             BufferedWriter diskWriter = FileUtils.getWriter(csvFile, diskIO.getCharset())) {

            boolean hasContent = false;

            while (webReader.ready()) {
                if (hasContent)
                    diskWriter.newLine();

                diskWriter.append(webReader.readLine());
                hasContent = true;
            }
        }
    }


    /**
     * Iterates through the rows of a CSV file that is parsed from a specified {@linkplain InputStream}.
     *
     * @param csvInput an {@linkplain InputStream} of the CSV file that is to be parsed
     * @param iterFunction a consumer function that accepts each row of the CSV file
     *
     * @throws IOException if there is an error reading the CSV file
     */
    private void parseCsvStream(final InputStream csvInput, final Consumer<String[]> iterFunction) throws IOException
    {
        try
            (InputStreamReader inputStreamReader = new InputStreamReader(csvInput, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             CSVReader csvReader = new CSVReaderBuilder(bufferedReader).withSkipLines(1).build()) {

            while (true) {
                final String[] row = csvReader.readNext();

                if (row == null)
                    break;
                else
                    iterFunction.accept(row);
            }
        }
    }
}
