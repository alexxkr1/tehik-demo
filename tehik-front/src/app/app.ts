import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TaskManager } from './features/task-manager/task-manager';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TaskManager],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('tehik-front');
}
