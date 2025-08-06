<script setup lang="ts">
import { onMounted } from 'vue';
import { QMarkupTable } from 'quasar';
import { taskRelated } from 'src/composables/tasks/taskList.ts'
import { changeTaskById } from 'src/composables/tasks/changeTaskById.ts';
import {taskListUpdate} from "src/composables/tasks/taskListUpdate.ts";
import {TaskData} from "components/dto/TaskData.ts";

const { tasks, getCurrentUserTaskList } = taskRelated();

defineOptions({
  name: 'TableTasksPlanned'
});

onMounted(async () => {
  await getCurrentUserTaskList();
});

async function deleteTask(task: TaskData) {
  const { removeTask } = taskListUpdate(task);
  await removeTask();
}

async function startTaskById(taskId: number) {
  const { startTask } = changeTaskById(taskId);
  await startTask();
}

</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Tasks planned</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
      <q-markup-table title="PLANNED TASKS" class="q-pt-none">
      <thead>
      <tr>
        <th>Task ID</th>
        <th>Task Description</th>
        <th>Status</th>
        <th>Time Spent</th>
        <th>Action</th>
        <th>Delete task</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="task in tasks" :key="task.id">
        <template v-if="task.status === 'PENDING'">
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <td><button @click="startTaskById(task.id)">Start</button></td>
          <td><button @click="deleteTask(task)">Delete</button></td>
        </template>
      </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
