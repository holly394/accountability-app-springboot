<script setup lang="ts">
import { QMarkupTable } from 'quasar';
import { taskData } from 'src/composables/taskData.ts'
import {TaskData} from "components/dto/TaskData.ts";
import { ref, watch, computed } from "vue";
const { deleteTask, startTask } = taskData();

defineOptions({
  name: 'TableTasksPlanned'
});

const props = defineProps<{
  taskList: TaskData[]
}>()

const listCopy = ref(props.taskList)

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  for(let i=0; i<listCopy.value.length; i++) {
    if(listCopy.value[i].id === taskId) {
      listCopy.value.splice(i, 1);
    }
  }
}

async function startTaskButton(taskId: number) {
  const started = await startTask(taskId);
  for(let i=0; i<listCopy.value.length; i++) {
    if(listCopy.value[i].id === taskId) {
      listCopy.value.splice(i, 1, started);
    }
  }
}

const page = ref<number>(1)
const currentPage = ref<number>(1)
const totalPages = ref<number>(5)

const getTaskData = computed<TaskData[]>(() => {
  const start = (page.value - 1) * totalPages.value
  const end = start + totalPages.value
  return listCopy.value.slice(start, end)
})

watch (() => props.taskList, (newList) => {
  listCopy.value = newList;
})

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
      <tr v-for="task in getTaskData" :key="task.id" v-ripple>
        <template v-if="task.status === 'PENDING'">
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <td><button @click="startTaskButton(task.id)">Start</button></td>
          <td><button @click="deleteTaskButton(task.id)">Delete</button></td>
        </template>
      </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
    <q-card-actions align="center">
      <q-pagination
        v-model="page"
        :min="currentPage"
        :max="Math.ceil(listCopy.length/totalPages)"
        :input="true"
        input-class="text-orange-10"
      >
      </q-pagination>
    </q-card-actions>
  </q-card>
</template>
