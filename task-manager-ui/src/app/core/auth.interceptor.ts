import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { inject } from "@angular/core";
import { catchError, throwError } from "rxjs";

export const authInterceptor: HttpInterceptorFn = (req, next) => {

    //1.get the authservice so we can ask it for the token
    const auth = inject(AuthService);
    const token = auth.getToken();

    //2.if we have a token, make a copy of the request with the badge added.
    //(angular does'nt allow editing the original, so we copy it.)
    const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;


    //3.send the request on to the backend, and watch the answer that comes back
    return next(authReq).pipe(
        catchError((err: HttpErrorResponse) => {
            //4. If the backend says 401(not logged in) log the user out.
            //exception: login and register calls(URL contains /auth/),
            //because there 401 only means "wrong password". 

            if (err.status === 401 && !req.url.includes('/auth/')) {
                auth.logout();
            }

            //5.pass the error along so the page can still show a message
            return throwError(() => err);
        })
    );
};