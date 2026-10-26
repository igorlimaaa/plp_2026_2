package loo1.plp.orientadaObjetos1.expressao.binaria;

import loo1.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo1.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo1.plp.orientadaObjetos1.expressao.Expressao;
import loo1.plp.orientadaObjetos1.expressao.valor.Valor;
import loo1.plp.orientadaObjetos1.expressao.valor.ValorBooleano;
import loo1.plp.orientadaObjetos1.expressao.valor.ValorInteiro;
import loo1.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo1.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo1.plp.orientadaObjetos1.util.Tipo;
import loo1.plp.orientadaObjetos1.util.TipoPrimitivo;

public class ExpMenor extends ExpBinaria {

    public ExpMenor(Expressao esq, Expressao dir) {
        super(esq, dir, "<");
    }

    public Valor avaliar(AmbienteExecucaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
        ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        return verificarMenor(ambiente);
    }

    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        boolean result;
        if (super.checaTipo(ambiente)
            && getEsq().getTipo(ambiente) instanceof TipoPrimitivo
            && getDir().getTipo(ambiente) instanceof TipoPrimitivo
            && ((TipoPrimitivo) getEsq().getTipo(ambiente)).eInteiro()
            && ((TipoPrimitivo) getDir().getTipo(ambiente)).eInteiro()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public Tipo getTipo(AmbienteCompilacaoOO1 ambiente) {
        return TipoPrimitivo.TIPO_BOOLEANO;
    }

    private ValorBooleano verificarMenor(AmbienteExecucaoOO1 ambiente)
            throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
                   ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        return new ValorBooleano(
            ((ValorInteiro) getEsq().avaliar(ambiente)).valor() <
            ((ValorInteiro) getDir().avaliar(ambiente)).valor()
        );
    }
}
