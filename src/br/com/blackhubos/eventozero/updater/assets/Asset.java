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

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.json.simple.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.assets.uploader.Uploader;
import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.parser.Parser;

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

        Optional<String> label = Optional.absent();

        Optional<Uploader> uploader = Optional.absent();

        for (Map.Entry entries : (Set<Map.Entry>) jsonObject.entrySet()) {

            Object key = entries.getKey();
            Object value = entries.getValue();
            String valueString = String.valueOf(value);

            switch (AssetInput.parseObject(key)) {
                case URL: {
                    url = valueString;
                    break;
                }
                case ID: {
                    id = Long.parseLong(valueString);
                    break;
                }
                case BROWSER_DOWNLOAD_URL: {
                    downloadUrl = valueString;
                    break;
                }
                case CREATED_AT: {
                    if (formatter.canFormat(Date.class)) {
                        Optional<Date> dateResult = formatter.format(valueString, Date.class);
                        if (dateResult.isPresent()) {
                            createdDate = dateResult.get();
                        }
                    }
                    break;
                }
                case UPDATED_AT: {
                    if (formatter.canFormat(Date.class)) {
                        Optional<Date> dateResult = formatter.format(valueString, Date.class);
                        if (dateResult.isPresent()) {
                            updatedDate = dateResult.get();
                        }
                    }
                    break;
                }
                case NAME: {
                    name = valueString;
                    break;
                }
                case DOWNLOAD_COUNT: {
                    downloads = Long.parseLong(valueString);
                    break;
                }

                case LABEL: {
                    if (value == null) {
                        label = Optional.absent();
                    } else {
                        label = Optional.of(valueString);
                    }
                    break;
                }

                case STATE: {
                    state = AssetState.parseString(valueString);
                    break;
                }

                case SIZE: {
                    size = Long.parseLong(valueString);
                    break;
                }

                case UPLOADER: {
                    uploader = Uploader.parseJsonObject((JSONObject) value, formatter);
                    break;
                }

                default: {
                }
            }
        }

        if (id == Long.MIN_VALUE) {
            return Optional.absent();
        }

        return Optional.of(new Asset(url, name, downloadUrl, createdDate, updatedDate, id, size, downloads, state, label, uploader));
    }

    @Override
    public Optional<Asset> parseObject(JSONObject object, MultiTypeFormatter formatter) {
        return parseJsonObject(object, formatter);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public long getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public long getDownloads() {
        return downloads;
    }

    public AssetState getState() {
        return state;
    }

    public Optional<String> getLabel() {
        return label;
    }

    public Optional<Uploader> getUploader() {
        return uploader;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
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
