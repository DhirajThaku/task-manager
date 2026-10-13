import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../../core/auth.service';
import { TaskService } from '../../core/task.service';
import { Task, TaskPriority, TaskRequest, TaskStatus } from '../../models/task.model';
import { ApiError } from '../../models/error.model';

type View = 'all' | 'schedule';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, FormsModule],   // FormsModule is needed for the form boxes
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private auth = inject(AuthService);
  private taskService = inject(TaskService);

  username = this.auth.getUsername();

  // ---------- list and tabs ----------
  view = signal<View>('all');
  tasks = signal<Task[]>([]);
  scheduled = signal<Task[]>([]);
  loading = signal(true);
  error = signal('');

  visibleTasks = computed(() =>
    this.view() === 'all' ? this.tasks() : this.scheduled()
  );

  total = computed(() => this.tasks().length);
  completed = computed(() => this.tasks().filter(t => t.status === 'COMPLETED').length);
  pending = computed(() => this.total() - this.completed());

  // ---------- form ----------
  // the choices shown in the two dropdowns
  priorities: TaskPriority[] = ['URGENT', 'HIGH', 'MEDIUM', 'LOW'];
  statuses: TaskStatus[] = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];

  formOpen = signal(false);              // is the pop-up visible?
  editingId = signal<number | null>(null); // null = adding, a number = editing that task
  saving = signal(false);
  formError = signal('');

  // what the user types in the form
  fTitle = signal('');
  fDescription = signal('');
  fDeadline = signal('');                // text like "2026-10-12T18:00"
  fPriority = signal<TaskPriority>('MEDIUM');
  fStatus = signal<TaskStatus>('PENDING');

  ngOnInit(): void {
    this.loadAll();
  }

  // ---------- tabs and loading ----------
  setView(tab: View): void {
    this.view.set(tab);
    if (tab === 'schedule') {
      this.loadSchedule();
    } else {
      this.loadAll();
    }
  }

  loadAll(): void {
    this.loading.set(true);
    this.error.set('');

    this.taskService.getAll().subscribe({
      next: list => {
        this.tasks.set(list);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(this.getMessage(err));
      }
    });
  }

  loadSchedule(): void {
    this.loading.set(true);
    this.error.set('');

    this.taskService.getSchedule().subscribe({
      next: list => {
        this.scheduled.set(list);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(this.getMessage(err));
      }
    });

    // also refresh the full list so the counts stay correct
    this.taskService.getAll().subscribe({
      next: list => this.tasks.set(list)
    });
  }

  // reload whichever tab is open (used after save / done)
  refresh(): void {
    if (this.view() === 'schedule') {
      this.loadSchedule();
    } else {
      this.loadAll();
    }
  }

  // ---------- form: open and close ----------
  //showing the defaults values in the form where all fileds are mapped with the signals
  openAdd(): void {
    this.editingId.set(null);          // null means "adding a new task"
    this.fTitle.set('');
    this.fDescription.set('');
    this.fDeadline.set('');
    this.fPriority.set('MEDIUM');
    this.fStatus.set('PENDING');
    this.formError.set('');
    this.formOpen.set(true);
  }


  //showing the defaults value in the form for edit
  openEdit(task: Task): void {
    this.editingId.set(task.id);       // remember WHICH task we are editing
    this.fTitle.set(task.title);
    this.fDescription.set(task.description ?? '');
    // the date box needs "yyyy-MM-ddTHH:mm", so we cut the seconds off
    this.fDeadline.set(task.deadline ? task.deadline.slice(0, 16) : '');
    this.fPriority.set(task.priority);
    this.fStatus.set(task.status);
    this.formError.set('');
    this.formOpen.set(true);
  }

  closeForm(): void {
    this.formOpen.set(false);
  }

  // ---------- form: save ----------
  saveTask(): void {
    // 1. check the form in the browser first
    const title = this.fTitle().trim();
    if (!title) {
      this.formError.set('Title is required.');
      return;
    }
    if (title.length > 150) {
      this.formError.set('Title must be 150 characters or fewer.');
      return;
    }
    if (this.fDescription().length > 1000) {
      this.formError.set('Description must be 1000 characters or fewer.');
      return;
    }

    // 2. build the JSON the backend expects (your TaskRequest)
    const body: TaskRequest = {
      title,
      description: this.fDescription().trim() || undefined,
      deadline: this.fDeadline() || null,     // empty box = no deadline
      priority: this.fPriority(),
      status: this.fStatus()
    };

    // 3. adding or editing?
    const id = this.editingId();
    const request = id === null
      ? this.taskService.create(body)         // POST
      : this.taskService.update(id, body);    // PUT

    this.saving.set(true);
    this.formError.set('');

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.closeForm();
        this.refresh();
      },
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.formError.set(this.getMessage(err));
      }
    });
  }

  // ---------- one-click actions ----------
  markDone(task: Task): void {
    // PUT needs the whole task, so we resend its values with status COMPLETED
    const body: TaskRequest = {
      title: task.title,
      description: task.description ?? undefined,
      deadline: task.deadline,
      priority: task.priority,
      status: 'COMPLETED'
    };

    this.taskService.update(task.id, body).subscribe({
      next: () => this.refresh(),
      error: (err: HttpErrorResponse) => this.error.set(this.getMessage(err))
    });
  }

  deleteTask(task: Task): void {
    if (!confirm(`Delete "${task.title}"?`)) {
      return;
    }

    this.taskService.delete(task.id).subscribe({
      next: () => {
        this.tasks.update(list => list.filter(t => t.id !== task.id));
        this.scheduled.update(list => list.filter(t => t.id !== task.id));
      },
      error: (err: HttpErrorResponse) => this.error.set(this.getMessage(err))
    });
  }

  logout(): void {
    this.auth.logout();
  }

  isOverdue(task: Task): boolean {
    return !!task.deadline
      && task.status !== 'COMPLETED'
      && new Date(task.deadline).getTime() < Date.now();
  }

  private getMessage(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }
    const body = err.error as ApiError | null;
    return body?.message ?? 'Something went wrong. Please try again.';
  }
}