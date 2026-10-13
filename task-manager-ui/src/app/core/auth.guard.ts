import { inject } from "@angular/core";
import { AuthService } from "./auth.service"
import { CanActivateFn, Router } from "@angular/router";

export const authGuard: CanActivateFn = () => {

    const auth = inject(AuthService); //the service that knows about login
    const router = inject(Router); // the tool that changes pages

    //logged in? -> true( open the page)
    //not logged in?-> go to /login

    return auth.isLoggedIn() ? true : router.createUrlTree(['login']);
}