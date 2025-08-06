import { api } from 'boot/axios.ts';
import { Ref, toRef} from "vue";
import {TaskData} from "components/dto/TaskData.ts";
import {taskListUpdate} from "src/composables/tasks/taskListUpdate.ts";

export function editTask(status: string | Ref<string> , taskId: number | Ref<number>) {
  const thisTaskId = toRef(taskId);
  const thisTaskStatus = toRef(status);

  const updateStatus = async () => {
    await api.post<TaskData>(`/tasks/${thisTaskId.value}/update-status`,
      thisTaskStatus.value)
      .then(res => {
        const { getUpdatedList } = taskListUpdate(res.data);
        getUpdatedList();
      })
      .catch(error => {
        console.log(error);
      })
  };

  return { updateStatus };
}
