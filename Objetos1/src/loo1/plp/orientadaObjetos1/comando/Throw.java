package loo1.plp.orientadaObjetos1.comando;

import loo1.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo1.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo1.plp.orientadaObjetos1.excecao.execucao.ExcecaoLancadaException;
import loo1.plp.orientadaObjetos1.expressao.Expressao;
import loo1.plp.orientadaObjetos1.expressao.valor.Valor;
import loo1.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo1.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;

/**
 * Comando de lançamento explícito de exceção: <code>throw Expressao</code>.
 */
public class Throw implements Comando {
    /**
     * Expressão cujo valor será lançado como exceção.
     */
    private Expressao expressao;

    /**
     * Construtor.
     * @param expressao Expressão cujo valor será lançado como exceção.
     */
    public Throw(Expressao expressao) {
        this.expressao = expressao;
    }

    /**
     * Avalia a expressão e interrompe a execução sinalizando a exceção
     * através de {@link ExcecaoLancadaException}, que carrega o valor lançado.
     * @param ambiente o ambiente de execução.
     * @return nunca retorna normalmente.
     */
    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente)
        throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
        ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        Valor valor = expressao.avaliar(ambiente);
        throw new ExcecaoLancadaException(valor);
    }

    /**
     * Realiza a verificacao de tipos da expressão lançada pelo comando
     * <code>throw</code>.
     * @param ambiente o ambiente de compilação.
     * @return <code>true</code> se a expressão está bem tipada;
     *          <code>false</code> caso contrario.
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
        ClasseNaoDeclaradaException {
        return expressao.checaTipo(ambiente);
    }
}
