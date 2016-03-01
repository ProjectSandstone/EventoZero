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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import br.com.blackhubos.eventozero.updater.assets.Asset;
import br.com.blackhubos.eventozero.updater.assets.versions.Version;
import br.com.blackhubos.eventozero.updater.formater.BooleanFormatter;
import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.github.formatter.GitHubDateFormatter;
import br.com.blackhubos.eventozero.updater.searcher.Searcher;
import br.com.blackhubos.eventozero.util.ThreadUtils;

public class GitHubSearcher implements Searcher {

    private static final String GITHUB_RELEASES_URL = "https://github.com/BlackHubOS/EventoZero/releases";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/BlackHubOS/EventoZero/";
    private static final String RELEASES_PATH = "releases";
    private static final String LATEST_PATH = "/latest";
    private static final String TAG_PATH = "/tags";

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
        return Optional.empty();
    }

    @Override
    public Optional<Version> getLatestVersionFor(String mcVersion) {
        Objects.requireNonNull(mcVersion);
        // Faz um loop em todas versões
        for (Version version : getAllVersion()) {
            // Faz um loop em todas versões suportadas
            for (String supported : version.getSupportedVersions()) {
                // Verifica se a versão do minecraft inicia com a versão suportada
                if (mcVersion.startsWith(supported)) {
                    // Retorna a versão
                    return Optional.of(version);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Version> getLatestStableVersion() {

        try {
            // Conecta ao URL de todas versões
            Optional<Collection<Version>> versions = connect(Optional.empty());

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
        return Optional.empty();
    }

    @Override
    public Optional<Version> getLatestStableVersionFor(String mcVersion) {
        Objects.requireNonNull(mcVersion);
        // Faz um loop em todas versões
        for (Version version : getAllVersion()) {
            // Pula a versão se ela não for estável
            if (version.isCriticalBug() || version.isPreRelease()) continue;
            // Faz um loop em todas versões suportadas
            for (String supported : version.getSupportedVersions()) {
                // Verifica se a versão do minecraft inicia com a versão suportada
                if (mcVersion.startsWith(supported)) {
                    // Retorna a versão
                    return Optional.of(version);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Collection<Version> getAllVersion() {
        try {
            // Conecta ao URL de todas versões
            Optional<Collection<Version>> versions = connect(Optional.empty());

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
            Optional<Collection<Version>> versions = connect(Optional.of(TAG_PATH + "/" + tag));

            // Verifica se encontrou alguma (NullPointerException cade você?)
            if (versions.isPresent()) {
                // Retorna a primeira (que será a unica neste caso)
                Iterator<Version> versionIterator = versions.get().iterator();
                return Optional.of(versionIterator.next());
            }
        } catch (IOException | ParseException | java.text.ParseException ignored) {
        }
        // Retorna uma lista vazia caso não encontre a versão informada
        return Optional.empty();
    }

    @Override
    public String getReleasesUrl() {
        return GITHUB_RELEASES_URL;
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

        return (!versionList.isEmpty() ? Optional.of(versionList) : Optional.empty());
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
        List<String> supportedVersions = new ArrayList<>();
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

                    // Regex para obter a linha que diz as versões suportadas
                    Pattern supportedPattern = Pattern.compile("^(Versões|Supported)", Pattern.CASE_INSENSITIVE);

                    // Faz loop nas linhas
                    for (String line : changelog.split("\n")) {
                        // Procura o regex na linha
                        if (supportedPattern.matcher(line).find()) {
                            // Remove as letras
                            line = line.replaceAll("[^\\d. ]+", "").trim();
                            // Adiciona a lista
                            supportedVersions.addAll(Arrays.asList(line.split(" ")));
                        }
                    }


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
            Version versionInstance = new Version(name, version, supportedVersions, downloadUrl, commitish, changelog, creationDate, publishDate, id, criticalBug, preRelease);
            versionList.add(versionInstance);
        }
    }

    /**
     * Neste ENUM estão os valores que iremos obter, no momento são somente estes (nem sei porque
     * tanto)
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
