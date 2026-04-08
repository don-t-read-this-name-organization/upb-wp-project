const ACCESS_TOKEN_KEY = 'accessToken'
const REFRESH_TOKEN_KEY = 'refreshToken'
const LEGACY_TOKEN_KEY = 'token'

function looksLikeJwt(token: string): boolean {
  // Basic shape check: header.payload.signature
  return token.split('.').length === 3
}

function getCookie(name: string): string | null {
  const cookies = document.cookie ? document.cookie.split('; ') : []
  for (const cookie of cookies) {
    const [key, ...parts] = cookie.split('=')
    if (key === name) {
      return decodeURIComponent(parts.join('='))
    }
  }
  return null
}

export function getAccessToken(): string | null {
  const accessToken = localStorage.getItem(ACCESS_TOKEN_KEY)
  if (accessToken && looksLikeJwt(accessToken)) {
    return accessToken
  }

  const legacyToken = localStorage.getItem(LEGACY_TOKEN_KEY)
  if (legacyToken && looksLikeJwt(legacyToken)) {
    return legacyToken
  }

  return null
}

export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

export function setAuthTokens(accessToken: string, refreshToken?: string): void {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
  localStorage.setItem(LEGACY_TOKEN_KEY, accessToken)
  if (refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
  }
}

export function clearAuthTokens(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem(LEGACY_TOKEN_KEY)
}

function isApiRequest(input: RequestInfo | URL): boolean {
  const raw = typeof input === 'string' ? input : input instanceof URL ? input.toString() : input.url
  const url = new URL(raw, window.location.origin)
  return url.pathname.startsWith('/api/')
}

function shouldAttachCsrf(method: string, url: string): boolean {
  const upper = method.toUpperCase()
  if (upper === 'GET' || upper === 'HEAD' || upper === 'OPTIONS') {
    return false
  }
  return !url.startsWith('/api/auth/')
}

let refreshPromise: Promise<string | null> | null = null

async function refreshAccessToken(rawFetch: typeof window.fetch): Promise<string | null> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    return null
  }
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const response = await rawFetch('/api/auth/refresh', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken }),
          credentials: 'same-origin',
        })
        if (!response.ok) {
          return null
        }
        const data = await response.json()
        if (!data?.accessToken) {
          return null
        }
        setAuthTokens(data.accessToken, data.refreshToken)
        return data.accessToken as string
      } catch {
        return null
      } finally {
        refreshPromise = null
      }
    })()
  }
  return refreshPromise
}

export function setupApiClient(): void {
  const rawFetch = window.fetch.bind(window)

  window.fetch = async (input: RequestInfo | URL, init?: RequestInit): Promise<Response> => {
    if (!isApiRequest(input)) {
      return rawFetch(input, init)
    }

    const requestUrl =
      typeof input === 'string' ? new URL(input, window.location.origin).pathname : input instanceof URL ? input.pathname : new URL(input.url, window.location.origin).pathname
    const method = (init?.method || (input instanceof Request ? input.method : 'GET')).toUpperCase()
    const headers = new Headers(init?.headers || (input instanceof Request ? input.headers : undefined))

    const accessToken = getAccessToken()
    if (accessToken) {
      headers.set('Authorization', `Bearer ${accessToken}`)
    }
    if (shouldAttachCsrf(method, requestUrl)) {
      const csrfToken = getCookie('XSRF-TOKEN')
      if (csrfToken) {
        headers.set('X-CSRF-TOKEN', csrfToken)
      }
    }

    const requestInit: RequestInit = {
      ...init,
      headers,
      credentials: init?.credentials || 'same-origin',
    }

    const response = await rawFetch(input, requestInit)
    if (
      response.status === 401 &&
      requestUrl !== '/api/auth/refresh' &&
      !headers.has('X-Auth-Retried') &&
      getRefreshToken()
    ) {
      const refreshedToken = await refreshAccessToken(rawFetch)
      if (!refreshedToken) {
        clearAuthTokens()
        return response
      }

      headers.set('Authorization', `Bearer ${refreshedToken}`)
      headers.set('X-Auth-Retried', '1')
      return rawFetch(input, { ...requestInit, headers })
    }

    return response
  }
}
