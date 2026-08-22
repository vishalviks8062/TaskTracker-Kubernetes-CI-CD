import { Task } from './task.model';

export interface Bucket {
  id: number;
  name: string;
  tasks: Task[];
}
