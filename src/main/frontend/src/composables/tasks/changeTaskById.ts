import { api } from 'boot/axios.ts';
import {Ref, toRef} from "vue";
import {TaskData} from "components/dto/TaskData.ts";
import {taskListUpdate} from "src/composables/tasks/taskListUpdate.ts";

export function changeTaskById(taskId: number | Ref<number>) {

  const thisTaskId = toRef(taskId);

  const startTask = async () => {
    await api.post<TaskData>(`/tasks/${thisTaskId.value}/start`, thisTaskId.value)
      .then(response => {
        console.log('Task started with ID:', response.data.id);
        const { getUpdatedList } = taskListUpdate(response.data);
        getUpdatedList();
      })
      .catch(error => {
        console.log(error);
      })
  }

  const endTask = async () => {
    await api.post(`/tasks/${thisTaskId.value}/end`, thisTaskId.value)
      .then(response => {
        console.log('Task ended with ID:', response.data.id);
        const { getUpdatedList } = taskListUpdate(response.data);
        getUpdatedList();
      })
      .catch(error => {
        console.log(error);
      })
  }

  return { startTask, endTask };
}
