import { describe, expect, it } from "vitest";

import { applyTab, formatIndentation } from "./indent";

describe("formatIndentation", () => {
  it("indenta pela profundidade de chaves", () => {
    const entrada = '{\nclasse C {\nint x = 0;\nproc f() {\nskip\n}\n};\nwrite("a")\n}';
    expect(formatIndentation(entrada)).toBe(
      '{\n  classe C {\n    int x = 0;\n    proc f() {\n      skip\n    }\n  };\n  write("a")\n}',
    );
  });

  it("normaliza indentação errada e espaços no fim da linha", () => {
    expect(formatIndentation("{\n        skip   \n   }")).toBe("{\n  skip\n}");
  });

  it("mantém linhas em branco vazias", () => {
    expect(formatIndentation("{\n\n   \nskip\n}")).toBe("{\n\n\n  skip\n}");
  });

  it("trata `} catch (e) {` como fechamento seguido de abertura", () => {
    const entrada = 'try {\nthrow "x"\n} catch (e) {\nwrite(e)\n} finally {\nskip\n}';
    expect(formatIndentation(entrada)).toBe('try {\n  throw "x"\n} catch (e) {\n  write(e)\n} finally {\n  skip\n}');
  });

  it("ignora chaves em strings e comentários", () => {
    const entrada = '{\nwrite("{ aberta")\n// } comentario\nskip\n}';
    expect(formatIndentation(entrada)).toBe('{\n  write("{ aberta")\n  // } comentario\n  skip\n}');
  });

  it("não altera linhas dentro de comentário de bloco", () => {
    const entrada = "{\n/* inicio\n      meio {\n fim */\nskip\n}";
    expect(formatIndentation(entrada)).toBe("{\n  /* inicio\n      meio {\n fim */\n  skip\n}");
  });

  it("é idempotente", () => {
    const entrada = "{\nclasse C {\nint x = 0;\nproc f() {\nskip\n}\n};\nskip\n}";
    const uma = formatIndentation(entrada);
    expect(formatIndentation(uma)).toBe(uma);
  });

  it("não fica com profundidade negativa quando sobra `}`", () => {
    expect(formatIndentation("}\n}\nskip")).toBe("}\n}\nskip");
  });
});

describe("applyTab", () => {
  it("insere indentação na posição do cursor", () => {
    expect(applyTab("abc", 1, 1, false)).toEqual({ value: "a  bc", selectionStart: 3, selectionEnd: 3 });
  });

  it("indenta todas as linhas da seleção", () => {
    const r = applyTab("a\nb\nc", 0, 3, false);
    expect(r.value).toBe("  a\n  b\nc");
    expect(r.selectionStart).toBe(2);
    expect(r.selectionEnd).toBe(7);
  });

  it("não inclui a última linha quando a seleção termina no início dela", () => {
    expect(applyTab("a\nb\nc", 0, 4, false).value).toBe("  a\n  b\nc");
    expect(applyTab("a\nb\nc", 0, 2, false).value).toBe("  a\nb\nc");
  });

  it("não indenta linhas vazias da seleção", () => {
    expect(applyTab("a\n\nb", 0, 4, false).value).toBe("  a\n\n  b");
  });

  it("Shift+Tab remove a indentação da linha atual sem seleção", () => {
    const r = applyTab("  abc", 4, 4, true);
    expect(r).toEqual({ value: "abc", selectionStart: 2, selectionEnd: 2 });
  });

  it("Shift+Tab remove só o que existe e não recua o cursor para antes da linha", () => {
    const r = applyTab("a\n b", 3, 3, true);
    expect(r.value).toBe("a\nb");
    expect(r.selectionStart).toBe(2);
  });

  it("Shift+Tab desindenta todas as linhas da seleção", () => {
    const r = applyTab("  a\n    b\nc", 0, 10, true);
    expect(r.value).toBe("a\n  b\nc");
  });

  it("Shift+Tab em linha sem indentação não altera nada", () => {
    expect(applyTab("abc", 1, 1, true)).toEqual({ value: "abc", selectionStart: 1, selectionEnd: 1 });
  });
});
