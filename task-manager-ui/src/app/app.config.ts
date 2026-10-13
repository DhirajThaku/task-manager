import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(), //catches unexpected errors (already there by default)
    provideRouter(routes), //"use the directory from app.routes.ts"
    provideHttpClient(withInterceptors([authInterceptor]))
    //allow calling the backend, and put the badge helper on every call
  ]
};
