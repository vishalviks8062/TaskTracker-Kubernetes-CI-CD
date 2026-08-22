import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Bucket } from '../bucket.model';
import { BoardService } from './board.service';
import { BucketColumnComponent } from './bucket-column.component';

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [CommonModule, FormsModule, BucketColumnComponent],
  templateUrl: './board.component.html',
  styleUrl: './board.component.css',
})
export class BoardComponent implements OnInit {
  buckets: Bucket[] = [];
  loading = false;
  error = '';
  newBucketName = '';

  constructor(private boardService: BoardService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading = true;
    this.error = '';
    this.boardService.getBoard().subscribe({
      next: (buckets) => {
        this.buckets = buckets;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load your board.';
        this.loading = false;
      },
    });
  }

  addBucket(): void {
    const name = this.newBucketName.trim();
    if (!name) {
      return;
    }
    this.boardService.createBucket(name).subscribe(() => {
      this.newBucketName = '';
      this.refresh();
    });
  }

  deleteBucket(id: number): void {
    this.boardService.deleteBucket(id).subscribe(() => this.refresh());
  }
}
