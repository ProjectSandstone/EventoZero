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

import java.util.Collection;
import java.util.Date;

import br.com.blackhubos.eventozero.updater.assets.Asset;

public class Version {

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

    public String getVersion() {
        return version;
    }

    public Collection<Asset> getAssets() {
        return assets;
    }

    public String getCommitish() {
        return commitish;
    }

    public String getChangelog() {
        return changelog;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public long getId() {
        return id;
    }

    public boolean isCriticalBug() {
        return criticalBug;
    }

    public boolean isPreRelease() {
        return preRelease;
    }

    public String getName() {
        return name;
    }
}
