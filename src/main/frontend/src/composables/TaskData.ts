import { TaskData } from 'components/dto/task/TaskData.ts';
import {TaskEditRequestDto} from "components/dto/task/TaskEditRequestDto.ts";
import {api} from "boot/axios.ts";
import {TaskCalculatorDto} from "components/dto/task/TaskCalculatorDto.ts";
import {TaskStatusDto} from "components/dto/task/TaskStatusDto.ts";
import {TaskStatus} from "components/dto/task/TaskStatus.ts";
import {Page} from "components/paging/Page.ts";

export function taskData() {

  //all other calls go to the TaskController
  const addTask
    = async (description: TaskEditRequestDto): Promise<TaskData> => {

    return (await api.post<TaskData>(`/tasks/add`,
      description)).data;
  }

  const editTaskDescription
    = async (taskId: number,
             description: string): Promise<void | TaskData> => {

    return (await api.put<TaskData>(`/tasks/${taskId}`,
      description)).data;
  }

  const startTask
    = async (taskId: number): Promise<TaskData> => {

    return (await api.post<TaskData>(`/tasks/${taskId}/start`)).data;
  }

  const endTask
    = async (taskId: number): Promise<TaskData> => {

    return (await api.post<TaskData>(`/tasks/${taskId}/end`)).data;
  }

  const deleteTask
    = async (taskId: number): Promise<void> => {

    await api.delete(`/tasks/${taskId}`);
  }

  const processTaskForPartner
    = async (taskId: number,
             newStatus: TaskStatusDto): Promise<TaskData> => {

    return (await api.post<TaskData>(`/tasks/${taskId}/process`,
      newStatus)).data;
  }

  const getTasksByCurrentUserAndStatus
    = async (status: TaskStatus,
             page: number = 0,
             size: number = 20): Promise<Page<TaskData>> => {

    return (await api.get<Page<TaskData>>(`/tasks`, {
      params: {
        statuses: status,
        page: page,
        size: size
      },
      paramsSerializer: {
        indexes: null
      }
    })).data;
  }

  //REMEMBER: cannot send JSON objects through a GET request!
  const getTasksByUserListAndStatus
    = async (usersById: number[],
             status: TaskStatus,
             page: number = 0,
             size: number = 20): Promise<Page<TaskData>> => {

      return (await api.get<Page<TaskData>>(`/tasks`, {
        params: {
          userIds: usersById,
          statuses: status,
          page: page,
          size: size
        },
        paramsSerializer: {
          indexes: null
        }
      })).data;
  }

  const calculatePaymentCompleted
    = async (): Promise<TaskCalculatorDto> => {

    return (await api.get(`/tasks/calculatePaymentCompleted`)).data;
  }

  const calculatePaymentInProgress
    = async (): Promise<TaskCalculatorDto> => {

    return (await api.get(`/tasks/calculatePaymentInProgress`)).data;
  }

  return { startTask, endTask, getTasksByCurrentUserAndStatus,
    getTasksByUserListAndStatus, processTaskForPartner, addTask,
    editTask: editTaskDescription, deleteTask,
    calculatePaymentCompleted, calculatePaymentInProgress };
}
