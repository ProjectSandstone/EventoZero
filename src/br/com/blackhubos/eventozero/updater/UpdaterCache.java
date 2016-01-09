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
package br.com.blackhubos.eventozero.updater;

import br.com.blackhubos.eventozero.updater.assets.versions.Version;

/**
 * Armazena os dados da atualização
 */
public class UpdaterCache implements Comparable<UpdaterCache> {

    private final boolean unsecureVersion;
    private final Version version;

    /**
     * Construtor dos dados do atualizador
     *
     * @param unsecureVersion Se a versão é insegura, toda versão que não tem compatibilidade
     *                        comprovada com a versão atual do minecraft ou quando a versão do
     *                        minecraft não é detectada ela será insegura
     * @param version         Instancia da versão
     */
    UpdaterCache(boolean unsecureVersion, Version version) {
        this.unsecureVersion = unsecureVersion;
        this.version = version;
    }

    /**
     * Retorna a instancia da versão
     *
     * @return a instancia da versão
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Determina se é versão insegura ou não
     *
     * @return se é versão insegura ou não
     */
    public boolean isUnsecureVersion() {
        return unsecureVersion;
    }

    @Override
    public int compareTo(UpdaterCache o) {
        return this.getVersion().compareTo(o.getVersion());
    }
}
