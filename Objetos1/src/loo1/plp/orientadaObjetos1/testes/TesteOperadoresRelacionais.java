package loo1.plp.orientadaObjetos1.testes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import loo1.plp.orientadaObjetos1.Programa;
import loo1.plp.orientadaObjetos1.memoria.ContextoCompilacaoOO1;
import loo1.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo1.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo1.plp.orientadaObjetos1.parser.OO1Parser;

public final class TesteOperadoresRelacionais {
    private TesteOperadoresRelacionais() {
    }

    public static void main(String[] args) throws Exception {
        String fonte = "{ classe Teste { int valor = 0; proc executar() { "
                + "this.valor := 7; "
                + "if this.valor > 5 then { write(\"maior\") } else { write(\"nao-maior\") }; "
                + "if this.valor < 10 then { write(\"menor\") } else { write(\"nao-menor\") } "
                + "} }; { Teste teste := new Teste; teste.executar() } }";
        Programa programa = new OO1Parser(new StringReader(fonte)).processaEntrada();
        exige(programa.checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                "comparações > e < entre inteiros devem ser bem tipadas");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;
        System.setOut(new PrintStream(bytes));
        try {
            programa.executar(new ContextoExecucaoOO1(new ListaValor()));
        } finally {
            System.setOut(saidaOriginal);
        }
        String saida = bytes.toString(StandardCharsets.UTF_8.name());
        exige(saida.contains("maior") && saida.contains("menor"),
                "as expressões > e < devem selecionar os ramos verdadeiros");
        System.out.println(".\n.\n\u001B[1mTestes dos operadores > e < passaram.");
    }

    private static void exige(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}