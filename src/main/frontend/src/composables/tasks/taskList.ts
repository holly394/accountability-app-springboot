import { api } from 'boot/axios.ts';
import { TaskData } from 'components/dto/TaskData.ts';
import {ref} from "vue";

export function taskRelated() {
  const tasks = ref<TaskData[]>([]);

  const getCurrentUserTaskList = async () => {
    tasks.value = await api.get<TaskData[]>('/tasks')
      .then(res => res.data)
  };

  const forceUpdate = async () => {
    tasks.value = [...tasks.value];
  };

  return { tasks, getCurrentUserTaskList, forceUpdate };
}
