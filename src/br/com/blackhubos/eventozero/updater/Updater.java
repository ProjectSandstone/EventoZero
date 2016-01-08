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

import com.google.common.base.Optional;

import java.util.logging.Logger;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.updater.assets.versions.Version;
import br.com.blackhubos.eventozero.updater.github.searcher.GitHubSearcher;
import br.com.blackhubos.eventozero.updater.searcher.Searcher;

/**
 * Classe que irá atualizar tudo
 */
public class Updater {
    private static final Searcher searcher = new GitHubSearcher();
    /**
     * Versão alternativa caso não encontre a informada no .YML
     */
    private static final String INTERNAL_VERSION = "1.0.1";
    private static final String updaterNamespace = "[Updater] ";

    private final EventoZero plugin;
    private final Optional<Version> current;
    private final Logger logger;

    private final boolean auto_install;
    private final boolean debug;
    private final boolean enable;

    public Updater(EventoZero plugin) {
        /** OBTER VALORES DA CONFIGURAÇÃO **/

        this.auto_install = true;
        this.debug = false;
        this.enable = true;
        /** FIM DOS VALORES DA CONFIGURAÇÃO **/

        this.plugin = plugin;
        this.logger = plugin.getLogger();

        String versionString = this.plugin.getDescription().getVersion();

        logger.info(String.format("%sProcurando a versão '%s'...", updaterNamespace, versionString));

        Optional<Version> versionOptional = searcher.findVersion(versionString);
        current = versionOptional;
        if (!versionOptional.isPresent()) {
            versionOptional = searcher.findVersion(INTERNAL_VERSION);
        }


        if (versionOptional.isPresent()) {
            logger.info(String.format("%sVersão econtrada.", updaterNamespace));
        } else {
            logger.warning(String.format("%sNão foi possivel encontrar a versão '%s'...", updaterNamespace, versionString));
        }


    }

    public void initCheck() {
        // TODO: Verificar versões antigas e atualizar
        if (!current.isPresent()) {
            logger.info(String.format("%sNão foi possivel verificar as versões (Versão atual: %s)!", updaterNamespace, plugin.getDescription().getVersion()));
            if (debug) {
                logger.info(String.format("%sVocê pode encontrar todas versões no link a seguir: '%s'!", updaterNamespace, searcher.getReleasesUrl()));
            }
            return;
        }

        Optional<Version> latest = searcher.getLatestStableVersion();

        if (latest.isPresent()) {
            Version latestVersion = latest.get();
            if (current.get().isSameVersion(latestVersion)) {
                logger.info(String.format("%sVocê está utilizando a versão mais recente do %s! Versão: %s",
                        updaterNamespace, plugin.getDescription().getName(), current.get().getVersion()));
            } else if (current.get().isNewerThan(latestVersion)) {
                if (debug) {
                    logger.warning(String.format("%sOps! Sua versão é mais recente que a ultima disponivel! ", updaterNamespace));
                    logger.warning(String.format("%sCaso você esteja desenvolvendo uma nova versão ou compilado a partir do repositório, ignore este erro. ", updaterNamespace));
                    logger.warning(String.format("%sCaso contrário, por favor reporte este erro para nossa equipe!", updaterNamespace));
                    logger.warning(String.format("%sEnvie a seguinte mensagem para nosta equipe ao reportar o erro:", updaterNamespace));
                    logger.warning(String.format("%sVersão atual: %s. Ultima versão encontrada: %s", updaterNamespace, current.get().toString(), latestVersion.toString()));
                }
            }
        }


    }
}
