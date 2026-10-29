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
 * Testes de parsing, tipagem e execução do comando defer.
 */
public final class TesteDefer {
    private TesteDefer() {
    }

    public static void main(String[] args) throws Exception {
        verificaOrdemLifo();
        verificaDeferExecutaComThrowSemCatch();
        verificaOrdemComTryFinallyAninhado();
        verificaDeferVeEstadoNaSaida();
        verificaDeferDeVariavelDeBlocoFalhaAoSair();
        verificaDeferForaDeMetodo();
        System.out.println(".\n.\n\u001B[1mTestes defer passaram.");
    }

    private static void verificaOrdemLifo() throws Exception {
        String fonte = programa(
                "defer write(\"1\");",
                "defer write(\"2\");",
                "defer write(\"3\");",
                "write(\"corpo\")");
        String saida = executa(fonte);
        int corpo = saida.indexOf("corpo");
        int terceiro = saida.indexOf("3");
        int segundo = saida.indexOf("2");
        int primeiro = saida.indexOf("1");
        exige(corpo >= 0 && corpo < terceiro, "o corpo do método deve executar antes de qualquer defer");
        exige(terceiro < segundo && segundo < primeiro,
                "os defer devem executar em ordem LIFO (último declarado primeiro)");
        System.out.println("Teste de verificaOrdemLifo passou.");
    }

    private static void verificaDeferExecutaComThrowSemCatch() throws Exception {
        String fonte = programa(
                "defer write(\"liberou recurso\");",
                "throw \"falhou\"");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;
        ExcecaoLancadaException lancada = null;
        System.setOut(new PrintStream(bytes));
        try {
            Programa programa = parse(fonte);
            exige(programa.checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                    "programa com defer antes de um throw sem catch deve ser bem tipado");
            programa.executar(new ContextoExecucaoOO1(new ListaValor()));
        } catch (ExcecaoLancadaException excecao) {
            lancada = excecao;
        } finally {
            System.setOut(saidaOriginal);
        }
        exige(lancada != null && "falhou".equals(lancada.getValorLancado().toString()),
                "a exceção sem catch deve continuar propagando normalmente");
        exige(bytes.toString(StandardCharsets.UTF_8.name()).contains("liberou recurso"),
                "defer deve executar mesmo quando o método lança sem capturar a exceção");
        System.out.println("Teste de verificaDeferExecutaComThrowSemCatch passou.");
    }

    private static void verificaOrdemComTryFinallyAninhado() throws Exception {
        String fonte = programa(
                "defer write(\"saida do metodo\");",
                "try {",
                "  throw \"erro\"",
                "} catch (erro) {",
                "  write(\"catch\")",
                "} finally {",
                "  write(\"finally do try\")",
                "}");
        String saida = executa(fonte);
        int finallyDoTry = saida.indexOf("finally do try");
        int saidaDoMetodo = saida.indexOf("saida do metodo");
        exige(finallyDoTry >= 0 && saidaDoMetodo >= 0 && finallyDoTry < saidaDoMetodo,
                "o finally de um try interno deve executar antes dos defer do método, "
                        + "que só correm quando o método como um todo termina");
        System.out.println("Teste de verificaOrdemComTryFinallyAninhado passou.");
    }

    private static void verificaDeferVeEstadoNaSaida() throws Exception {
        String fonte = programa(
                "defer write(this.valor);",
                "this.valor := 2");
        String saida = executa(fonte);
        exige(saida.contains("2") && !saida.contains("0"),
                "o comando adiado deve ler o atributo no momento em que executa (na saída do método), "
                        + "não no momento em que o defer foi declarado (semântica tardia, diferente "
                        + "do defer de Go, que avalia os argumentos imediatamente)");
        System.out.println("Teste de verificaDeferVeEstadoNaSaida passou.");
    }

    private static void verificaDeferDeVariavelDeBlocoFalhaAoSair() throws Exception {
        String fonte = programa(
                "{ int x = 1;",
                "  defer write(x);",
                "  x := 2",
                "}");
        boolean falhouEmTempoDeExecucao = false;
        Programa programa = parse(fonte);
        exige(programa.checaTipo(new ContextoCompilacaoOO1(new ListaValor())),
                "o defer de uma variável de bloco deve passar a checagem de tipos: "
                        + "o escopo só é violado em tempo de execução");
        try {
            programa.executar(new ContextoExecucaoOO1(new ListaValor()));
        } catch (VariavelNaoDeclaradaException excecao) {
            falhouEmTempoDeExecucao = true;
        }
        exige(falhouEmTempoDeExecucao,
                "defer que referencia uma variável de um bloco já encerrado deve falhar em tempo de "
                        + "execução: o próprio bloco restaura seu escopo antes do método terminar, e só "
                        + "então os defer do método são drenados — por isso defer só deve referenciar "
                        + "this/atributos/parâmetros, nunca variáveis locais de blocos aninhados");
        System.out.println("Teste de verificaDeferDeVariavelDeBlocoFalhaAoSair passou.");
    }

    private static void verificaDeferForaDeMetodo() throws Exception {
        String fonte = "{ classe Vazia { int x = 0; proc nada() { skip } };"
                + " { Vazia v := new Vazia; defer write(\"fim do programa\"); write(\"corpo do programa\") } }";
        String saida = executa(fonte);
        exige(saida.indexOf("corpo do programa") < saida.indexOf("fim do programa"),
                "defer fora de qualquer método deve executar quando o programa termina");
        System.out.println("Teste de verificaDeferForaDeMetodo passou.");
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
