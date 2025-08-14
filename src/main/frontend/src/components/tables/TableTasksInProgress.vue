<script setup lang="ts">
//Table with current user's in-progress tasks only
//tasks include buttons for deleting and ending a task
//ending a task will set that task to COMPLETED and will be listed in the completed table

//Expects props:
// taskList = inProgressTasks (gets current user's in-progress tasks)
// payment = inProgressPayment (gets minimum-wage earnings of total time
// spent on in-progress tasks from start to current time)

//Expects composable:
// TaskData() from TaskData.ts composable with methods:
// deleteTask (deletes task from current user's list),
// endTask (sets task to status: COMPLETED)

//Quirks:
//Parent TaskPage.vue listens to emitted events endTask and deleteTask

import { QMarkupTable } from 'quasar';
import { taskData } from 'src/composables/taskData.ts'
import {TaskData} from "components/dto/TaskData.ts";
import {Page} from "components/paging/Page.ts";
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";

const { deleteTask, endTask } = taskData();

defineOptions({
  name: 'TableTasksInProgress',
});

const props = defineProps<{
  taskList: Page<TaskData>
  payment: TaskCalculatorDto
}>()

const emit = defineEmits(['endTask', 'deleteTask'])

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  emit('deleteTask');
}

async function endTaskButton(taskId: number) {
  await endTask(taskId);
  emit('endTask');
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
      <tr v-for="task in props.taskList.content" :key="task.id">
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <td><button @click="endTaskButton(task.id)">End</button></td>
          <td><button @click="deleteTaskButton(task.id)">Delete</button></td>
      </tr>
      <tr>
        <td>TOTAL VALUE: </td>
        <td>{{ props.payment.payment.toFixed(2) }}</td>
      </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
