package loo1.plp.orientadaObjetos1.testes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import loo1.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo1.plp.orientadaObjetos1.Programa;
import loo1.plp.orientadaObjetos1.excecao.execucao.ExcecaoLancadaException;
import loo1.plp.orientadaObjetos1.memoria.ContextoCompilacaoOO1;
import loo1.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo1.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo1.plp.orientadaObjetos1.parser.OO1Parser;

/**
 * Testes de parsing, tipagem e execução do comando try/catch/finally.
 */
public final class TesteTryCatchFinally {
    private TesteTryCatchFinally() {
    }

    public static void main(String[] args) throws Exception {
        verificaCapturaEFinally();
        verificaFinallySemExcecao();
        verificaFinallyEmExcecaoNoCatch();
        verificaEscopoAninhadoComExcecao();
        verificaExcecaoDoFinallyPrevalece();
        verificaTrySemFinally();
        verificaEscopoDoCatch();
        verificaTipoDinamicoDoCatch();
        verificaCatchObrigatorio();
        System.out.println(".\n.\n\u001B[1mTestes try/catch/finally passaram.");
    }

    private static void verificaFinallySemExcecao() throws Exception {
        String fonte = programa(
                "try {",
                "  write(\"try\")",
                "} catch (erro) {",
                "  write(erro)",
                "} finally {",
                "  write(\"finally normal\")",
                "}");
        String saida = executa(fonte);
        exige(saida.indexOf("try") >= 0, "bloco try deve executar normalmente");
        exige(saida.indexOf("finally normal") > saida.indexOf("try"),
            "finally deve executar quando não há exceção");
        System.out.println("Teste de verificaFinallySemExcecao passou.");
    }

    private static void verificaCapturaEFinally() throws Exception {
        String fonte = programa(
                "try {",
                "  throw \"erro original\"",
                "} catch (erro) {",
                "  write(erro)",
                "} finally {",
                "  write(\"finalizado\")",
                "}");
        String saida = executa(fonte);
        exige(saida.indexOf("erro original") >= 0, "catch deve receber o valor lançado");
        exige(saida.indexOf("finalizado") > saida.indexOf("erro original"),
                "finally deve executar depois do catch");
        System.out.println("Teste de verificaCapturaEFinally passou.");
    }

    private static void verificaEscopoAninhadoComExcecao() throws Exception {
        String fonte = programa(
                "try {",
                "  { int local = 1; throw \"aninhada\" }",
                "} catch (erro) {",
                "  write(erro)",
                "} finally {",
                "  write(\"escopos restaurados\")",
                "}");
        String saida = executa(fonte);
        exige(saida.indexOf("aninhada") >= 0, "catch deve capturar throw de bloco aninhado");
        exige(saida.indexOf("escopos restaurados") > saida.indexOf("aninhada"),
            "finally deve executar após throw em bloco aninhado");

        String escopoSombrado = programa(
            "{ int valor = 10; try { { int valor = 20; throw \"aninhada\" } } "
                + "catch (erro) { skip }; write(valor) }");
        saida = executa(escopoSombrado);
        exige(saida.contains("10") && !saida.contains("20"),
            "escopo local do try não pode ocultar a variável externa após a exceção");
        System.out.println("Teste de verificaEscopoAninhadoComExcecao passou.");
    }

    private static void verificaExcecaoDoFinallyPrevalece() throws Exception {
        String fonte = programa(
                "try { throw \"try\" }",
                "catch (erro) { skip }",
                "finally { throw \"finally\" }");
        ExcecaoLancadaException lancada = null;
        try {
            Programa programa = parse(fonte);
            exige(programa.checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                "programa com throw em finally deve ser bem tipado");
            programa.executar(new ContextoExecucaoOO1(new ListaValor()));
        } catch (ExcecaoLancadaException excecao) {
            lancada = excecao;
        }
        exige(lancada != null && "finally".equals(lancada.getValorLancado().toString()),
            "exceção de finally deve prevalecer sobre a anterior");
        System.out.println("Teste de verificaExcecaoDoFinallyPrevalece passou.");
    }

    private static void verificaFinallyEmExcecaoNoCatch() throws Exception {
        String fonte = programa(
            "try { throw \"primeira\" }",
            "catch (erro) { throw \"segunda\" }",
            "finally { write(\"finally executado\") }");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;
        ExcecaoLancadaException lancada = null;
        System.setOut(new PrintStream(bytes));
        try {
            Programa programa = parse(fonte);
            exige(programa.checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                    "programa com rethrow deve ser bem tipado");
            programa.executar(new ContextoExecucaoOO1(new ListaValor()));
        } catch (ExcecaoLancadaException excecao) {
            lancada = excecao;
        } finally {
            System.setOut(saidaOriginal);
        }
        exige(lancada != null && "segunda".equals(lancada.getValorLancado().toString()),
                "a exceção relançada pelo catch deve propagar");
        exige(bytes.toString(StandardCharsets.UTF_8.name()).contains("finally executado"),
                "finally deve executar antes de propagar a exceção do catch");
        System.out.println("Teste de verificaFinallyEmExcecaoNoCatch passou.");
    }

    private static void verificaTrySemFinally() throws Exception {
        String fonte = programa(
            "try {",
            "  throw \"capturado\"",
            "} catch (erro) {",
            "  write(erro)",
            "}");
        exige(executa(fonte).contains("capturado"), "finally deve ser opcional");
        System.out.println("Teste de verificaTrySemFinally passou.");
    }

    private static void verificaEscopoDoCatch() throws Exception {
        String fonte = programa(
            "try { throw \"local\" } catch (erro) { write(erro) };",
            "write(erro)");
        boolean rejeitado = false;
        try {
            parse(fonte).checaTipo(new ContextoCompilacaoOO1(new ListaValor()));
        } catch (VariavelNaoDeclaradaException excecao) {
            rejeitado = true;
        }
        exige(rejeitado, "identificador do catch não pode escapar do bloco");
        System.out.println("Teste de verificaEscopoDoCatch passou.");
    }

    private static void verificaTipoDinamicoDoCatch() throws Exception {
        String valido = programa(
            "try { throw 7 } catch (erro) {",
            "  write(\"valor: \" ++ erro)",
            "}");
        exige(parse(valido).checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                "catch deve aceitar valores de diferentes tipos em write/concatenação");

        String invalido = programa(
            "try { throw 7 } catch (erro) {",
            "  write(erro + 1)",
            "}");
        exige(!parse(invalido).checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                "o valor dinâmico do catch não deve ser usado como inteiro");

        String condicaoInvalida = programa(
            "try { throw true } catch (erro) {",
            "  if erro then { skip }",
            "}");
        exige(!parse(condicaoInvalida).checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
            "o valor dinâmico do catch não deve ser usado como condição booleana");
        System.out.println("Teste de verificaTipoDinamicoDoCatch passou.");
    }

    private static void verificaCatchObrigatorio() throws Exception {
        String fonte = programa(
            "try { write(\"sem catch\") }",
            "finally { write(\"fim\") }");
        boolean rejeitado = false;
        try {
            parse(fonte);
        } catch (Exception excecao) {
            rejeitado = true;
        }
        exige(rejeitado, "try/finally sem catch deve ser rejeitado pela BNF");
        System.out.println("Teste de verificaCatchObrigatorio passou.");
    }

    private static String executa(String fonte) throws Exception {
        Programa programa = parse(fonte);
        exige(programa.checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                "programa deveria passar na checagem de tipos");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;
        System.setOut(new PrintStream(bytes));
        try {
            programa.executar(new ContextoExecucaoOO1(new ListaValor()));
        } finally {
            System.setOut(saidaOriginal);
        }
        return bytes.toString(StandardCharsets.UTF_8.name());
    }

    private static Programa parse(String fonte) throws Exception {
        return new OO1Parser(new StringReader(fonte)).processaEntrada();
    }

    private static String programa(String... linhasComando) {
        String comando = String.join("\n", linhasComando);
        return "{ classe Teste { int valor = 0; proc executar() { " + comando
                + " } }; { Teste teste := new Teste; teste.executar() } }";
    }

    private static void exige(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
