import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';

export const routes: Routes = [
    { path: 'login', component: Login }, //login shows the login page
    { path: 'register', component: Register },  //register shows the register page
    { path: 'dashboard', component: Dashboard, canActivate: [authGuard] }, //guarded
    { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, //empty URL goes to dashboard
    { path: '**', redirectTo: 'dashboard' }//any unkown URL goes to dashboard

];
