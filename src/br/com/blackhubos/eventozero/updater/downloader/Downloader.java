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

import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import br.com.blackhubos.eventozero.EventoZero;

/**
 * Classe que baixar os arquivos apartir de uma URL com um numero determinado de Threads para
 * download simultaneo do mesmo arquivo Cada thread é uma nova conexão no servidor, cuidado ao
 * utiliza-lo, talvez o servidor limite as conexões e o arquivo poderá vir corrompido
 */
public class Downloader {

    private final URL url;
    private final int threads;
    private final long size;
    private final DownloadMonitor downloadMonitor;
    private final List<Thread> threadList = new ArrayList<>();

    /**
     * Constroi um novo downloader apartir da URL, quantidade de threads (e conexões), e com um
     * monitor de progresso especifico
     *
     * @param url             URL de download
     * @param threads         Numero de threads e conexões
     * @param downloadMonitor Monitor de download
     */
    public Downloader(URL url, int threads, DownloadMonitor downloadMonitor) {
        try {
            URLConnection connection = url.openConnection();
            boolean support = connection.getHeaderField("Accept-Ranges").equals("bytes");
            if (threads > 1) {
                if (!support) {
                    //Doesn't supports multi-thread download
                    threads = 1;
                }
            } else if (threads < 1) {
                throw new InvalidParameterException("'threads' parameter cannot be < 1");
            }
            this.downloadMonitor = downloadMonitor;
            size = connection.getContentLengthLong();
            this.url = url;
            this.threads = threads;
        } catch (Exception e) {
            throw new RuntimeException("Fail", e);
        }

    }

    /**
     * Baixa um arquivo especifico
     *
     * @param path     Diretório aonde será salvo o arquivo
     * @param saveFile Nome do arquivo a ser salvo
     */
    public synchronized void download(final File path, final String saveFile) {
        final AtomicLong last = new AtomicLong(0);
        final AtomicInteger downloaded = new AtomicInteger(0);
        for (int x = 0; x < threads; ++x) {

            final long downloadEndRange = ((x + 1 >= threads) ? (size) : (last.get() + (size / threads)));
            final long downloadStartRange = last.get();

            Thread tr = new Thread(() -> {
                try {
                    // Cria um nova instancia para o arquivo
                    File save = new File(path, saveFile);
                    // Verifica se existe
                    if (!save.exists()) {
                        // Cria novo arquivo
                        save.createNewFile();
                    }
                    // Cria uma instancia de acesso aleatorio ao arquivo
                    RandomAccessFile raf = new RandomAccessFile(save, "rw");

                    // "Anda" com o ponteiro até o inicio do arquivo, este inicio é relativo e definido por conexão
                    // Cada conexão baixa uma Range (Inico <-> Fim) do arquivo, assim permitindo multiplos threads escrever no mesmo arquivo sem problemas
                    raf.seek(downloadStartRange);

                    BufferedInputStream bis;

                    // Cria uma nova conexão de URL
                    URLConnection connection = url.openConnection();

                    // Define a Range que o thread irá baixar
                    connection.setRequestProperty("Range", "bytes=" + downloadStartRange + "-" + downloadEndRange + "");

                    // Inicia a conexão
                    connection.connect();

                    // Cria um buffer de 4096 bytes
                    byte[] data = new byte[4096];

                    // Nova int para armazenar a qunatidade de bytes lidos
                    int ibyte;

                    // Nova Stream de entrada com os dados da conexão
                    bis = new BufferedInputStream(connection.getInputStream());

                    // Faz uma leitura dos bytes da conexão, a váriavel ibyte é definida com a quantidade de bytes lidos na stream, quando a quantidade for -1 indica o fim do arquivo
                    while ((ibyte = bis.read(data, 0, 4096)) != -1) {
                        // Escreve na instancia de RandomAccess que irá enviar ao arquivo
                        raf.write(data, 0, ibyte);

                        // Define a quantidade de bytes baixadas
                        // Pode ser usado também: downloaded.addAndGet(ibyte);
                        downloaded.set(downloaded.get() + ibyte);

                    }

                    // Quando terminar de ler arquivo totalmente o thread será removido
                    threadList.remove(Thread.currentThread());

                    // E fechamos a RandomAccessStream
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Depois de definir adiciona o thread
            threadList.add(tr);

            // Inicia o thread
            tr.start();

            // Define a ultima range (para o próximo thread saber aonde começar)
            last.set(downloadEndRange);
        }

        // Criamos um BukkitRunnable para para atualizar os progresso
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                // Faz loop nos threads e verifica se eles acabaram e os remove caso afirmativo
                for (Thread tr : threadList) {
                    if (!tr.isAlive() || tr.isInterrupted()) {
                        threadList.remove(tr);
                    }
                }

                if (threadList.isEmpty()) {
                    // Informa o fim do download
                    downloadMonitor.downloadFinish();

                    // Cancela o Task
                    cancel();
                } else {
                    // Atualiza o progresso
                    downloadMonitor.progressChange(downloaded.get(), size);
                }


            }
        };
        bukkitRunnable.runTaskTimer(EventoZero.getInstance(), 0, TimeUnit.MILLISECONDS.toSeconds(downloadMonitor.getInterval()) * 20L);
    }

    /**
     * Irá forçar a parada dos downloads
     *
     * @return Retorna false se não encontrar nenhum download em andamento
     */
    public boolean stopDownload() {
        if (threadList.isEmpty())
            return false;
        // Faz loop nos threads e verifica se eles ainda estão ativos e força o fechamento do mesmo
        for (Thread tr : threadList) {
            if (!tr.isInterrupted() || tr.isAlive()) {
                tr.interrupt();
                threadList.remove(tr);
            }
        }
        return true;
    }
}
