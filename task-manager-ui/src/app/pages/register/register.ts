import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../../models/error.model';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {


  private auth = inject(AuthService);
  private router = inject(Router);


  //what user types
  username = signal('');
  email = signal('');
  password = signal('');
  confirmPassword = signal('');

  //what screen shows
  error = signal('');
  loading = signal(false);
  showPassword = signal(false);


  togglePassword(): void {
    this.showPassword.update(v => !v);
  }


  onSubmit(): void {
    //1.check the form ourselves first. If something is wrong show it and stop.
    const problem = this.validate();
    if (problem) {
      this.error.set(problem);
      return;
    }
    this.error.set('');
    this.loading.set(true);



    //2.send to the backend. trim() removes accidental at the ends.
    this.auth.register({
      username: this.username().trim(),
      email: this.email().trim(),
      password: this.password()
    }).subscribe({
      //success: token is already saved by authService, go to the dashboard
      next: () => this.router.navigate(['/dashboard']),
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(this.getMessage(err));
      }
    });
  }
  private validate(): string {
    if (this.username().trim().length < 3) {
      return "Username must be at least 3 characters.";
    }

    //simple email shape check: something@something.something
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email().trim())) {
      return 'Please enter a valid email address.';
    }

    if (this.password().length < 6) {
      return 'Password must be at least 6 characters.';
    }

    if (this.password() !== this.confirmPassword()) {
      return 'Password do not match.';
    }
    return '';
  }

  private getMessage(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }

    const body = err.error as ApiError | null;
    return body?.message ?? 'Registration failed. Please try again.';

  }

}
