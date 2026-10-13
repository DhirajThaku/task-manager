import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { API_URL } from "./api.config";
import { Observable } from "rxjs";
import { Task, TaskRequest } from "../models/task.model";


@Injectable({ providedIn: 'root' })
export class TaskService {
    private http = inject(HttpClient);
    private baseUrl = `${API_URL}/tasks`;//http://localhost:8080/api/tasks


    //GET /api/tasks => my tasks

    getAll(): Observable<Task[]> {
        return this.http.get<Task[]>(this.baseUrl);
    }

    //GET /api/tasks/schedule ->my tasks ranked by min heap
    getSchedule(): Observable<Task[]> {
        return this.http.get<Task[]>(`${this.baseUrl}/schedule`);
    }

    //POST/api/tasks ->create (backend returns 201 + the new task)
    create(data: TaskRequest): Observable<Task> {
        return this.http.post<Task>(this.baseUrl, data);
    }

    //PUT /api/tasks/{id} ->update
    update(id: number, data: TaskRequest): Observable<Task> {
        return this.http.put<Task>(`${this.baseUrl}/${id}`, data);
    }


    //DELETE /api/tasks/{id} ->backend returns 204 (no body)
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

}