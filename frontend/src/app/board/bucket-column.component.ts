import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Bucket } from '../bucket.model';
import { Task } from '../task.model';
import { BoardService } from './board.service';
import { TaskCardComponent } from './task-card.component';

@Component({
  selector: 'app-bucket-column',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskCardComponent],
  templateUrl: './bucket-column.component.html',
  styleUrl: './bucket-column.component.css',
})
export class BucketColumnComponent {
  @Input({ required: true }) bucket!: Bucket;
  @Input({ required: true }) allBuckets: Bucket[] = [];
  @Output() changed = new EventEmitter<void>();
  @Output() deleteBucket = new EventEmitter<number>();

  showAddForm = false;
  newTitle = '';
  newDescription = '';

  constructor(private boardService: BoardService) {}

  addTask(): void {
    const title = this.newTitle.trim();
    if (!title) {
      return;
    }
    this.boardService.createTask(title, this.newDescription.trim(), this.bucket.id).subscribe(() => {
      this.newTitle = '';
      this.newDescription = '';
      this.showAddForm = false;
      this.changed.emit();
    });
  }

  toggleDone(task: Task): void {
    this.boardService.updateTask(task.id, { done: !task.done }).subscribe(() => this.changed.emit());
  }

  moveTask(task: Task, bucketId: number): void {
    if (bucketId === task.bucketId) {
      return;
    }
    this.boardService.updateTask(task.id, { bucketId }).subscribe(() => this.changed.emit());
  }

  removeTask(task: Task): void {
    this.boardService.deleteTask(task.id).subscribe(() => this.changed.emit());
  }
}
