# Proposta de Projeto — Rascunho

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
- **Garantia de Finalização (`finally`):** Bloco cuja execução é garantida obrigatoriamente antes de concluir a instrução `try`, independentemente da ocorrência de exceções.
- **Comando de Adiamento (`defer`):** Agenda um comando para ser executado obrigatoriamente no momento de saída do método atual. Múltiplos comandos `defer` declarados dentro do método são organizados em uma pilha LIFO.

## 3. Exemplo de Código na Linguagem Estendida

```java
{
  classe ContaBancaria {
    var saldo = 100;

    metodo sacar(valor) {
      // defer: executado obrigatoriamente ao finalizar a execução do método (ordem LIFO)
      defer write("1. [defer] Registro de auditoria gravado no log.");
      defer write("2. [defer] Estado do objeto conta liberado.");

      try {
        if (valor > saldo) {
          throw "Saldo insuficiente para o saque";
        }
        saldo = saldo - valor;
        write("Saque efetuado com sucesso. Saldo restante: " + saldo);
      } catch (erro) {
        write("Falha ao processar saque: " + erro);
      } finally {
        // finally: executado obrigatoriamente ao final da estrutura try/catch
        write("3. [finally] Bloco de confirmacao concluido.");
      }
    }
  };

  var minhaConta = new ContaBancaria();
  minhaConta.sacar(150)
}
```

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
TipoPrimitivo ::= TipoPrimitivo
```
