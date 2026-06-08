import { HttpError } from '@error/HttpError';

export class HttpClient {
  private readonly baseURL: string;
  private readonly defaultHeaders: Record<string, string>;

  constructor() {
    this.baseURL = 'http://localhost:8080';
    this.defaultHeaders = {
      'Content-Type': 'application/json',
    };
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const error = await response.json().catch(() => ({
        message: response.statusText,
        status: response.status,
      }));
      throw new HttpError(error.message, error.status ?? response.status);
    }
    if (response.status === 204) {
      return undefined as T;
    }
    return response.json() as Promise<T>;
  }

  async get<T>(url: string): Promise<T> {
    const response = await fetch(`${this.baseURL}${url}`, {
      method: 'GET',
      headers: this.defaultHeaders,
    });
    return this.handleResponse<T>(response);
  }

  async post<T>(url: string, body: unknown): Promise<T> {
    const response = await fetch(`${this.baseURL}${url}`, {
      method: 'POST',
      headers: this.defaultHeaders,
      body: JSON.stringify(body),
    });
    return this.handleResponse<T>(response);
  }

  async put<T>(url: string, body: unknown): Promise<T> {
    const response = await fetch(`${this.baseURL}${url}`, {
      method: 'PUT',
      headers: this.defaultHeaders,
      body: JSON.stringify(body),
    });
    return this.handleResponse<T>(response);
  }

  async patch<T>(url: string, body: unknown): Promise<T> {
    const response = await fetch(`${this.baseURL}${url}`, {
      method: 'PATCH',
      headers: this.defaultHeaders,
      body: JSON.stringify(body),
    });
    return this.handleResponse<T>(response);
  }

  async delete<T>(url: string): Promise<T> {
    const response = await fetch(`${this.baseURL}${url}`, {
      method: 'DELETE',
      headers: this.defaultHeaders,
    });
    return this.handleResponse<T>(response);
  }
}
