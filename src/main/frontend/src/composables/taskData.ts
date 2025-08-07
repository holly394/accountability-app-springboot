import { TaskData } from 'components/dto/TaskData.ts';
import {TaskEditRequestDto} from "components/dto/TaskEditRequestDto.ts";
import {api} from "boot/axios.ts";
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";
import {TaskStatusDto} from "components/dto/TaskStatusDto.ts";

export function taskData() {
  const addTask = async (description: TaskEditRequestDto): Promise<TaskData> => {
    return (await api.post<TaskData>(`/tasks/add`, description)).data;
  }

  const editTaskDescription = async (taskId: number, description: string): Promise<void | TaskData> => {
    return (await api.put<TaskData>(`/tasks/${taskId}`,description)).data;
  }

  const startTask = async (taskId: number): Promise<TaskData> => {
    return (await api.post<TaskData>(`/tasks/${taskId}/start`)).data;
  }

  const endTask = async (taskId: number): Promise<TaskData> => {
    return (await api.post<TaskData>(`/tasks/${taskId}/end`)).data;
  }

  const updateTaskStatus = async (taskId: number, newStatus: TaskStatusDto): Promise<TaskData> => {
    return (await api.post<TaskData>(`/tasks/${taskId}/update-status`, newStatus)).data;
  }

  const deleteTask = async (taskId: number): Promise<void> => {
    await api.delete(`/tasks/${taskId}`);
  }

  const getTasksCurrentUser = async (): Promise<TaskData[]> => {
    return (await api.get<TaskData[]>(`/tasks`)).data;
  }

  const getTasksByUserId = async (idList: number[]): Promise<TaskData[]> => {
    return (await api.get<TaskData[]>(`/tasks/get-tasks-by-partner-id?ids=${idList}`)).data;
  }

  const calculatePaymentCompleted = async (): Promise<TaskCalculatorDto> => {
    return (await api.get(`/tasks/calculatePaymentCompleted`)).data;
  }

  const calculatePaymentInProgress = async (): Promise<TaskCalculatorDto> => {
    return (await api.get(`/tasks/calculatePaymentInProgress`)).data;
  }

  return { startTask, endTask, getTasks: getTasksCurrentUser, getTasksByUserId,
    updateTaskStatus, addTask, editTask: editTaskDescription, deleteTask,
    calculatePaymentCompleted, calculatePaymentInProgress};
}
