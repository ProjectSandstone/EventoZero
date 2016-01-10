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
package br.com.blackhubos.eventozero.updater.searcher;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

import br.com.blackhubos.eventozero.updater.assets.versions.Version;

public interface Searcher {

    /**
     * Buscar a ultima versão
     *
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    Optional<Version> getLatestVersion();

    /**
     * Buscar a ultima versão para uma versão especifica do Minecraft
     *
     * @param mcVersion Versão do minecraft
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    Optional<Version> getLatestVersionFor(String mcVersion);

    /**
     * Buscar a ultima versão para uma versão especifica do Minecraft
     *
     * @param mcVersion Versão do minecraft
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    default Optional<Version> getLatestVersionFor(Supplier<String> mcVersion) {
        return getLatestVersionFor(mcVersion.get());
    }

    /**
     * Buscar a ultima versão estável
     *
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    Optional<Version> getLatestStableVersion();

    /**
     * Buscar a ultima versão estável para uma versão especifica do Minecraft
     *
     * @param mcVersion Versão do Minecraft
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    Optional<Version> getLatestStableVersionFor(String mcVersion);

    /**
     * Buscar a ultima versão estável para uma versão especifica do Minecraft
     *
     * @param mcVersion Versão do Minecraft
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    default Optional<Version> getLatestStableVersionFor(Supplier<String> mcVersion) {
        return this.getLatestStableVersionFor(mcVersion.get());
    }

    /**
     * Buscar todas versões
     *
     * @return Coleção de versões, ou lista vazia se não encontrar nenhuma
     */
    Collection<Version> getAllVersion();

    /**
     * Encontrar versão especifica
     *
     * @param tag Nome da versão (tag)
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    Optional<Version> findVersion(String tag);

    /**
     * Encontrar versão especifica
     *
     * @param tag Nome da versão (tag)
     * @return Versão ou {@link Optional#empty()} se não encontrar nenhuma
     */
    default Optional<Version> findVersion(Supplier<String> tag) {
        return this.findVersion(tag.get());
    }

    /**
     * Retorna o link onde as releases estão hospedadas
     *
     * @return O link onde as releases estão hospedadas
     */
    String getReleasesUrl();
}
