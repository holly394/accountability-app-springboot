import { TaskData } from 'components/dto/TaskData.ts';
import {TaskEditRequestDto} from "components/dto/TaskEditRequestDto.ts";
import {api} from "boot/axios.ts";
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";
import {TaskStatusDto} from "components/dto/TaskStatusDto.ts";
import {TaskStatus} from "components/dto/TaskStatus.ts";
import {Page} from "components/paging/Page.ts";

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

  const processTaskForPartner = async (taskId: number, newStatus: TaskStatusDto): Promise<TaskData> => {
    return (await api.post<TaskData>(`/tasks/${taskId}/process`, newStatus)).data;
  }

  const deleteTask = async (taskId: number): Promise<void> => {
    await api.delete(`/tasks/${taskId}`);
  }

  const getTasksByUserId = async (idList: number[]): Promise<Page<TaskData>> => {
    return (await api.get<Page<TaskData>>(`/tasks`, {
      params: {
        userIds: idList
      },
      paramsSerializer: {
        indexes: null
      }
    })).data;
  }

  const getTasks = async (): Promise<Page<TaskData>> => {
    return (await api.get<Page<TaskData>>(`/tasks`)).data;
  }

  const getTasksForStatus = async (status: TaskStatus): Promise<Page<TaskData>> => {
    return (await api.get<Page<TaskData>>(`/tasks`, {
      params: {
        statuses: status,
      }
    })).data;
  }

  const calculatePaymentCompleted = async (): Promise<TaskCalculatorDto> => {
    return (await api.get(`/tasks/calculatePaymentCompleted`)).data;
  }

  const calculatePaymentInProgress = async (): Promise<TaskCalculatorDto> => {
    return (await api.get(`/tasks/calculatePaymentInProgress`)).data;
  }

  return { getTasks, getTasksForStatus, startTask, endTask, getTasksByUserId,
    updateTaskStatus: processTaskForPartner, addTask, editTask: editTaskDescription, deleteTask,
    calculatePaymentCompleted, calculatePaymentInProgress};
}
