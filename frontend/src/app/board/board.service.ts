import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bucket } from '../bucket.model';
import { Task } from '../task.model';

const API_BASE = '/api';

@Injectable({ providedIn: 'root' })
export class BoardService {
  constructor(private http: HttpClient) {}

  getBoard(): Observable<Bucket[]> {
    return this.http.get<Bucket[]>(`${API_BASE}/board`);
  }

  createBucket(name: string): Observable<Bucket> {
    return this.http.post<Bucket>(`${API_BASE}/buckets`, { name });
  }

  deleteBucket(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/buckets/${id}`);
  }

  createTask(title: string, description: string, bucketId: number): Observable<Task> {
    return this.http.post<Task>(`${API_BASE}/tasks`, { title, description, bucketId });
  }

  updateTask(id: number, changes: Partial<Pick<Task, 'title' | 'description' | 'done' | 'bucketId'>>): Observable<Task> {
    return this.http.put<Task>(`${API_BASE}/tasks/${id}`, changes);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/tasks/${id}`);
  }
}
