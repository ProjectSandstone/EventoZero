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

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.updater.assets.Asset;
import br.com.blackhubos.eventozero.updater.assets.versions.Version;
import br.com.blackhubos.eventozero.updater.downloader.Downloader;
import br.com.blackhubos.eventozero.updater.github.searcher.GitHubSearcher;
import br.com.blackhubos.eventozero.updater.searcher.Searcher;
import br.com.blackhubos.eventozero.util.Framework;
import br.com.blackhubos.eventozero.util.ThreadUtils;

/**
 * Classe que irá atualizar tudo Alguns processos são automaticos então leia a documentação com
 * muita atenção
 */
public class Updater implements CallBack<File> {
    private static final Searcher searcher = new GitHubSearcher();
    private static final File pluginFile = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    /**
     * Versão alternativa caso não encontre a informada no .YML
     */
    private static final String INTERNAL_VERSION = "1.0.1";
    private static final String updaterNamespace = "[Updater] ";

    private final EventoZero plugin;
    private final Logger logger;
    private final boolean auto_install;
    private final boolean debug;
    // Definição AutoCheck Updates
    private final boolean enable;
    private final File updates;
    private final File oldDir;
    private final AtomicReference<UpdaterCache> updaterCache = new AtomicReference<>();
    private Optional<Version> current;
    private Downloader downloader;
    private File downloadedFile = null;

    /**
     * Cria um novo Updater com a instancia do {@link EventoZero}
     *
     * Este método detecta automaticamente a versão atual (independente da definição do 'AutoCheck
     * Updates')
     *
     * Só não irá verificar as ATUALIZAÇÕES caso o 'AutoCheck Updates' seja 'FALSO', porém, a versão
     * ainda será buscada nos servidores
     *
     * @param plugin               Instancia do {@link EventoZero}
     * @param updaterConfiguration Configuração do Updater
     */
    public Updater(EventoZero plugin, FileConfiguration updaterConfiguration) {
        /** OBTER VALORES DA CONFIGURAÇÃO **/

        this.auto_install = Framework.getBoolean(updaterConfiguration.getString("auto_install"));
        this.debug = Framework.getBoolean(updaterConfiguration.getString("check.debug"));
        this.enable = Framework.getBoolean(updaterConfiguration.getString("check.enable"));
        this.updates = new File(plugin.getDataFolder(), updaterConfiguration.getString("save_directory"));
        this.oldDir = new File(updates, "olds/");
        if (!this.updates.exists()) {
            this.oldDir.mkdirs();
        }
        /** FIM DOS VALORES DA CONFIGURAÇÃO **/

        this.plugin = plugin;
        this.logger = plugin.getLogger();

        // Verifica se é o thread principal
        ThreadUtils.createNewThread(this, new Runnable() {
            @Override
            public void run() {
                String versionString = Updater.this.plugin.getDescription().getVersion();

                logger.info(String.format("%sProcurando a versão '%s'...", updaterNamespace, versionString));

                Optional<Version> versionOptional = searcher.findVersion(versionString);
                current = versionOptional;
                if (!versionOptional.isPresent()) {
                    versionOptional = searcher.findVersion(INTERNAL_VERSION);
                }


                if (versionOptional.isPresent()) {
                    logger.info(String.format("%sVersão encontrada.", updaterNamespace));
                } else {
                    logger.warning(String.format("%sNão foi possivel encontrar a versão '%s'...", updaterNamespace, versionString));
                }

                if (Updater.this.enable) {
                    logger.info(String.format("%sFazendo verificações automaticas, caso não queira isto desabilite na configuração.", updaterNamespace));
                    updateCheck();
                }
            }
        }).start();
    }

    /**
     * Verifica as atualizações mostrando mensagens de informação Obs: Este método roda
     * automaticamente em um Thread caso não esteja em um
     */
    public synchronized void updateCheck() {
        this.updateCheck(false);
    }

    /**
     * Verifica as atualizações mostrando mensagens de informação caso não seja somente atualização
     * de cache Obs: Este método roda automaticamente em um Thread caso não esteja em um
     *
     * @param cacheUpdateOnly Determina se é somente uma atualização de cache, se for, não mostrará
     *                        mensagens
     */
    private synchronized void updateCheck(boolean cacheUpdateOnly) {
        // Verifica se é o thread principal
        if (Bukkit.isPrimaryThread()) {
            /**
             * Cria um novo thread
             */
            ThreadUtils.createNewThread(this, new Runnable() {
                @Override
                public void run() {
                    updateCheck();
                }
            }).start();
            // Impede que seja rodado o código de atualização fora de um Thread (para não travar o Thread principal do servidor
            return;
        }

        // Verifica se a versão atual está presente (ela é verifica automaticamente no construtor)
        if (!current.isPresent()) {
            logger.info(String.format("%sNão foi possivel verificar as versões (Versão atual: %s)!", updaterNamespace, plugin.getDescription().getVersion()));
            if (debug) {
                logger.info(String.format("%sVocê pode encontrar todas versões no link a seguir: '%s'!", updaterNamespace, searcher.getReleasesUrl()));
            }
            return;
        }
        // Obtém a versão do minecraft
        String minecraftVersion;
        try {
            minecraftVersion = Framework.getMinecraftVersion();
        } catch (RuntimeException e) {
            e.printStackTrace();
            minecraftVersion = null;
        }

        // Se a versão não for nula irá procurar versões para a versão correspondente do Minecraft
        // Caso seja nulo irá fazer uma verificação de uma versão de funcionamento NÃO GARANTIDO
        Optional<Version> latest;
        if (minecraftVersion != null) {
            latest = searcher.getLatestStableVersionFor(minecraftVersion);
        } else {
            logger.warning(String.format("%sEstamos buscando atualização para a ultima versão do Minecraft, " +
                    "porém não será atualizado sem sua permissão pois não detectamos sua versão! ", updaterNamespace));
            latest = searcher.getLatestStableVersion();
        }

        if (latest.isPresent()) {
            Version latestVersion = latest.get();
            // Verifica se é a mesma versão
            if (current.get().isSameVersion(latestVersion)) {
                if (!cacheUpdateOnly) {
                    logger.info(String.format("%sVocê está utilizando a versão mais recente do %s! Versão: %s",
                            updaterNamespace, plugin.getDescription().getName(), current.get().getVersion()));
                }
                // Verifica se é uma versão mais nova
            } else if (current.get().isNewerThan(latestVersion)) {
                if (!cacheUpdateOnly && debug) {
                    logger.warning(String.format("%sOps! Sua versão é mais recente que a ultima disponivel! ", updaterNamespace));
                    logger.warning(String.format("%sCaso você esteja desenvolvendo uma nova versão ou compilado a partir do repositório, ignore este erro. ", updaterNamespace));
                    logger.warning(String.format("%sCaso contrário, por favor reporte este erro para nossa equipe!", updaterNamespace));
                    logger.warning(String.format("%sEnvie a seguinte mensagem para nosta equipe ao reportar o erro:", updaterNamespace));
                    logger.warning(String.format("%sVersão atual: %s. Ultima versão encontrada: %s", updaterNamespace, current.get().toString(), latestVersion.toString()));
                }
                //Verifica se é uma versão mais antiga
            } else if (current.get().isOlderThan(latestVersion)) {
                if (!cacheUpdateOnly) {
                    logger.info(String.format("%sNova versão encontrada: '%s'!", updaterNamespace, latestVersion.getVersion()));
                }
                boolean isUnsecure = false;
                if (minecraftVersion == null) {
                    if (!cacheUpdateOnly) {
                        logger.info(String.format("%sCompativel com as versões: '%s'!", updaterNamespace, latestVersion.getSupportedVersions().toString().replaceAll("\\[|\\]", "")));
                        logger.info(String.format("%sCaso queira atualizar digite: /ez update unsecure!", updaterNamespace));
                    }
                    isUnsecure = true;
                } else {
                    if (!cacheUpdateOnly) {
                        logger.info(String.format("%sCaso queira atualizar digite: /ez update!", updaterNamespace));
                    }
                }

                // Define o cache de versões
                setVersionCache(isUnsecure, latestVersion);
            }
        } else {
            logger.info(String.format("%sNenhuma versão recente encontrada!", updaterNamespace));
        }
    }

    /**
     * Inicia a atualização, se o cache estiver vazio ele verifica as atualizações automaticamente
     *
     * Obs: Caso o cache não seja nulo, este método irá baixar as atualizações enquanto verifica se
     * há versões mais recentes, se houver, cancela o download atual e inicia um novo com o cache
     * atualizado!
     */
    private void startUpdate() {

        if (updaterCache.get() == null) {
            updateCheck();
        }

        final UpdaterCache cache = this.updaterCache.get();

        ThreadUtils.createNewThread(this, "updater", new Runnable() {
            @Override
            public void run() {
                if (cache.getVersion().getAssets().size() > 0) {
                    Asset asset = cache.getVersion().getAssets().iterator().next();
                    try {
                        logger.info(String.format("%sAtualizando...!", updaterNamespace));
                        URL downloadUrl = new URL(asset.getDownloadUrl());
                        downloadedFile = new File(updates, asset.getName());
                        downloader = new Downloader(downloadUrl, 1, new DefaultDownloadMonitor(Updater.this, downloadedFile, logger, updaterNamespace));
                        downloader.download(updates, asset.getName());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        ThreadUtils.createNewThread(this, new Runnable() {
            @Override
            public void run() {
                // Inicia uma verificação somente de atualização de cache
                updateCheck(true);
                // Verifica se o resultado é mais recente que o atual
                if (cache.compareTo(updaterCache.get()) > 0) {
                    logger.info(String.format("%sNova versão detectada!", updaterNamespace));
                    Thread currentUpdate = ThreadUtils.getThread(Updater.this, "updater");
                    if (currentUpdate != null && currentUpdate.isAlive() && !currentUpdate.isInterrupted()) {
                        logger.info(String.format("%sParando atualização atual...", updaterNamespace));
                        currentUpdate.interrupt();
                        // Tenta apagar o arquivo antigo
                        if (downloadedFile != null && downloadedFile.exists()) {
                            downloadedFile.delete();
                        }
                    }
                    logger.info(String.format("%sIniciando nova atualização.", updaterNamespace));
                    startUpdate();
                }
            }
        }).start();
    }

    /**
     * Aplica a atualização e move o arquivo antigo para o diretório de versões antigas para que
     * possa ser restaurado caso ocorra algum erro
     *
     * @param newFile Destino do novo arquivo
     */
    public synchronized void applyUpdate(File newFile) {
        downloadedFile = null;
        File oldFile = new File(oldDir, pluginFile.getName());
        logger.info(String.format("%sAplicando atualizações... ", updaterNamespace));
        try {
            Files.move(pluginFile.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.move(newFile.toPath(), pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info(String.format("%sA atualização foi salva! Para aplica-la reinicie o servidor ou o plugin", updaterNamespace));
            logger.info(String.format("%sCaso ocorra algum problema você poderá encontrar a versão antiga no diretório de atualizações!", updaterNamespace));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quando chamado irá limpar todos arquivos antigos de atualizações
     */
    public synchronized void updateSuccess() {
        logger.info(String.format("%sA atualização foi executada com sucesso!", updaterNamespace));
        File[] files = oldDir.listFiles();

        if (files != null && files.length > 0) {
            if (debug) {
                logger.info(String.format("%sRemovendo versões antigas...", updaterNamespace));
            }
            for (File file : files) {
                boolean delete = file.delete();
                if (!delete) {
                    if (debug) {
                        logger.info(String.format("%sErro ao remover o arquivo '%s'. Iremos tentar remove-lo quando o servidor fechar!", updaterNamespace, file));
                    }
                    file.deleteOnExit();
                }
            }
            if (debug) {
                logger.info(String.format("%sVersões antigas removidas!", updaterNamespace));
            }
        }
    }

    /**
     * Define o novo chache
     *
     * @param unsecure     Versão não segura = true, define true se a versão do minecraft não for
     *                     determinada
     * @param versionCache Nova versão para o cache
     */
    private synchronized void setVersionCache(boolean unsecure, Version versionCache) {
        this.updaterCache.set(new UpdaterCache(unsecure, versionCache));
    }

    @Override
    public void callBack(File object) {
        if (auto_install) {
            applyUpdate(object);
        }
    }

    /**
     * Para todos threads criados somente pelo Update (os demais, como do Downloader não são
     * cancelados)
     */
    public void stopAll() {
        ThreadUtils.stopAllThreads(this);
    }
}
