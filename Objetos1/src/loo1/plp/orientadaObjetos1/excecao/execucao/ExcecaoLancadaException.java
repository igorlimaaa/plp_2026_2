package loo1.plp.orientadaObjetos1.excecao.execucao;

import loo1.plp.orientadaObjetos1.expressao.valor.Valor;

/**
 * Exceção de execução que carrega o valor sinalizado pelo comando
 * <code>throw</code> da linguagem OO1. Ela não representa um erro do
 * interpretador, e sim o mecanismo de sinalização de exceções da própria
 * linguagem, por isso é não-checada: deve poder atravessar os demais comandos
 * até encontrar um bloco <code>catch</code> compatível.
 */
public class ExcecaoLancadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Valor valorLancado;

    public ExcecaoLancadaException(Valor valorLancado) {
        super(String.valueOf(valorLancado));
        this.valorLancado = valorLancado;
    }

    public Valor getValorLancado() {
        return valorLancado;
    }
}
