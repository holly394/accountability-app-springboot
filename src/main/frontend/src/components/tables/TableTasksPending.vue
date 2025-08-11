<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {taskData} from 'src/composables/taskData.ts'
import {TaskData} from "components/dto/TaskData.ts";
import {Page} from "components/paging/Page.ts";

const { deleteTask, startTask } = taskData();

defineOptions({
  name: 'TableTasksPending'
});

const props = defineProps<{
  taskList: Page<TaskData>
}>()

const emit = defineEmits(['startTask', 'deleteTask'])

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  emit('deleteTask');
}

async function startTaskButton(taskId: number) {
  await startTask(taskId);
  emit('startTask');
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
      <tr v-for="task in props.taskList.content" :key="task.id" v-ripple >
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <td><button @click="startTaskButton(task.id)">Start</button></td>
          <td><button @click="deleteTaskButton(task.id)">Delete</button></td>
      </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
