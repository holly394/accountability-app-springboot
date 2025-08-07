<script setup lang="ts">
import {onMounted, ref} from 'vue';
import { TaskEditRequestDto } from 'components/dto/TaskEditRequestDto.ts';
import TableTasksCompleted from 'components/tables/TableTasksCompleted.vue';
import TableTasksInProgress from "components/tables/TableTasksInProgress.vue";
import TableTasksPlanned from "components/tables/TableTasksPlanned.vue";
import { taskData } from 'src/composables/taskData.ts'
import {TaskData} from "components/dto/TaskData.ts";
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";

const { getTasks, addTask, calculatePaymentInProgress,
  calculatePaymentCompleted } = taskData();

defineOptions({
  name: 'TaskPage'
});

let list = ref<TaskData[]>([]);
let totalInProgressPayment = ref<TaskCalculatorDto>();
let totalCompletedPayment = ref<TaskCalculatorDto>();

onMounted(async () => {
  list.value = await getTasks();
  totalCompletedPayment.value = await calculatePaymentCompleted();
  totalInProgressPayment.value = await calculatePaymentInProgress();
});

// this should be a ref<TaskData> - you want to send TaskData to the backend - or maybe a new class like TaskEditRequestDto.ts
const formData = ref<TaskEditRequestDto>({
  description: ''
});

const addTaskForm = async () => {
  const newTask = await addTask(formData.value);
  list.value.push(newTask);
  formData.value.description = '';
};

</script>

<template>
<div>
  <q-page class="column items-center justify-evenly">

    <q-form @submit="addTaskForm">
      <q-input
        v-model="formData.description"
        label="description"
        filled
        @keyup.enter="addTaskForm"
        type="textarea"
      />
      <q-btn label="Submit" type="submit" color="primary"/>
    </q-form>

    <br>
    <TableTasksCompleted :taskList="list" :completedPayment="totalCompletedPayment"/>
    <br>
    <TableTasksInProgress :taskList="list" :inProgressPayment="totalInProgressPayment"/>
    <br>
    <TableTasksPlanned :taskList="list" />

  </q-page>
</div>
</template>
