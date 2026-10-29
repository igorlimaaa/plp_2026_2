package loo1.plp.orientadaObjetos1.comando;

import loo1.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo1.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ObjetoJaDeclaradoException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo1.plp.orientadaObjetos1.excecao.execucao.EntradaInvalidaException;
import loo1.plp.orientadaObjetos1.excecao.execucao.ExcecaoLancadaException;
import loo1.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo1.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo1.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo1.plp.orientadaObjetos1.util.TipoExcecao;

/**
 * Representa try/catch com um bloco finally opcional.
 */
public class TryCatchFinally implements Comando {
    private final Comando comandoTry;
    private final Id identificadorCatch;
    private final Comando comandoCatch;
    private final Comando comandoFinally;

    public TryCatchFinally(Comando comandoTry, Id identificadorCatch,
            Comando comandoCatch, Comando comandoFinally) {
        this.comandoTry = comandoTry;
        this.identificadorCatch = identificadorCatch;
        this.comandoCatch = comandoCatch;
        this.comandoFinally = comandoFinally;
    }

    @Override
    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente)
            throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
            ObjetoJaDeclaradoException, ObjetoNaoDeclaradoException,
            ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
            ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
            EntradaInvalidaException {
        AmbienteExecucaoOO1 resultado = ambiente;
        try {
            resultado = comandoTry.executar(resultado);
        } catch (ExcecaoLancadaException excecao) {
            resultado.incrementa();
            try {
                resultado.map(identificadorCatch, excecao.getValorLancado());
                resultado = comandoCatch.executar(resultado);
            } finally {
                resultado.restaura();
            }
        } finally {
            if (comandoFinally != null) {
                resultado = comandoFinally.executar(resultado);
            }
        }
        return resultado;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
            ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
            ClasseJaDeclaradaException, ClasseNaoDeclaradaException {
        if (!comandoTry.checaTipo(ambiente)) {
            return false;
        }

        ambiente.incrementa();
        try {
            ambiente.map(identificadorCatch, TipoExcecao.INSTANCIA);
            if (!comandoCatch.checaTipo(ambiente)) {
                return false;
            }
        } finally {
            ambiente.restaura();
        }

        return comandoFinally == null || comandoFinally.checaTipo(ambiente);
    }
}
