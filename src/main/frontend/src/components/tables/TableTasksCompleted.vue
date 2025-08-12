<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {taskData} from 'src/composables/taskData.ts'
import {TaskData} from "components/dto/TaskData.ts";
import {Page} from "components/paging/Page.ts";
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";

const { deleteTask } = taskData();

defineOptions({
  name: 'TableTasksCompleted',
});

const props = defineProps<{
  taskList: Page<TaskData>
  payment: TaskCalculatorDto
}>()

const emit = defineEmits(['deleteTask'])

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  emit('deleteTask');
}

</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Tasks completed</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
    <q-markup-table title="COMPLETED TASKS" class="q-pt-none">
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
            <td />
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
