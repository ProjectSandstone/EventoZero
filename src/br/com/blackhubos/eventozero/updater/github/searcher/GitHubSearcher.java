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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.assets.Asset;
import br.com.blackhubos.eventozero.updater.assets.versions.Version;
import br.com.blackhubos.eventozero.updater.formater.BooleanFormatter;
import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.github.formatter.GitHubDateFormatter;
import br.com.blackhubos.eventozero.updater.searcher.Searcher;

public class GitHubSearcher implements Searcher {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/BlackHubOS/EventoZero/";
    private static final String RELEASES_PATH = "releases";
    private static final String LATEST_PATH = "/latest";
    private static final String TAG_PATH = "/tags";

    /**
     * Test METHOD
     **/
    public static void main(String[] args) {
        GitHubSearcher gitHubSearcher = new GitHubSearcher();
        gitHubSearcher.getLatestStableVersion();
    }

    @Override
    public Optional<Version> getLatestVersion() {
        try {
            // Conecta ao URL de ultimas versões
            Optional<Collection<Version>> versions = connect(Optional.of(LATEST_PATH));
            // Verifica se há alguma
            if (versions.isPresent()) {
                // Retorna a primeira
                Iterator<Version> versionIterator = versions.get().iterator();
                return Optional.of(versionIterator.next());
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public Optional<Version> getLatestStableVersion() {

        try {
            // Conecta ao URL de todas versões
            Optional<Collection<Version>> versions = connect(Optional.<String>absent());

            // Verifica se encontrou alguma (NullPointerException jamais)
            if (versions.isPresent()) {
                // Obtem a lista e ordena ela
                Collection<Version> versionCollection = versions.get();
                List<Version> versionList = Version.sortVersions(versionCollection, true);

                // Faz um loop para verificar qual a ultima versão sem bug critico ou em pre-release.
                // Se a ultima versão disponivel estiver sem bug ela será retornada
                for (Version version : versionList) {
                    if (!version.isCriticalBug() && !version.isPreRelease()) {
                        return Optional.of(version);
                    }
                }
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public Collection<Version> getAllVersion() {
        try {
            // Conecta ao URL de todas versões
            Optional<Collection<Version>> versions = connect(Optional.<String>absent());

            // Verifica se encontrou alguma (NullPointerException não sentirei saudades)
            if (versions.isPresent()) {
                // Retorna a lista de versões ordenadas
                return Version.sortVersions(versions.get(), true);
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        // Retorna uma lista vazia caso tenhamos algum erro ao obter versão
        return Collections.emptyList();
    }

    @Override
    public Optional<Version> findVersion(String tag) {
        try {
            // Conecta ao URL de todas versões
            Optional<Collection<Version>> versions = connect(Optional.of(TAG_PATH+"/"+tag));

            // Verifica se encontrou alguma (NullPointerException cade você?)
            if (versions.isPresent()) {
                // Retorna a primeira (que será a unica neste caso)
                Iterator<Version> versionIterator = versions.get().iterator();
                return Optional.of(versionIterator.next());
            }
        } catch (IOException | ParseException | java.text.ParseException ignored) {
        }
        // Retorna uma lista vazia caso não encontre a versão informada
        return Optional.absent();
    }

    private Optional<Collection<Version>> connect(Optional<String> additionalUrl) throws IOException, ParseException, java.text.ParseException {
        // Formatadores de valor a partir de textos e objetos
        MultiTypeFormatter multiFormatter = new MultiTypeFormatter();
        BooleanFormatter booleanFormatter = new BooleanFormatter();
        GitHubDateFormatter dateFormatter = new GitHubDateFormatter();
        multiFormatter.registerFormatter(booleanFormatter);
        multiFormatter.registerFormatter(dateFormatter);

        // Coleção de todas as versões
        Collection<Version> versionList = new LinkedList<>();

        // Conexão e abertura de stream para obter o JSON
        URL gitHubUrl = new URL(GITHUB_API_URL + RELEASES_PATH + (additionalUrl.isPresent() ? additionalUrl.get() : ""));
        URLConnection urlConnection = gitHubUrl.openConnection();
        InputStream inputStream = urlConnection.getInputStream();

        // Parser do JSON
        JSONParser parser = new JSONParser();
        Object array = parser.parse(new InputStreamReader(inputStream));

        // Verifica se é uma array (ou seja, se tem varias versoes)
        if (array instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) array;

            // Faz loop em todos objetos da array
            for (Object jsonObj : jsonArray) {
                // Verifica um objeto (para evitar problemas de ClassCannotCastException)
                if (jsonObj instanceof JSONObject) {
                    // Processa a versão
                    processJsonObject((JSONObject) jsonObj, multiFormatter, versionList);
                }
            }
        } else {
            // Caso não seja várias versões processa somente 1
            processJsonObject((JSONObject) array, multiFormatter, versionList);
        }

        return (!versionList.isEmpty() ? Optional.of(versionList) : Optional.<Collection<Version>>absent());
    }

    @SuppressWarnings("unchecked")
    private void processJsonObject(JSONObject jobject, MultiTypeFormatter formatter, Collection<Version> versionList) {
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

        for (Map.Entry object : (Set<Map.Entry>) jobject.entrySet()) {

            Object key = object.getKey();
            Object value = object.getValue();
            String stringValue = String.valueOf(value);
            switch (GitHubAPIInput.parseObject(key)) {
                // Tag geralmente é a versão
                case TAG_NAME: {
                    version = stringValue;
                    break;
                }

                // Data de criação
                case CREATED_AT: {
                    creationDate = formatter.format(stringValue, Date.class).get();
                    break;
                }

                // Data de publicação
                case PUBLISHED_AT: {
                    publishDate = formatter.format(stringValue, Date.class).get();
                    break;
                }

                // Assets/Artefatos ou Arquivos (processado externamente)
                case ASSETS: {
                    // Array com multiplos artefatos
                    JSONArray jsonArray = (JSONArray) value;

                    for (Object assetsJsonObject : jsonArray) {
                        // Obtem o objeto a partir da array de artefatos
                        JSONObject jsonAsset = (JSONObject) assetsJsonObject;
                        // Obtém o artefato a partir do objeto
                        Optional<Asset> assetOptional = Asset.parseJsonObject(jsonAsset, formatter);
                        // É bom evitar um null né :P
                        if (assetOptional.isPresent()) {
                            // Adiciona o artefato caso ele seja encontrado
                            downloadUrl.add(assetOptional.get());
                        }
                    }
                    break;
                }

                // Obtem o nome (titulo) da versão
                case NAME: {
                    name = stringValue;
                    break;
                }

                // Numero de identificação do GitHub (nem sei se vamos usar)
                case ID: {
                    id = Long.parseLong(stringValue);
                    break;
                }

                // Obtém a mensagem, geralmente nosso changelog, e define se é uma versão de bug critico
                case BODY: {
                    changelog = stringValue;
                    // Define se é versão de bug critico
                    criticalBug = changelog.endsWith("!!!CRITICAL BUG FOUND!!!")
                            || changelog.endsWith("CRITICAL BUG FOUND")
                            || changelog.endsWith("CRITICAL BUG");
                    break;
                }

                // Formata a boolean e verifica se ela é uma pre-release (alpha, beta, etc)
                case PRERELEASE: {
                    Optional<Boolean> booleanOptional = formatter.format(value, Boolean.class);

                    // Evitar um nullinho :D
                    if (!booleanOptional.isPresent()) {
                        preRelease = false;
                        break;
                    }

                    preRelease = booleanOptional.get();
                    break;
                }

                // Commitish geralmente é a branch ou a Commit relacionada a versão
                case TARGET_COMMITISH: {
                    commitish = stringValue;
                    break;
                }

                default: {
                    break;
                }
            }
        }

        // Verifica se o ID é Diferente do valor minimo, isto vai fazer com que nós saibamos se alguma versão foi encontrada ou não :D
        if (id != Long.MIN_VALUE) {
            // Cria uma nova versão e adiciona a lista
            Version versionInstance = new Version(name, version, downloadUrl, commitish, changelog, creationDate, publishDate, id, criticalBug, preRelease);
            versionList.add(versionInstance);
        }
    }

    /**
     * Neste ENUM estão os valores que iremos obter, no momento são somente estes (nem sei porque tanto)
     */
    enum GitHubAPIInput {
        UNKNOWN, TAG_NAME, CREATED_AT, BODY,
        ASSETS, PRERELEASE, TARGET_COMMITISH,
        NAME, ID, PUBLISHED_AT;

        // Obter um dos valores acima a partir de um objeto
        public static GitHubAPIInput parseObject(Object objectToParse) {
            if (objectToParse instanceof String) {
                String stringToParse = (String) objectToParse;
                try {
                    return GitHubAPIInput.valueOf(stringToParse.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            return GitHubAPIInput.UNKNOWN;
        }

    }

}
