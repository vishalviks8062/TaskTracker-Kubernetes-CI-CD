import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Bucket } from '../bucket.model';
import { Task } from '../task.model';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-card.component.html',
  styleUrl: './task-card.component.css',
})
export class TaskCardComponent {
  @Input({ required: true }) task!: Task;
  @Input({ required: true }) buckets: Bucket[] = [];
  @Output() toggleDone = new EventEmitter<void>();
  @Output() move = new EventEmitter<number>();
  @Output() remove = new EventEmitter<void>();

  onBucketChange(event: Event): void {
    const bucketId = Number((event.target as HTMLSelectElement).value);
    this.move.emit(bucketId);
  }
}
