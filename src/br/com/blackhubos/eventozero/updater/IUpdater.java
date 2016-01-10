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
import java.util.Optional;
import java.util.function.Supplier;

public interface IUpdater {
    /**
     * Verifica as atualizações mostrando mensagens de informação Obs: Este método roda
     * automaticamente em um Thread caso não esteja em um
     */
    void updateCheck();

    /**
     * Inicia a atualização, se o cache estiver vazio ele verifica as atualizações automaticamente
     *
     * Obs: Caso o cache não seja nulo, este método irá baixar as atualizações enquanto verifica se
     * há versões mais recentes, se houver, cancela o download atual e inicia um novo com o cache
     * atualizado!
     */
    void startUpdate();

    /**
     * Aplica as atualizações (utilize somente caso não saiba qual o arquivo de atualização)
     */
    void applyUpdate();

    /**
     * Aplica a atualização e move o arquivo antigo para o diretório de versões antigas para que
     * possa ser restaurado caso ocorra algum erro
     *
     * @param newFile Destino do novo arquivo
     */
    void applyUpdate(File newFile);

    default void applyUpdate(Supplier<File> fileSupplier) {
        this.applyUpdate(fileSupplier.get());
    }

    /**
     * Aplica atualizações inseguras (utilize somente caso não saiba qual o arquivo de atualização)
     */
    void applyUnsecureUpdate();

    /**
     * Aplica atualizações inseguras
     *
     * @param newFile Arquivo de atualização
     */
    void applyUnsecureUpdate(File newFile);

    /**
     * Quando chamado irá limpar todos arquivos antigos de atualizações
     */
    void updateSuccess();

    /**
     * Para todos threads criados somente pelo Update (os demais, como do Downloader não são
     * cancelados)
     */
    void stopAll();

    /**
     * Obtem o cache que contem a atualização atual encontrada
     *
     * @return O cache que contem a atualização atual encontrada
     */
    Optional<UpdaterCache> getUpdaterCache();
}
