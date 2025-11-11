export enum TaskStatus {
  PENDING = 'PENDING',
  DONE = 'DONE',
  FAILED = 'FAILED',
}

export interface TaskResponseDTO {
  id: number;
  originalValue: number;
  resultValue: number | null;
  status: TaskStatus;
  createdAt: string;
}

export interface TaskCreationRequestDTO {
  originalValue: number;
}