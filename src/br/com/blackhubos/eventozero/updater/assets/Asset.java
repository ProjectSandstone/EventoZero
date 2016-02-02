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
package br.com.blackhubos.eventozero.updater.assets;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.simple.JSONObject;

import com.google.common.base.MoreObjects;

import br.com.blackhubos.eventozero.updater.assets.uploader.Uploader;
import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.parser.Parser;

/**
 * Classe que representa os arquivos da versão
 */
public class Asset implements Parser<JSONObject, Asset> {

    private final String url;
    private final String name;
    private final String downloadUrl;
    private final Date createdDate;
    private final Date updatedDate;
    private final long id;
    private final long size;
    private final long downloads;
    private final AssetState state;
    private final Optional<String> label;
    private final Optional<Uploader> uploader;

    public Asset() {
        this(null, null, null, null, null, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, null, null, null);
    }

    /**
     * Cria um novo Asset
     * @param url Url do asset (não o de download)
     * @param name Nome do asset
     * @param downloadUrl Url de Download do Asset
     * @param createdDate Data de criação do Asset
     * @param updatedDate Data de atualização do Asset
     * @param id Id do Asset
     * @param size Tamanho do arquivo do Asset
     * @param downloads Quantidade de Downloads
     * @param state Estado do Asset
     * @param label Rótulo do Asset
     * @param uploader Quem enviou o Asset
     */
    private Asset(String url, String name, String downloadUrl, Date createdDate, Date updatedDate, long id, long size, long downloads, AssetState state, Optional<String> label, Optional<Uploader> uploader) {
        this.url = url;
        this.name = name;
        this.downloadUrl = downloadUrl;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.id = id;
        this.size = size;
        this.downloads = downloads;
        this.state = state;
        this.label = label;
        this.uploader = uploader;
    }

    @SuppressWarnings("unchecked")
    public static Optional<Asset> parseJsonObject(JSONObject jsonObject, MultiTypeFormatter formatter) {

        String url = null;
        String name = null;
        String downloadUrl = null;

        Date createdDate = null;
        Date updatedDate = null;

        long id = Long.MIN_VALUE;
        long size = Long.MIN_VALUE;
        long downloads = Long.MIN_VALUE;

        AssetState state = null;

        Optional<String> label = Optional.empty();

        Optional<Uploader> uploader = Optional.empty();
        // Obtem todos valores do JSON
        for (Map.Entry entries : (Set<Map.Entry>) jsonObject.entrySet()) {

            Object key = entries.getKey();
            Object value = entries.getValue();
            String valueString = String.valueOf(value);

            switch (AssetInput.parseObject(key)) {
                case URL: {
                    // URL do Asset
                    url = valueString;
                    break;
                }
                case ID: {
                    // Id do Asset
                    id = Long.parseLong(valueString);
                    break;
                }
                case BROWSER_DOWNLOAD_URL: {
                    // Link de download
                    downloadUrl = valueString;
                    break;
                }
                case CREATED_AT: {
                    // Data de criação
                    if (formatter.canFormat(Date.class)) {
                        Optional<Date> dateResult = formatter.format(valueString, Date.class);
                        if (dateResult.isPresent()) {
                            createdDate = dateResult.get();
                        }
                    }
                    break;
                }
                case UPDATED_AT: {
                    // Data de atualização
                    if (formatter.canFormat(Date.class)) {
                        Optional<Date> dateResult = formatter.format(valueString, Date.class);
                        if (dateResult.isPresent()) {
                            updatedDate = dateResult.get();
                        }
                    }
                    break;
                }
                case NAME: {
                    // Nome
                    name = valueString;
                    break;
                }
                case DOWNLOAD_COUNT: {
                    // Quantidade de downloads
                    downloads = Long.parseLong(valueString);
                    break;
                }

                case LABEL: {
                    /** Rótulo (se houver, caso contrário, {@link Optional#absent()}  **/
                    if (value == null) {
                        label = Optional.empty();
                    } else {
                        label = Optional.of(valueString);
                    }
                    break;
                }

                case STATE: {
                    // Estado
                    state = AssetState.parseString(valueString);
                    break;
                }

                case SIZE: {
                    // Tamanho do arquivo (em bytes)
                    size = Long.parseLong(valueString);
                    break;
                }

                case UPLOADER: {
                    // Quem envou (traduzido externalmente)
                    uploader = Uploader.parseJsonObject((JSONObject) value, formatter);
                    break;
                }

                default: {
                }
            }
        }

        if (id == Long.MIN_VALUE) {
            // Retorna um optional de valor ausente se não for encontrado o Asset.
            return Optional.empty();
        }

        // Cria um novo Asset
        return Optional.of(new Asset(url, name, downloadUrl, createdDate, updatedDate, id, size, downloads, state, label, uploader));
    }

    @Override
    public Optional<Asset> parseObject(JSONObject object, MultiTypeFormatter formatter) {
        return parseJsonObject(object, formatter);
    }

    /**
     * Obtem o URL do Asset
     * @return O URL do Asset
     */
    public String getUrl() {
        return url;
    }

    /**
     * Obtem o nome do Asset
     * @return O nome do Asset
     */
    public String getName() {
        return name;
    }

    /**
     * Obtem o endereço de download
     * @return O endereço de download
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Retorna a data de criação
     * @return A data de criação
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Retorna a data de atualização
     * @return A data de atualização
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Retorna o ID
     * @return O ID
     */
    public long getId() {
        return id;
    }

    /**
     * Retorna o tamanho do arquivo em bytes
     * @return O tamanho do arquivo em bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Retorna a quantidade de downloads
     * @return A quantidade de downloads
     */
    public long getDownloads() {
        return downloads;
    }

    /**
     * Retorna o Estado
     * @return O Estado
     */
    public AssetState getState() {
        return state;
    }

    /**
     * Retorna o rótulo, se existir, caso contrário {@link Optional#empty()}
     * @return O rótulo, se existir, caso contrário {@link Optional#empty()}
     */
    public Optional<String> getLabel() {
        return label;
    }

    /**
     * Retorna quem enviou, se existir, caso contrário {@link Optional#empty()}
     * @return Quem enviou, se existir, caso contrário {@link Optional#empty()}
     */
    public Optional<Uploader> getUploader() {
        return uploader;
    }

    /**
     * Retorna um texto representando o Objeto
     * @return Um texto representando o Objeto
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", this.url)
                .add("name", this.name)
                .add("downloadUrl", this.downloadUrl)
                .add("createdDate", this.createdDate)
                .add("updatedDate", this.updatedDate)
                .add("id", this.id)
                .add("size", this.size)
                .add("downloads", this.downloads)
                .add("state", this.state)
                .add("label", this.label)
                .add("uploader", this.uploader)
                .toString();
    }

    enum AssetInput {
        UNKNOWN, URL, ID, NAME, LABEL, UPLOADER, STATE, SIZE,
        DOWNLOAD_COUNT, CREATED_AT, UPDATED_AT, BROWSER_DOWNLOAD_URL;

        public static AssetInput parseObject(Object objectToParse) {
            if (objectToParse instanceof String) {
                String stringToParse = (String) objectToParse;
                try {
                    return AssetInput.valueOf(stringToParse.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            return AssetInput.UNKNOWN;
        }

    }

    public enum AssetState {
        NEW,
        UPLOADED,
        UNKNOWN;

        public static AssetState parseString(String string) {
            try {
                return AssetState.valueOf(string.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }

            return UNKNOWN;
        }
    }

}
