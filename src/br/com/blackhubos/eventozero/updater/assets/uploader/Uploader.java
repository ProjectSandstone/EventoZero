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
package br.com.blackhubos.eventozero.updater.assets.uploader;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.parser.Parser;

/**
 * Classe que representa quem enviou o arquivo
 */
public class Uploader implements Parser<JSONObject, Uploader> {

    private final String name;
    private final boolean admin;
    private final long id;

    public Uploader() {
        this(null, false, Long.MIN_VALUE);
    }

    private Uploader(String name, boolean admin, long id) {
        this.name = name;
        this.admin = admin;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public static Optional<Uploader> parseJsonObject(JSONObject parse, MultiTypeFormatter formatter) {

        long id = Long.MIN_VALUE;
        String name = null;
        boolean admin = false;
        // Loop em todas entradas do JSON
        for (Map.Entry entries : (Set<Map.Entry>) parse.entrySet()) {

            Object key = entries.getKey();
            Object value = entries.getValue();
            String valueString = String.valueOf(value);
            /** Transforma o objeto em um {@link AssetUploaderInput) para usar com switch **/
            switch (AssetUploaderInput.parseObject(key)) {
                case ADMIN: {
                    // Obtem o valor que indica se quem enviou era administrador
                    if (formatter.canFormat(Boolean.class)) {
                        Optional<Boolean> result = formatter.format(value, Boolean.class);
                        if (result.isPresent())
                            admin = result.get();
                    }
                    break;
                }
                case ID: {
                    // Obtém o ID do usuário
                    id = Long.parseLong(valueString);
                    break;
                }
                case LOGIN: {
                    // Obtém o nome/login do usuário
                    name = valueString;
                    break;
                }

                default: {
                    break;
                }
            }
        }
        if (id == Long.MIN_VALUE || name == null) {
            return Optional.absent();
        }
        return Optional.of(new Uploader(name, admin, id));
    }

    /**
     * Obter o nome do usuário
     * @return Nome do usuário
     */
    public String getName() {
        return name;
    }

    /**
     * Obter o id do usuário
     * @return Id do usuário
     */
    public long getId() {
        return id;
    }

    /**
     * Verifica se ele é administrador
     * @return Se ele é administrador -> True
     * <br>
     * Caso contrário -> False
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Transforma em uma String
     * @return String representando todos valores do Uploader
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", this.name)
                .add("id", this.id)
                .add("admin", this.admin)
                .toString();
    }


    @Override
    public Optional<Uploader> parseObject(JSONObject object, MultiTypeFormatter formatter) {
        return parseJsonObject(object, formatter);
    }

    enum AssetUploaderInput {
        UNKNOWN, LOGIN, ID, ADMIN;

        public static AssetUploaderInput parseObject(Object objectToParse) {
            if (objectToParse instanceof String) {
                String stringToParse = (String) objectToParse;
                try {
                    return AssetUploaderInput.valueOf(stringToParse.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            return AssetUploaderInput.UNKNOWN;
        }

    }


}