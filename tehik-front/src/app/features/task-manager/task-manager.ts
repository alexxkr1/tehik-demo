import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '../../core/services/task.service';
import { BehaviorSubject, finalize, map, Observable, shareReplay, switchMap, take } from 'rxjs';
import { TaskCreationRequestDTO, TaskResponseDTO } from '../../core/models/task.model';
import { Page } from '../../core/models/pagination.interface';

@Component({
  selector: 'app-task-manager',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './task-manager.html',
  styleUrl: './task-manager.scss',
})
export class TaskManager {
  private formBuilder = inject(FormBuilder);
  private taskService = inject(TaskService);

  pageSize = signal(10);
  sortCriteria = signal<'asc' | 'desc'>('desc');

  isLoading = signal(false);
  isEditing = signal(false);

  toggleEditing() {
    this.isEditing.update((value) => !value);
  }

  currentPageSubject = new BehaviorSubject<number>(0);
  currentPage$ = this.currentPageSubject.asObservable();

  form = this.formBuilder.group({
    originalValue: [0, Validators.required],
  });

  tasks$!: Observable<TaskResponseDTO[]>;
  tasksPage$!: Observable<Page<TaskResponseDTO>>;

  ngOnInit() {
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

  onSubmit() {
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

  refreshTasks() {
    this.currentPageSubject.next(this.currentPageSubject.value);
  }
}
