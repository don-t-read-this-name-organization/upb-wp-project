import { marked } from 'marked'
import katex from 'katex'

const mathInlinePattern = /\$([^$\n]+)\$/g
const mathBlockPattern = /\$\$([^$\n]+)\$\$/g

function renderMath(content: string): string {
  content = content.replace(mathBlockPattern, (_match, formula) => {
    try {
      return `<div class="math-block">${katex.renderToString(formula, { displayMode: true })}</div>`
    } catch {
      return `<div class="math-error">${formula}</div>`
    }
  })

  content = content.replace(mathInlinePattern, (_match, formula) => {
    try {
      return katex.renderToString(formula, { displayMode: false })
    } catch {
      return `<span class="math-error">${formula}</span>`
    }
  })

  return content
}

export function parseMarkdown(content: string): string {
  const withMath = renderMath(content)
  return marked.parse(withMath) as string
}
