package loo1.plp.orientadaObjetos1.util;

import loo1.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo1.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;

/**
 * Tipo estatico do valor associado ao identificador de um bloco catch.
 */
public final class TipoExcecao implements Tipo {
    public static final TipoExcecao INSTANCIA = new TipoExcecao();

    private static final Id IDENTIFICADOR_TIPO = new Id("exception");

    private TipoExcecao() {
    }

    @Override
    public Id getTipo() {
        return IDENTIFICADOR_TIPO;
    }

    @Override
    public boolean eValido(AmbienteCompilacaoOO1 ambiente) {
        return true;
    }

    @Override
    public boolean equals(Object objeto) {
        return objeto instanceof TipoExcecao;
    }

    @Override
    public int hashCode() {
        return TipoExcecao.class.hashCode();
    }
}
