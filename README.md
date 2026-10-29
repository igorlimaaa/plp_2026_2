# Proposta de Projeto

**Disciplina:** Paradigmas de Linguagens de Programação (IN1007)
**Professor:** Augusto Sampaio

**Equipe:** Amanda Melo (abm7@cin.ufpe.br) e Igor Lima (ial@cin.ufpe.br)

## 1. Suporte ao Mecanismo de Exceções e Adiamento de Execução

Suporte ao Mecanismo de Exceções (`try` / `catch` / `finally` / `throw`) e Adiamento de Execução (`defer`) na **Linguagem Orientada a Objetos 1 (OO1)** via JavaCC.

#### 🔗 [Apresentação em slides](https://www.figma.com/deck/CEvxdYZf0PaplMmrBkzZbe/PLP---Apresenta%C3%A7%C3%A3o-1?node-id=1-27&t=E2nRvbpGEjQCdevA-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1)

## 2. Descrição das Funcionalidades

- **Lançamento Explícito de Erro (`throw`):** Interrompe a sequência normal do bloco e sinaliza uma exceção contendo um valor ou mensagem.
  - [Throw.java](https://github.com/igorlimaaa/plp_2026_2/blob/main/Objetos1/src/loo1/plp/orientadaObjetos1/comando/Throw.java)
- **Captura e Tratamento (`try` / `catch`):** Avalia o bloco monitorado e, em caso de exceção, redireciona o fluxo para o `catch`, registrando o erro em uma variável local com escopo isolado.
  - [TryCatchFinally.java](https://github.com/igorlimaaa/plp_2026_2/blob/main/Objetos1/src/loo1/plp/orientadaObjetos1/comando/TryCatchFinally.java)
- **Garantia de Finalização (`finally`):** Bloco cuja execução é garantida obrigatoriamente antes de concluir a instrução `try`, independentemente da ocorrência de exceções.
  - [TryCatchFinally.java](https://github.com/igorlimaaa/plp_2026_2/blob/main/Objetos1/src/loo1/plp/orientadaObjetos1/comando/TryCatchFinally.java)
- **Comando de Adiamento (`defer`):** Agenda um comando para ser executado obrigatoriamente no momento de saída do método atual. Múltiplos comandos `defer` declarados dentro do método são organizados em uma pilha LIFO.
  - [Defer.java](https://github.com/igorlimaaa/plp_2026_2/blob/main/Objetos1/src/loo1/plp/orientadaObjetos1/comando/Defer.java)

## 3. Exemplo de Código na Linguagem Estendida

```java
{
  classe ContaBancaria {
    int saldo = 100;

    proc sacar(int valor) {
      // defer: executado obrigatoriamente ao finalizar a execução do método (ordem LIFO)
      defer write("1. [defer] Registro de auditoria gravado no log.");
      defer write("2. [defer] Estado do objeto conta liberado.");

      try {
        if valor > this.saldo then {
          throw "Saldo insuficiente para o saque"
        };
        this.saldo := this.saldo - valor;
        write("Saque efetuado com sucesso. Saldo restante: " ++ this.saldo)
      } catch (erro) {
        write("Falha ao processar saque: " ++ erro)
      } finally {
        // finally: executado obrigatoriamente ao final da estrutura try/catch
        write("3. [finally] Bloco de confirmacao concluido.")
      }
    }
  };

  {
    ContaBancaria minhaConta := new ContaBancaria;
    minhaConta.sacar(150)
  }
}
```

> Este é o exemplo completo da linguagem pretendida para a entrega final. Nesta branch, declarações, atribuições, condições, instanciação, comparações `>` e `<`, o bloco `try/catch/finally` e o comando `defer` são aceitos pela gramática OO1. A variável do `catch` tem tipo dinâmico: pode ser escrita ou concatenada com `++`, mas não usada como operando numérico ou lógico. `defer` empilha o comando e só o executa quando o método atual termina (ou quando o programa termina, se declarado fora de qualquer método); por isso, o comando adiado deve referenciar apenas `this`, atributos ou parâmetros do método — uma variável local de um bloco aninhado já está fora de escopo quando os `defer` do método são executados.

## 4. BNF

```ebnf
Programa ::= "{" DecClasse ";" Comando "}"

Comando ::= Atribuicao
          | ComDeclaracao
          | While
          | IfThenElse
          | IO
          | Comando ";" Comando
          | Skip
          | New
          | ChamadaMetodo
          | TryCatchFinally
          | Throw
          | Defer

TryCatchFinally ::= "try" "{" Comando "}" "catch" "(" Id ")" "{" Comando "}" [ "finally" "{" Comando "}" ]

Throw ::= "throw" Expressao

Defer ::= "defer" Comando

Skip ::= 
ComDeclaracao ::= "{" DecVariavel ";" Comando "}"
While ::= "while" Expressao "do" "{" Comando "}"
IfThenElse ::= "if" Expressao "then" "{" Comando "}"
             | "if" Expressao "then" "{" Comando "}" "else" "{" Comando "}"
ChamadaMetodo ::= Expressao "." Id "(" ListaExpressao ")" | Expressao "." Id "(" ")"
ListaExpressao ::= Expressao | Expressao "," ListaExpressao
New ::= LeftExpression ":=" "new" Id
Atribuicao ::= LeftExpression ":=" Expressao
IO ::= Write | Read

Expressao ::= Valor
            | ExpUnaria
            | ExpBinaria
            | LeftExpression
            | This

Valor ::= ValorConcreto
ValorConcreto ::= ValorInteiro
               | ValorBooleano
               | ValorString
               | ValorNull

ExpUnaria ::= "-" Expressao
            | "not" Expressao
            | "length" Expressao

ExpBinaria ::= Expressao "+" Expressao
             | Expressao "-" Expressao
             | Expressao "and" Expressao
             | Expressao "or" Expressao
             | Expressao "==" Expressao
             | Expressao ">" Expressao
             | Expressao "<" Expressao
             | Expressao "++" Expressao

LeftExpression ::= Id | AcessoAtributo
AcessoAtributo ::= LeftExpression "." Id | "this" "." Id

DecClasse ::= "classe" Id "{" DecVariavel ";" DecProcedimento "}"
            | DecClasse "," DecClasse

DecVariavel ::= Tipo Id "=" Expressao
              | DecVariavel "," DecVariavel
              | Tipo Id ":=" "new" Id

DecProcedimento ::= "proc" Id "(" ListaDeclaracaoParametro ")" "{" Comando "}"
                  | DecProcedimento "," DecProcedimento

ListaDeclaracaoParametro ::= Tipo Id | Tipo Id "," ListaDeclaracaoParametro

Tipo ::= TipoClasse | TipoPrimitivo
TipoClasse ::= Id
TipoPrimitivo ::= "int" | "boolean" | "string"
```

## 5. Como executar o projeto OO1

Com o JDK e o Maven instalados, execute a partir da raiz do repositório:

```bash
cd Objetos1
mvn compile exec:java -Dexec.args=../Testes/TesteOO1_ContaBancaria.txt
```

O Maven gera o parser JavaCC, compila o projeto e executa o arquivo informado. Para executar outro programa, substitua o caminho depois de `-Dexec.args=`.

### Compilar e executar os testes sem os plugins Maven

**Pré-requisito:** Java 8. O `pom.xml` depende de plugins Maven antigos que podem não ser resolvidos fora da rede interna do CNJ. Nesse caso, use o JavaCC standalone para gerar o parser e compilar o projeto:

```bash
cd Objetos1
curl -o /tmp/javacc-5.0.jar https://repo1.maven.org/maven2/net/java/dev/javacc/javacc/5.0/javacc-5.0.jar
mkdir -p target/generated-sources/javacc/loo1/plp/orientadaObjetos1/parser target/classes
java -cp /tmp/javacc-5.0.jar javacc \
  -OUTPUT_DIRECTORY=target/generated-sources/javacc/loo1/plp/orientadaObjetos1/parser \
  src/loo1/plp/orientadaObjetos1/parser/OO1.jj
find src target/generated-sources -name "*.java" > /tmp/sources.txt
javac -encoding UTF-8 -d target/classes @/tmp/sources.txt
```

Execute as suítes de testes de `defer`, `try/catch/finally` e operadores relacionais:

```bash
java -cp target/classes loo1.plp.orientadaObjetos1.testes.TesteDefer
java -cp target/classes loo1.plp.orientadaObjetos1.testes.TesteTryCatchFinally
java -cp target/classes loo1.plp.orientadaObjetos1.testes.TesteOperadoresRelacionais
```

Por fim, execute o exemplo da conta bancária como teste ponta a ponta:

```bash
java -cp target/classes loo1.plp.orientadaObjetos1.parser.OO1Parser \
  ../Testes/TesteOO1_ContaBancaria.txt
```

A saída esperada do exemplo, na ordem, é:

```text
Falha ao processar saque: Saldo insuficiente para o saque
3. [finally] Bloco de confirmacao concluido.
2. [defer] Estado do objeto conta liberado.
1. [defer] Registro de auditoria gravado no log.
```

O exemplo usa `throw`, `try/catch/finally` e `defer`, além de atributos (`int`), procedimentos (`proc`), condições (`then`), atribuições (`:=`) e instanciação de objetos (`:= new Classe`), todos reconhecidos pelo parser atual.
