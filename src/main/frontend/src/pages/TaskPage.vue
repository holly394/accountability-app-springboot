<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios';
import { TaskData } from 'components/dto/TaskData.ts';
import { TaskEditRequestDto } from 'components/dto/TaskEditRequestDto.ts';
import { QMarkupTable } from "quasar";

const tasks = ref<TaskData[]>([]);

defineOptions({
  name: 'TaskPage'
});

onMounted(async () => {
  tasks.value = await api.get<TaskData[]>('/tasks').then(res => res.data)
});

// this should be a ref<TaskData> - you want to send TaskData to the backend - or maybe a new class like TaskEditRequestDto.ts
const formData = ref<TaskEditRequestDto>({
  description: ""
});

const addTask = async () => {
  await api.post<TaskData>('/tasks/add', formData.value)
    .then(response => {
      console.log('New task created with ID:', response.data.id);
      formData.value.description = "";
      tasks.value.push(response.data);
    })
    .catch(error => {
      console.log(error);
    })
};

async function removeTask(taskId: number) {
  try{
    await api.delete(`/tasks/${taskId}`);

    const index = tasks.value.findIndex(
      (task) => task.id === taskId
    );

    tasks.value.splice(index, 1);
  } catch {
    console.log('Error while deleting task', taskId);
  }
}

async function startTask(taskId: number) {
    await api.post<TaskData>(`/tasks/${taskId}/start`, taskId)
      .then(response => {
          console.log('Task started with ID:', response.data.id);
          const index = tasks.value.findIndex(
            (task) => task.id === taskId
          );
          tasks.value.splice(index, 1, response.data);
        })
          .catch(error => {
            console.log(error);
          })
}

async function endTask(taskId: number) {
  await api.post(`/tasks/${taskId}/end`, taskId)
    .then(response => {
      console.log('Task ended with ID:', response.data.id);
      const index = tasks.value.findIndex(
        (task) => task.id === taskId
      );

      tasks.value.splice(index, 1, response.data);
    })
    .catch(error => {
      console.log(error);
    })
}




</script>

<template>
<div>
  <q-page class="row items-center justify-evenly">
    <q-form @submit="addTask">
      <q-input
        v-model="formData.description"
        label="description"
        filled
        type="textarea"
      />
      <q-btn label="Submit" type="submit" color="primary"/>
    </q-form>
    <br>

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
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <td>
            <div v-if="task.durationString === 'PT0S'">
              <button @click="startTask(task.id)">Start</button>
            </div>
            <div v-else-if="task.status === 'IN_PROGRESS'">
              <button @click="endTask(task.id)">End</button>
            </div>
            <div v-else></div>
          </td>
          <td><button @click="removeTask(task.id)">Delete</button></td>
        </tr>
      </tbody>
    </q-markup-table>
  </q-page>
</div>
</template>
