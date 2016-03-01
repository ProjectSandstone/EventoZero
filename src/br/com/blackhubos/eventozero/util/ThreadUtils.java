/**
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot. Copyright © 2016 BlackHub
 * OS and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package br.com.blackhubos.eventozero.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Simples gerenciador de threads
 */
public final class ThreadUtils {
    // Mapa dos threads e instancias que são as donas do threads
    private static final Map<Object, Set<ThreadId>> threads = new HashMap<>();

    /**
     * Cria um novo thread e registra o mesmo
     *
     * @param instanceRunner Dono do thread
     * @param runnable       Código a ser executado pelo Thread
     * @return O Thread criado
     */
    public synchronized static Thread createNewThread(Object instanceRunner, Runnable runnable) {
        return createNewThread(instanceRunner, null, runnable);
    }

    /**
     * Cria um novo thread e registra o mesmo
     *
     * @param instanceRunner Dono do thread
     * @param id             Id (Identificação) do Thread
     * @param runnable       Código a ser executado pelo Thread
     * @return O Thread criado
     */
    public synchronized static Thread createNewThread(Object instanceRunner, String id, Runnable runnable) {
        Objects.requireNonNull(instanceRunner);
        Objects.requireNonNull(runnable);

        Thread thread;
        if (id != null) {
            thread = new Thread(runnable, id);
        } else {
            thread = new Thread(runnable);
        }
        add(instanceRunner, new ThreadId(instanceRunner, id, thread, runnable));
        return thread;
    }

    /**
     * Para todos threads registrados
     */
    public synchronized static void stopAllThreads() {
        threads.keySet().forEach(ThreadUtils::stopAllThreads);
    }

    /**
     * Para todos threads registrados pela instancia
     *
     * @param instanceRunner Instancia que registrou o Thread
     */
    public synchronized static void stopAllThreads(Object instanceRunner) {
        stopThread(instanceRunner, (String) null);
    }

    /**
     * Obtém um thread a partir de um ID
     *
     * @param instanceRunner Instancia que registrou o Thread
     * @param threadId       Id (Identificação) do Thread
     * @return Thread correspondente
     */
    public synchronized static Optional<Thread> getThread(Object instanceRunner, String threadId) {
        Objects.requireNonNull(instanceRunner);
        Objects.requireNonNull(threadId);
        synchronized (threads) {
            if (threads.containsKey(instanceRunner)) {
                for (ThreadId thread : threads.get(instanceRunner)) {
                    if (threadId.equals(thread.getId())) {
                        return Optional.of(thread.getThread());
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Obtém um Thread a partir de seu Runnable
     *
     * @param instanceRunner Instancia que registrou o Thread
     * @param runnable       Runnable do thread
     * @since 01/03/2016
     * @return Thread correspondente ou um {@link Optional#empty()}
     */
    public synchronized static Optional<Thread> getThread(Object instanceRunner, Runnable runnable) {
        Objects.requireNonNull(instanceRunner);
        Objects.requireNonNull(runnable);

        synchronized (threads) {
            if (threads.containsKey(instanceRunner)) {
                Optional<ThreadId> threadIdOptional = threads.get(instanceRunner).stream().filter(threadId -> threadId != null && threadId.getRunnable() != null && threadId.getRunnable() == runnable).findFirst();
                if (threadIdOptional.isPresent())
                    return Optional.of(threadIdOptional.get().getThread());
            }
        }

        return Optional.empty();
    }

    /**
     * Para o thread baseado no ID (se for nulo, para todos threads) e na instancia que o registrou
     *
     * @param instanceRunner Instancia que registrou o Thread
     * @param threadId       Id (Identificação) do Thread
     */
    public synchronized static void stopThread(Object instanceRunner, String threadId) {
        Objects.requireNonNull(instanceRunner);
        if (threads.containsKey(instanceRunner)) {
            threads.get(instanceRunner)
                    .stream()
                    .filter(thread -> threadId == null || threadId.equals(thread.getId()))
                    .filter(thread -> !thread.getThread().isInterrupted() || thread.getThread().isAlive())
                    .forEach(thread -> thread.getThread().interrupt());
            cleanUp();
        }
    }

    /**
     * Para um Thread baseado em seu Runnable
     * @param instanceRunner Instancia que registrou o Thread
     * @param threadRunnable Runnable do thread
     * @since 01/03/2016
     */
    public synchronized static void stopThread(Object instanceRunner, Runnable threadRunnable) {
        Objects.requireNonNull(instanceRunner);
        if (threads.containsKey(instanceRunner)) {
            threads.get(instanceRunner)
                    .stream()
                    .filter(thread -> threadRunnable == null || thread.getRunnable() == threadRunnable)
                    .filter(thread -> !thread.getThread().isInterrupted() || thread.getThread().isAlive())
                    .forEach(thread -> thread.getThread().interrupt());
            cleanUp();
        }
    }

    /**
     * Método para adicionar um ThreadId na List do Map da Instancia
     *
     * @param instanceRunner Instancia que registrou o Thread
     * @param thread         Thread para adicionar
     */
    private synchronized static void add(Object instanceRunner, ThreadId thread) {
        setEmpty(instanceRunner);
        synchronized (threads) {
            threads.get(instanceRunner).add(thread);
        }
    }

    /**
     * Cria um novo set vazio para a instancia (é uma preparação para chamar o método add)
     *
     * @param instanceRunner Instancia que registrou o Thread
     */
    private synchronized static void setEmpty(Object instanceRunner) {
        synchronized (threads) {
            Objects.requireNonNull(instanceRunner);
            if (!threads.containsKey(instanceRunner))
                threads.put(instanceRunner, new HashSet<>());
        }
    }

    /**
     * Conta todos threads registrados
     *
     * @return Numero threads registrados
     */
    public synchronized static int countAllThreads() {
        int count = 0;
        synchronized (threads) {
            for (Map.Entry<Object, Set<ThreadId>> entry : threads.entrySet()) {
                count += entry.getValue().size();
            }
        }
        return count;
    }

    /**
     * Limpa todos threads que já foram parados
     */
    private synchronized static void cleanUp() {
        synchronized (threads) {
            Iterator<Map.Entry<Object, Set<ThreadId>>> entriesIterator = threads.entrySet().iterator();

            while (entriesIterator.hasNext()) {
                Map.Entry<Object, Set<ThreadId>> entry = entriesIterator.next();
                Iterator<ThreadId> threadIds = entry.getValue().iterator();

                while (threadIds.hasNext()) {
                    ThreadId current = threadIds.next();
                    if (current.canCleanUp()) {
                        threadIds.remove();
                    }
                }
                if (entry.getValue().isEmpty()) {
                    entriesIterator.remove();
                }
            }
        }
    }

    /**
     * Classe de informação do Thread
     */
    private static final class ThreadId {
        private final Object holder;
        private final String id;
        private final Thread thread;
        private final Runnable runnable;

        /**
         * @param id       Id (Identificação) do Thread
         * @param thread   Thread correspondente ao Id
         * @param runnable Runnable do Thread
         */
        private ThreadId(Object holder, String id, Thread thread, Runnable runnable) {
            Objects.requireNonNull(thread);
            this.id = id;
            this.thread = thread;
            if (runnable != null)
                this.runnable = runnable;
            else
                this.runnable = thread;
            this.holder = holder;
        }

        /**
         * Obtem o ID do Thread
         *
         * @return O ID do Thread
         */
        public String getId() {
            return id;
        }

        /**
         * Obtem o Thread
         *
         * @return O Thread
         */
        public Thread getThread() {
            return thread;
        }

        /**
         * Retorna o "Runnable" do Thread
         *
         * @return Runnable do Thread
         */
        public Runnable getRunnable() {
            return runnable;
        }

        /**
         * Obtem a instancia que registrou o thread
         *
         * @return Instancia que registrou o thread
         */
        public Object getHolder() {
            return holder;
        }

        /**
         * Determina se pode limpar o Thread (geralmente quando já foi parado)
         *
         * @return Se pode limpar o Thread
         */
        public boolean canCleanUp() {
            return !getThread().isAlive() || getThread().isInterrupted();
        }

        @Override
        public int hashCode() {
            if (id != null)
                return id.hashCode();
            else
                return super.hashCode();
        }
    }
}
