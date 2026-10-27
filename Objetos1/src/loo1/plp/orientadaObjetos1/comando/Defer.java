package loo1.plp.orientadaObjetos1.comando;

import loo1.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo1.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo1.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo1.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;

/**
 * Comando de adiamento de execucao: <code>defer Comando</code>. Em vez de
 * executar o comando imediatamente, ele e empilhado para ser executado
 * obrigatoriamente na saida do metodo atual (ou do programa, se declarado
 * fora de qualquer metodo), em ordem LIFO em relacao aos demais
 * <code>defer</code> do mesmo metodo.
 */
public class Defer implements Comando {
    /**
     * Comando cuja execucao sera adiada.
     */
    private Comando comando;

    /**
     * Construtor.
     * @param comando Comando cuja execucao sera adiada.
     */
    public Defer(Comando comando) {
        this.comando = comando;
    }

    /**
     * Nao executa o comando agora: apenas o empilha no ambiente para ser
     * executado quando o metodo (ou o programa) atual terminar.
     * @param ambiente o ambiente de execucao.
     * @return o ambiente, inalterado alem do registro do comando adiado.
     */
    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente) {
        ambiente.adicionaDefer(this.comando);
        return ambiente;
    }

    /**
     * Realiza a verificacao de tipos do comando adiado, no ponto onde o
     * <code>defer</code> foi declarado (e nao no momento em que ele sera
     * efetivamente executado).
     * @param ambiente o ambiente de compilacao.
     * @return <code>true</code> se o comando adiado esta bem tipado;
     *          <code>false</code> caso contrario.
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
        ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
        ClasseJaDeclaradaException, ClasseNaoDeclaradaException {
        return this.comando.checaTipo(ambiente);
    }
}
