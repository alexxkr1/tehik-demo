import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '@/core/services/task.service';
import {
  BehaviorSubject,
  finalize,
  map,
  Observable,
  shareReplay,
  Subscription,
  switchMap,
  take,
} from 'rxjs';
import { TaskResponseDTO, TaskStatus } from '@/core/models/task.model';
import { Page } from '@/core/models/pagination.interface';
import { TaskWebSocketService } from '@/core/services/task.websocket.service';

@Component({
  selector: 'app-task-manager',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './task-manager.html',
  styleUrl: './task-manager.scss',
})
export class TaskManager {
  private formBuilder = inject(FormBuilder);
  private taskService = inject(TaskService);
  private wsSub?: Subscription;

  constructor(private taskWs: TaskWebSocketService) {}

  pageSize = signal(10);
  sortCriteria = signal<'asc' | 'desc'>('desc');

  isLoading = signal(false);
  isEditing = signal(false);

  currentPageSubject = new BehaviorSubject<number>(0);
  currentPage$ = this.currentPageSubject.asObservable();

  form = this.formBuilder.group({
    originalValue: [0, Validators.required],
  });

  tasks$!: Observable<TaskResponseDTO[]>;
  tasksPage$!: Observable<Page<TaskResponseDTO>>;

  ngOnInit() {
    this.taskWs.connect();
    this.taskWs.updates$.subscribe((update) => {
      if (update) {
        this.tasks$ = this.tasks$.pipe(
          take(1),
          map((tasks) =>
            tasks.map((task) =>
              task.id === update.id ? { ...task, ...update } : task
            )
          )
        );

        if (update.status === TaskStatus.DONE) {
          this.taskWs.unsubscribeFromTask(update.id);
        }
      }
    });

    this.tasksPage$ = this.currentPage$.pipe(
      switchMap((page) => {
        this.isLoading.set(true);
        const size = this.pageSize();
        const sort = this.sortCriteria();

        return this.taskService.getTasks(page, size, sort).pipe(
          finalize(() => {
            this.isLoading.set(false);
          })
        );
      }),

      shareReplay({ bufferSize: 1, refCount: true })
    );

    this.tasks$ = this.tasksPage$.pipe(map((page) => page.content));
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    this.taskWs.disconnect();
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isLoading.set(true);

    this.taskService
      .createTask(this.form.value.originalValue!)
      .pipe(
        take(1),
        finalize(() => {
          this.isLoading.set(false);
          this.currentPageSubject.next(0);
        })
      )
      .subscribe({
        next: (res) => {
          this.taskWs.subscribeToTask(res.id);
          this.currentPageSubject.next(0);
          this.isEditing.set(false);
        },
      });
  }

  goToPage(page: number): void {
    if (page >= 0) {
      this.currentPageSubject.next(page);
    }
  }

  nextPage(totalPages: number): void {
    const next = this.currentPageSubject.value + 1;
    if (next < totalPages) {
      this.currentPageSubject.next(next);
    }
  }

  prevPage(): void {
    const prev = this.currentPageSubject.value - 1;
    if (prev >= 0) {
      this.currentPageSubject.next(prev);
    }
  }

  refreshTasks(): void {
    this.currentPageSubject.next(this.currentPageSubject.value);
  }

  toggleEditing(): void {
    this.isEditing.update((value) => !value);
  }
}
