import { marked, type Tokens } from 'marked'
import katex from 'katex'

const mathInlinePattern = /\$([^$\n]+)\$/g
const mathBlockPattern = /\$\$([^$\n]+)\$\$/g

const IMAGE_EXTENSIONS = ['.png', '.jpg', '.jpeg', '.gif', '.webp', '.svg', '.bmp']

function isImageUrl(url: string): boolean {
  const lowerUrl = url.toLowerCase()
  const ext = lowerUrl.match(/\.[^.]+$/)?.[0] || ''
  return IMAGE_EXTENSIONS.includes(ext)
}

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

function renderMedia({ href, title, text }: Tokens.Image): string {
  const url = href ?? ''
  const alt = title ?? text ?? 'file'

  if (isImageUrl(url)) {
    return `<img src="${url}" alt="${alt}" class="markdown-image" />`
  }

  return `<a href="${url}" class="drive-link" title="${alt}"><i class="fas fa-file"></i> ${alt}</a>`
}

export function parseMarkdown(content: string): string {
  const withMath = renderMath(content)

  const renderer = new marked.Renderer()
  renderer.image = renderMedia

  marked.setOptions({ renderer })

  const result = marked.parse(withMath) as string
  return result
}

export function truncateMarkdown(html: string, maxLength: number = 300): string {
  const textOnly = html.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim()
  
  if (textOnly.length <= maxLength) {
    return html
  }

  const truncatedText = textOnly.slice(0, maxLength)
  const lastSpace = truncatedText.lastIndexOf(' ')
  const cutPoint = lastSpace > maxLength * 0.8 ? lastSpace : maxLength
  
  const words = truncatedText.slice(0, cutPoint).split(' ')
  words.pop()

  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = html
  
  let charCount = 0
  const truncateNode = (node: Node): boolean => {
    if (node.nodeType === Node.TEXT_NODE) {
      const text = node.textContent || ''
      if (charCount + text.length > maxLength) {
        const remaining = maxLength - charCount
        node.textContent = text.slice(0, remaining) + '...'
        return true
      }
      charCount += text.length
      return false
    }
    
    if (node.nodeType === Node.ELEMENT_NODE) {
      const el = node as Element
      if (el.classList.contains('media-embed')) {
        charCount += 50
        return charCount > maxLength
      }
      
      const children = Array.from(node.childNodes)
      for (const child of children) {
        if (truncateNode(child)) {
          let sibling = child.nextSibling
          while (sibling) {
            const next = sibling.nextSibling
            node.removeChild(sibling)
            sibling = next
          }
          const ellipsis = document.createTextNode('...')
          node.appendChild(ellipsis)
          return true
        }
      }
    }
    return false
  }

  truncateNode(tempDiv)
  
  return tempDiv.innerHTML
}

export function stripMarkdownToText(html: string): string {
  return html.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim()
}

export function extractMediaUrls(content: string): Array<{ url: string; isImage: boolean; title: string }> {
  const urlRegex = /!\[([^\]]*)\]\(([^)]+)\)/g
  const urls: Array<{ url: string; isImage: boolean; title: string }> = []
  let match
  
  while ((match = urlRegex.exec(content)) !== null) {
    const title = match[1] ?? ''
    const url = match[2] ?? ''
    urls.push({ url, isImage: isImageUrl(url), title })
  }
  
  return urls
}
