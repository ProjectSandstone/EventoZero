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
package br.com.blackhubos.eventozero.updater.assets.versions;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.com.blackhubos.eventozero.updater.assets.Asset;

public class Version implements Comparable<Version> {

    private final String name;
    private final String version;
    private final String commitish;
    private final String changelog;
    private final Date creationDate;
    private final Date publishDate;

    private final Collection<Asset> assets;

    private final long id;
    private final boolean criticalBug;
    private final boolean preRelease;

    public Version(String name, String version, Collection<Asset> assets, String commitish, String changelog, Date creationDate, Date publishDate, long id, boolean criticalBug, boolean preRelease) {
        this.name = name;
        this.version = version;
        this.assets = assets;
        this.commitish = commitish;
        this.changelog = changelog;
        this.creationDate = creationDate;
        this.publishDate = publishDate;
        this.id = id;
        this.criticalBug = criticalBug;
        this.preRelease = preRelease;
    }

    public static List<Version> sortVersions(Collection<Version> collection) {
        return sortVersions(collection, false);
    }

    public static List<Version> sortVersions(Collection<Version> collection, boolean reverse) {
        LinkedList<Version> linkedList = new LinkedList<>(collection);
        Collections.sort(linkedList);
        if (reverse)
            Collections.reverse(linkedList);

        return linkedList;
    }

    /**
     * Retorna a versão
     * @return A versão
     */
    public String getVersion() {
        return version;
    }

    /**
     * Retorna os Assets
     * @return Uma coleção com os Assets
     */
    public Collection<Asset> getAssets() {
        return assets;
    }

    /**
     * Retorna o Commit ou a Branch da versão
     * @return Commit ou a Branch da versão
     */
    public String getCommitish() {
        return commitish;
    }

    /**
     * Retorna o ChangeLog/Descrição
     * @return O ChangeLog/Descrição
     */
    public String getChangelog() {
        return changelog;
    }

    /**
     * Retorna a data de criação
     * @return A data de criação
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Retorna a data de publicação
     * @return A data de publicação
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * Retorna o id da versão
     * @return O id da versão
     */
    public long getId() {
        return id;
    }

    /**
     * Determina se há um bug critico
     * @return Se há um bug critico
     */
    public boolean isCriticalBug() {
        return criticalBug;
    }

    /**
     * Determina se é uma pre-release (alpha, beta, etc)
     * @return Se é uma pre-release (alpha, beta, etc)
     */
    public boolean isPreRelease() {
        return preRelease;
    }

    /**
     * Retorna o nome/titulo da versão
     * @return O nome/titulo da versão
     */
    public String getName() {
        return name;
    }

    /**
     * Transforma o objeto em String
     * @return Representação em String do Objeto
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", this.name)
                .add("version", this.version)
                .add("assets", this.assets)
                .add("commitish", this.commitish)
                .add("changelog", this.changelog)
                .add("creationDate", this.creationDate)
                .add("publishDate", this.publishDate)
                .add("id", this.id)
                .add("criticalBug", this.criticalBug)
                .add("preRelease", this.preRelease)
                .toString();
    }

    /**
     * Compara as datas das versões
     * @param anotherVersion A outra para comprar
     * @return Comparação entre as datas de publicação das versões
     */
    @Override
    public int compareTo(Version anotherVersion) {
        return this.getPublishDate().compareTo(anotherVersion.getPublishDate());
    }

    /**
     * Determina se a versão atual é mais nova que a informada
     * @see #compareTo(Version)
     * @param anotherVersion Versão a comparar
     * @return True se a versão atual é mais recente que a informada, false caso seja igual ou mais antiga
     */
    public boolean isNewerThan(Version anotherVersion) {
        return compareTo(anotherVersion) > 0;
    }

    /**
     * Determina se a versão atual é mais antiga que a informada
     * @see #compareTo(Version)
     * @param anotherVersion Versão a comparar
     * @return True se a versão atual é mais antiga que a informada, false caso seja igual ou mais recente
     */
    public boolean isOlderThan(Version anotherVersion) {
        return compareTo(anotherVersion) < 0;
    }

    /**
     * Determina se a versão atual é a mesma que a informada
     * @see #compareTo(Version)
     * @param anotherVersion Versão a comparar
     * @return True se a versão atual é a mesma que a informada, false caso seja mais recente ou mais antiga
     */
    public boolean isSameVersion(Version anotherVersion) {
        return compareTo(anotherVersion) == 0;
    }

}
