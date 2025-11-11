import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { TaskCreationRequestDTO, TaskResponseDTO } from "../models/task.model";
import { Page } from "../models/pagination.interface";

@Injectable({
    providedIn: 'root'
})
export class TaskService { 
    private httpService = inject(HttpClient);
    private readonly tasksEndpoint = `${import.meta.env.NG_APP_API_URL}/tasks`;

    constructor() { }

    getTasks(page: number, size: number, sort: string): Observable<Page<TaskResponseDTO>> { 
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sort', sort);
            
        return this.httpService.get<Page<TaskResponseDTO>>(this.tasksEndpoint, { params });
    }

    createTask(value: number): Observable<TaskResponseDTO> {
        const request: TaskCreationRequestDTO = { originalValue: value };
        return this.httpService.post<TaskResponseDTO>(this.tasksEndpoint, request);
    }
}