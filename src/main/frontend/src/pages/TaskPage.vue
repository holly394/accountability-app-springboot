<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { TaskEditRequestDto } from 'components/dto/TaskEditRequestDto.ts';
import TableTasksCompleted from 'components/tables/TableTasksCompleted.vue';
import TableTasksInProgress from "components/tables/TableTasksInProgress.vue";
import TableTasksPlanned from "components/tables/TableTasksPlanned.vue";
import { taskRelated } from 'src/composables/tasks/taskList.ts'
import {api} from "boot/axios.ts";
import {TaskData} from "components/dto/TaskData.ts";

const { tasks, getCurrentUserTaskList, forceUpdate } = taskRelated();

defineOptions({
  name: 'TaskPage'
});

onMounted(async () => {
  await getCurrentUserTaskList();
});

// this should be a ref<TaskData> - you want to send TaskData to the backend - or maybe a new class like TaskEditRequestDto.ts
const formData = ref<TaskEditRequestDto>({
  description: ''
});

const addTask = async () => {
  await api.post<TaskData>('/tasks/add', formData.value)
    .then(response => {
      console.log('New task created with ID:', response.data.id);
      tasks.value.push(response.data);
      forceUpdate();
    })
    .catch(error => {
      console.log(error);
    })
};


</script>

<template>
<div>
  <q-page class="column items-center justify-evenly">

    <q-form @submit="addTask">
      <q-input
        v-model="formData.description"
        label="description"
        filled
        @keyup.enter="addTask"
        type="textarea"
      />
      <q-btn label="Submit" type="submit" color="primary"/>
    </q-form>

    <br>
    <TableTasksCompleted />
    <br>
    <TableTasksInProgress />
    <br>
    <TableTasksPlanned />

  </q-page>
</div>
</template>
