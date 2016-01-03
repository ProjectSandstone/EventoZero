package br.com.blackhubos.eventozero.updater.formater;

/**
 * Created by jonathan on 03/01/16.
 */

import com.google.common.base.Optional;

import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Formatador de tipos
 * @param <T> Tipo suportado
 */
// TODO: Fazer uma classe que contem varios formatadores de tipos para ser usada no Parser dando a possibilidade de multiplos formatadores
// TODO: ou utilizarmos o AnnonTypeFormater
public interface TypeFormatter<T> {

    /**
     * Formata o objectToFormat e retorna o tipo T
     * Pode retornar um valor nulo, para evitar isto use {@link #tryFormatType(Object)} ou {@link #formatType(Object)}
     * OBS Para Implementação: é altamente recomendado a implementação utilizar este método para formatar os tipos e os demais para
     * Fazer a chamada deste método
     * @see BooleanFormatter Para ter um modelo de implementação
     * @see #tryFormatType(Object)
     * @see #formatType(Object)
     * @param objectToFormat Objeto para formatar
     * @deprecated Este método pode retornar valores nulos, provavelmente você não quer receber um NullExceptionPointer né?
     * @return Valor formatado
     */
    T unsecuredFormatType(Object objectToFormat);

    /**
     * Tem a mesma função que o {@link #formatType(Object)}
     * Este método pode gerar uma exceção do tipo {@link br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException}
     * Você pode usar o {@link #formatType(Object)} para evitar exceções e valores nulos!
     * @see #formatType(Object)
     * @param objectToFormat Objecto para formatar
     * @return Valor formatado
     */
    T tryFormatType(Object objectToFormat) throws CannotFormatTypeException;

    /**
     * Melhor forma para formatar tipos, impede valores nulos e evita exceções
     * @param objectToFormat Objeto para formatar
     * @return Valor formatado
     */
    Optional<T> formatType(Object objectToFormat);

    /**
     * Retorna as classe que ele pode formatar
     * Motivo deste método: Genéricos não funciona muito bem com reflection para obter esta classe.
     * @return Classe que ele pode formatar
     */
    Class<? extends T> getFormatClass();
}
