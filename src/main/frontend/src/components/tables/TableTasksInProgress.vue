<script setup lang="ts">
import {onMounted } from 'vue';
import { QMarkupTable } from 'quasar';
import { taskRelated } from 'src/composables/tasks/taskList.ts'
import {changeTaskById} from 'src/composables/tasks/changeTaskById.ts';
import {taskPayment} from "src/composables/tasks/taskPayment.ts";
import {TaskData} from "components/dto/TaskData.ts";
import {taskListUpdate} from "src/composables/tasks/taskListUpdate.ts";

const { tasks, getCurrentUserTaskList, forceUpdate } = taskRelated();
const { totalInProgressPayment, getTotalInProgressPayment } = taskPayment();

defineOptions({
  name: 'TableTasksInProgress',
});

onMounted(async () => {
  await getCurrentUserTaskList();
  await getTotalInProgressPayment();
});


async function deleteTask(task: TaskData) {
  const { removeTask } = taskListUpdate(task);
  await removeTask();
  await forceUpdate();
}

async function endTaskById(taskId: number) {
  const { endTask } = changeTaskById(taskId);
  await endTask();
  await forceUpdate();
}


</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Tasks in progress</div>
    </q-card-section>
    <q-card-section title="TASKS IN PROGRESS" class="q-pt-none">
    <q-markup-table>
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
        <template v-if="task.status === 'IN_PROGRESS'">
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <td><button @click="endTaskById(task.id)">End</button></td>
          <td><button @click="deleteTask(task)">Delete</button></td>
        </template>
      </tr>
      <tr>
        <td>TOTAL VALUE: </td>
        <td>{{ totalInProgressPayment.payment.toFixed(2) }}</td>
      </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
