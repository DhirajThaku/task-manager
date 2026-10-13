export type TaskPriority = 'URGENT' | 'HIGH' | 'MEDIUM' | 'LOW';
export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

export interface Task {
    id: number;
    title: string;
    description: string | null;
    deadline: string | null;
    priority: TaskPriority;
    status: TaskStatus;
}

//what we send when creating or updating (no id)
export interface TaskRequest {
    title: string;
    description?: string;
    deadline?: string | null;
    priority: TaskPriority;
    status?: TaskStatus;

}