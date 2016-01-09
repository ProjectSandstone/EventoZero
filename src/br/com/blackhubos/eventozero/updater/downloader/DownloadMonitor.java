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
package br.com.blackhubos.eventozero.updater.downloader;

/**
 * Interface do monitor de progresso do download
 *
 * @see Downloader
 */
public interface DownloadMonitor {
    /**
     * Método chamado pelo {@link Downloader} quando houver atualização no progresso (método não
     * exato, necessita de verificação no progresso)
     *
     * @param totalDownloaded Total baixado do arquivo
     * @param fileSize        Tamanho total do arquivo
     * @see br.com.blackhubos.eventozero.updater.DefaultDownloadMonitor
     */
    void progressChange(long totalDownloaded, long fileSize);

    /**
     * Método chamando quando o download terminar
     */
    void downloadFinish();

    /**
     * Retorna o invervalo entre as verificações de atualização
     *
     * @return Intervalo em MILLISEGUNDOS
     * @see java.util.concurrent.TimeUnit#MILLISECONDS
     */
    long getInterval();
}
