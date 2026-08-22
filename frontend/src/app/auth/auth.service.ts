import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface AuthResponse {
  token: string;
  username: string;
}

const TOKEN_KEY = 'tasktracker_token';
const USERNAME_KEY = 'tasktracker_username';

// Storing the JWT in localStorage (rather than only in memory) is what
// "preserves state" across a page refresh or closing the tab — reopening
// the app finds the token still there and skips straight to the board
// instead of asking you to log in again, until the token itself expires.
@Injectable({ providedIn: 'root' })
export class AuthService {
  username = signal<string | null>(localStorage.getItem(USERNAME_KEY));

  constructor(private http: HttpClient) {}

  register(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/auth/register', { username, password })
      .pipe(tap((res) => this.setSession(res)));
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/auth/login', { username, password })
      .pipe(tap((res) => this.setSession(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
    this.username.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private setSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USERNAME_KEY, res.username);
    this.username.set(res.username);
  }
}
