import { TaskData } from 'components/dto/TaskData.ts';
import {taskRelated} from "src/composables/tasks/taskList.ts";
import {api} from "boot/axios.ts";

export function taskListUpdate(thisTask: TaskData) {
  const { tasks, getCurrentUserTaskList } = taskRelated();

  const getUpdatedList = async () => {
    await getCurrentUserTaskList();
    const index = tasks.value.findIndex(
      (task) => task.id === thisTask.id)
    tasks.value.splice(index, 1, thisTask);
    tasks.value = [...tasks.value];
  };

  const removeTask = async () => {
    try{
      await api.delete(`/tasks/${thisTask.id}`);
      const index = tasks.value.findIndex(
        (task) => task.id === thisTask.id
      );
      tasks.value.splice(index, 1);
      tasks.value = [...tasks.value];
    } catch {
      console.log('Error while deleting task', thisTask.id);
    }
  };

  return { getUpdatedList, removeTask };
}
