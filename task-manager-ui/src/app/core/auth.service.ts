import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { AuthResponse, LoginRequest, RegisterRequest } from "../models/auth.model";
import { Observable, tap } from "rxjs";
import { API_URL } from "./api.config";

const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'username';


//providedIn:'root' angular creates ONE shared instance for the whole app

@Injectable({ providedIn: 'root' })
export class AuthService {
    //inject() asks angular to hand us the tools we need(like @Autowired)
    private http = inject(HttpClient);//sends HTTP requests
    private router = inject(Router);  //changes the page

    login(data: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${API_URL}/auth/login`, data)
            //tap="when the response arrives , also do this"-> save the token
            .pipe(tap(res => this.saveSession(res)));
    }

    register(data: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${API_URL}/auth/register`, data)
            .pipe(tap(res => this.saveSession(res)));  //register also returns the token

    }


    logout(): void {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        this.router.navigate(['/login']); //send the user to login page
    }

    getToken(): string | null {
        return localStorage.getItem(TOKEN_KEY);
    }

    getUsername(): string | null {
        return localStorage.getItem(USER_KEY);
    }

    isLoggedIn(): boolean {
        const token = this.getToken();
        if (!token) {
            return false;
        }
        if (this.isExpired(token)) {
            // the token is dead, so remove it instead of leaving it in storage
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
            return false;
        }
        return true;
    }

    private saveSession(res: AuthResponse): void {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, res.username);
    }

    private isExpired(token: string): boolean {
        try {
            const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
            const payload = JSON.parse(atob(base64));
            return payload.exp * 1000 < Date.now(); //convert seconds to miliseconds
        } catch {
            return true; //unredable token
        }
    }
}