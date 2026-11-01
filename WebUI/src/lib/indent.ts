export const INDENT = "  ";

/**
 * Reindenta o código pela profundidade de chaves `{}`. Chaves dentro de strings e
 * comentários são ignoradas, e linhas dentro de um comentário de bloco são mantidas como estão.
 */
export function formatIndentation(code: string): string {
  let depth = 0;
  let inBlockComment = false;

  return code
    .split("\n")
    .map((rawLine) => {
      if (inBlockComment) {
        // Linha dentro de comentário de bloco: mantida como está; só conta as chaves depois do `*/`.
        const end = rawLine.indexOf("*/");
        if (end === -1) return rawLine;
        inBlockComment = false;
        const rest = scanLine(rawLine.slice(end + 2).trim());
        depth = Math.max(0, depth + rest.opens - rest.closes);
        return rawLine;
      }

      const line = rawLine.trim();
      if (line === "") return "";
      const { opens, closes, leadingCloses } = scanLine(line);
      const lineDepth = Math.max(0, depth - leadingCloses);
      depth = Math.max(0, depth + opens - closes);
      return INDENT.repeat(lineDepth) + line;
    })
    .join("\n");

  function scanLine(line: string) {
    let opens = 0;
    let closes = 0;
    let leadingCloses = 0;
    let seenCode = false;
    let inString = false;

    for (let i = 0; i < line.length; i++) {
      const char = line[i];

      if (inString) {
        if (char === "\\") i++;
        else if (char === '"') inString = false;
        continue;
      }
      if (char === '"') {
        inString = true;
        seenCode = true;
      } else if (char === "/" && line[i + 1] === "/") {
        break;
      } else if (char === "/" && line[i + 1] === "*") {
        const end = line.indexOf("*/", i + 2);
        if (end === -1) {
          inBlockComment = true;
          break;
        }
        i = end + 1;
      } else if (char === "{") {
        opens++;
        seenCode = true;
      } else if (char === "}") {
        closes++;
        if (!seenCode) leadingCloses++;
      } else if (!/\s/.test(char)) {
        seenCode = true;
      }
    }

    return { opens, closes, leadingCloses };
  }
}

export interface EditResult {
  value: string;
  selectionStart: number;
  selectionEnd: number;
}

/**
 * Comportamento da tecla Tab em um editor de código.
 * - Sem seleção: insere uma indentação na posição do cursor (Shift+Tab remove da linha atual).
 * - Com seleção: indenta (ou, com Shift, desindenta) todas as linhas tocadas pela seleção.
 */
export function applyTab(value: string, start: number, end: number, shift: boolean): EditResult {
  if (start === end && !shift) {
    return {
      value: value.slice(0, start) + INDENT + value.slice(end),
      selectionStart: start + INDENT.length,
      selectionEnd: start + INDENT.length,
    };
  }

  const firstLineStart = value.lastIndexOf("\n", start - 1) + 1;
  // Seleção que termina logo no início de uma linha não inclui essa linha.
  const lastChar = end > start && value[end - 1] === "\n" ? end - 1 : end;
  const nextBreak = value.indexOf("\n", lastChar);
  const blockEnd = nextBreak === -1 ? value.length : nextBreak;

  const lines = value.slice(firstLineStart, blockEnd).split("\n");
  let startShift = 0;
  let totalShift = 0;

  const edited = lines.map((line, index) => {
    let delta: number;
    let next: string;
    if (shift) {
      const removed = line.startsWith(INDENT)
        ? INDENT.length
        : line.startsWith("\t")
          ? 1
          : line.startsWith(" ")
            ? 1
            : 0;
      next = line.slice(removed);
      delta = -removed;
    } else {
      next = line === "" ? line : INDENT + line;
      delta = next.length - line.length;
    }
    if (index === 0) startShift = delta;
    totalShift += delta;
    return next;
  });

  // Ao desindentar, o início da seleção não pode recuar para antes do começo da linha.
  const adjustedStart = shift ? start - Math.min(-startShift, start - firstLineStart) : start + startShift;

  return {
    value: value.slice(0, firstLineStart) + edited.join("\n") + value.slice(blockEnd),
    selectionStart: adjustedStart,
    selectionEnd: Math.max(adjustedStart, end + totalShift),
  };
}
