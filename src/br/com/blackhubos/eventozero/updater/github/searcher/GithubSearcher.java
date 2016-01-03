/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright © 2016 BlackHub OS and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package br.com.blackhubos.eventozero.updater.github.searcher;

import com.google.common.base.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.assets.Asset;
import br.com.blackhubos.eventozero.updater.formater.BooleanFormatter;
import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.github.formatter.GitHubDateFormatter;
import br.com.blackhubos.eventozero.updater.searcher.Searcher;
import br.com.blackhubos.eventozero.updater.assets.versions.Version;

public class GithubSearcher implements Searcher {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/BlackHubOS/EventoZero/";
    private static final String RELEASES_PATH = "releases";
    //2016-01-03T00:58:34Z

    @Override
    public Version getLastestVersion() {
        return null;
    }

    @Override
    public Version getRollbackVersion() {
        return null;
    }

    @Override
    public Collection<Version> getAllVersion() {
        return null;
    }

    @Override
    public Optional<Version> findVersion(String tag) {
        return null;
    }


    private Optional<Collection<Version>> connect(Optional<String> additionalUrl) throws IOException, ParseException, java.text.ParseException {

        MultiTypeFormatter multiFormatter = new MultiTypeFormatter();
        BooleanFormatter booleanFormatter = new BooleanFormatter();
        GitHubDateFormatter dateFormatter = new GitHubDateFormatter();

        multiFormatter.registerFormatter(booleanFormatter);
        multiFormatter.registerFormatter(dateFormatter);

        Collection<Version> versionList = new LinkedList<>();

        URL gitHubUrl = new URL(GITHUB_API_URL+RELEASES_PATH+(additionalUrl.isPresent() ? additionalUrl.get() : ""));

        URLConnection urlConnection = gitHubUrl.openConnection();

        InputStream inputStream = urlConnection.getInputStream();

        JSONParser parser = new JSONParser();
        Object array = parser.parse(new InputStreamReader(inputStream));
        if(array instanceof JSONArray){
            JSONArray jsonArray = (JSONArray) array;
            for (Object jsonObj : jsonArray) {
                if(jsonObj instanceof JSONObject){
                    processJsonObject((JSONObject) jsonObj, multiFormatter, versionList);
                }
            }
        }else{
            processJsonObject((JSONObject) array, multiFormatter, versionList);
        }

        return (versionList != null && !versionList.isEmpty() ? Optional.of(versionList) : Optional.<Collection<Version>>absent());
    }

    private void processJsonObject(JSONObject jobject, MultiTypeFormatter formatter, Collection<Version> versionList) {
        JSONObject jsonObject = jobject;
        /**
         * Variaveis do {@link Version}
         */
        String name = null;
        String version = null;
        Collection<Asset> downloadUrl = new ArrayList<>();
        String commitish = null;
        String changelog = null;
        Date creationDate = null;
        Date publishDate = null;
        long id = Long.MIN_VALUE;
        boolean criticalBug = false;
        boolean preRelease = false;
        /**
         * /Variaveis do {@link Version}
         */

        for(Map.Entry object : (Set<Map.Entry>) jsonObject.entrySet()){

            Object key = object.getKey();
            Object value = object.getValue();
            String stringValue = String.valueOf(value);
            switch (GitHubAPIInput.parseObject(key)) {
                case TAG_NAME: {
                    version = stringValue;
                    break;
                }

                case CREATED_AT: {
                    creationDate = formatter.format(stringValue, Date.class).get();
                    break;
                }

                case PUBLISHED_AT: {
                    publishDate = formatter.format(stringValue, Date.class).get();
                    break;
                }

                case ASSETS: {
                    JSONArray jsonArray = (JSONArray) value;

                    for(Object assetsJsonObject : jsonArray){
                        JSONObject jsonAsset = (JSONObject) assetsJsonObject;
                        Optional<Asset> assetOptional = Asset.parseJsonObject(jsonAsset, formatter);
                        if(assetOptional.isPresent()){
                            downloadUrl.add(assetOptional.get());
                        }
                    }
                    break;
                }

                case NAME: {
                    name = stringValue;
                    break;
                }

                case ID: {
                    id = Long.parseLong(stringValue);
                    break;
                }

                case BODY: {
                    changelog = stringValue;

                    criticalBug = changelog.endsWith("!!!CRITICAL BUG FOUND!!!")
                            || changelog.endsWith("CRITICAL BUG FOUND")
                            || changelog.endsWith("CRITICAL BUG");
                    break;
                }

                case PRERELEASE: {
                    Optional<Boolean> booleanOptional = formatter.format(value, Boolean.class);
                    if(!booleanOptional.isPresent()){
                        preRelease = false;
                        break;
                    }
                    preRelease = booleanOptional.get();
                    break;
                }

                case TARGET_COMMITISH: {
                    commitish = stringValue;
                    break;
                }
                default:{
                    break;
                }
            }
        }
        if(id != Long.MIN_VALUE) {
            Version versionInstance = new Version(name, version, downloadUrl, commitish, changelog, creationDate, publishDate, id, criticalBug, preRelease);
            versionList.add(versionInstance);
        }
    }

    enum GitHubAPIInput {
        UNKNOWN, TAG_NAME, AUTHOR, CREATED_AT, BODY, URL,
        ASSETS_URL, ASSETS, PRERELEASE, HTM_URL, TARGET_COMMITISH,
        DRAFT, ZIPBALL_URL, NAME, UPLOAD_URL, ID, PUBLISHED_AT,
        TARBALL_URL;

        public static GitHubAPIInput parseObject (Object objectToParse) {
            if(objectToParse instanceof String){
                String stringToParse = (String) objectToParse;
                try{
                    return GitHubAPIInput.valueOf(stringToParse.toUpperCase());
                }catch(IllegalArgumentException ignored){}
            }

            return GitHubAPIInput.UNKNOWN;
        }

    }

    /** Test METHOD **/
    /*$ if(is preprocessor){ removeMethod }**/
    public static void main(String[] args) {
        GithubSearcher githubSearcher = new GithubSearcher();
        try {
            Optional<Collection<Version>> optionals = githubSearcher.connect(Optional.of("/latest"));
            Collection<Version> versions = optionals.get();
            for(Version version : versions) {
                System.out.println("ID: "+version.getId());
                System.out.println("Changelog: "+version.getChangelog());

                for(Asset asset : version.getAssets()){
                    System.out.println("Asset url: "+asset.getDownloadUrl());
                }
                System.out.println("=======================================");
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
    }

}
