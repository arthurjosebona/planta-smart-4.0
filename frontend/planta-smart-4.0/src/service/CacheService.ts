interface CacheEntry<T> {
  data: T;
  timestamp: number;
}

interface CacheConfig {
  ttl?: number; 
}

export class CacheService {
  private readonly DEFAULT_TTL = 24 * 60 * 60 * 1000; // 24 horas

  get<T>(key: string): T | null {
    try {
      const cached = localStorage.getItem(key);
      const expiry = localStorage.getItem(`${key}_expiry`);

      if (!cached || !expiry) return null;

      if (Date.now() > parseInt(expiry)) {
        this.clear(key);
        return null;
      }

      const entry: CacheEntry<T> = JSON.parse(cached);
      return entry.data;
    } catch {
      this.clear(key);
      return null;
    }
  }

  set<T>(key: string, data: T, config: CacheConfig = {}): void {
    try {
      const ttl = config.ttl ?? this.DEFAULT_TTL;
      const entry: CacheEntry<T> = { data, timestamp: Date.now() };

      localStorage.setItem(key, JSON.stringify(entry));
      localStorage.setItem(`${key}_expiry`, (Date.now() + ttl).toString());
    } catch (e) {
      console.error(`[CacheService] Erro ao salvar cache para "${key}":`, e);
    }
  }

  clear(key: string): void {
    try {
      localStorage.removeItem(key);
      localStorage.removeItem(`${key}_expiry`);
    } catch (e) {
      console.error(`[CacheService] Erro ao limpar cache para "${key}":`, e);
    }
  }

  clearAll(prefix?: string): void {
    try {
      const keys = Object.keys(localStorage);
      const targets = prefix
        ? keys.filter((k) => k.startsWith(prefix))
        : keys;

      targets.forEach((k) => localStorage.removeItem(k));
    } catch (e) {
      console.error("[CacheService] Erro ao limpar todos os caches:", e);
    }
  }
}