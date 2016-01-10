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

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

import br.com.blackhubos.eventozero.updater.downloader.DownloadMonitor;

final class DefaultDownloadMonitor implements DownloadMonitor {

    private static final int INTERVAL = 1;

    private final Consumer<File> callBack;
    private final File callbackFile;
    private final Logger logger;
    private final String namespace;
    private boolean forceStop = false;
    private double lastProgress = -1;

    DefaultDownloadMonitor(Consumer<File> callBack, File callbackFile, Logger logger, String namespace) {
        this.callBack = callBack;
        this.callbackFile = callbackFile;
        this.logger = logger;
        this.namespace = namespace;
    }

    @Override
    public void progressChange(final long totalDowloaded, final long fileSize) {
        if (forceStop) return;
        print(totalDowloaded, fileSize);

    }

    private void print(final long totalDowloaded, final long fileSize) {

        double progress = ((double) totalDowloaded / (double) fileSize) * 100D;

        if (lastProgress != progress) {
            lastProgress = progress;
        } else {
            return;
        }

        logger.info(String.format("%sProgresso da atualização: %.2f%%", namespace, progress));
    }

    @Override
    public void downloadFinish() {
        forceStop = true;
        print(100, 100);
        this.callBack.accept(callbackFile);
    }

    @Override
    public long getInterval() {
        return TimeUnit.SECONDS.toMillis(INTERVAL);
    }
}
