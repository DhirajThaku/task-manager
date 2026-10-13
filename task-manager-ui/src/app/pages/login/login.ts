import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../../models/error.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],  //tools this page's html allowed to use
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  //the tools we need (like @Autowired)
  private auth = inject(AuthService);
  private router = inject(Router);

  //what the user types
  username = signal('');
  password = signal('');

  //what the screens shows
  error = signal('');//red error message, empty=no error
  loading = signal(false); // true while waiting for the backed
  showPassword = signal(false); // false=dots,true=readable text


  togglePassword(): void {
    this.showPassword.update(v => !v); //flip between true and false
  }
  onSubmit(): void {
    this.error.set('');//clear the old error
    this.loading.set(true); // diable the button

    //login(..) only prepare the call; subscribe(..)actually sends it
    this.auth.login({ username: this.username(), password: this.password() })
      .subscribe({
        //backend answered 200: token is already saved by AuthService
        next: () => this.router.navigate(['/dashboard']),

        //backend answered with an error (401,400..)
        error: (err: HttpErrorResponse) => {
          this.loading.set(false);
          this.error.set(this.getMessage(err));
        }
      })
  }

  //picks the best message to show
  private getMessage(err: HttpErrorResponse): string {
    // status === 0 means the browser couldnt connect at all (backend off or CORS problem)
    if (err.status === 0) {
      return 'Cannot reach the server. Is the backend running?';

    }
    const body = err.error as ApiError | null;
    // means use the backend's message if it exist s otherwise use this default
    return body?.message ?? 'Login failed. Please try again.';
  }
}
