<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {TaskEditRequestDto} from 'components/dto/TaskEditRequestDto.ts';
import TableTasksCompleted from 'components/tables/TableTasksCompleted.vue';
import TableTasksInProgress from "components/tables/TableTasksInProgress.vue";
import TableTasksPending from "components/tables/TableTasksPending.vue";
import {taskData} from 'src/composables/taskData.ts'
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";
import {TaskData} from "components/dto/TaskData.ts";
import {TaskStatus} from "components/dto/TaskStatus.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";

const { getTasks, getTasksForStatus, addTask, calculatePaymentInProgress, calculatePaymentCompleted } = taskData();

defineOptions({
  name: 'TaskPage'
});

const currentUserTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const pendingTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const inProgressTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const completedTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const totalInProgressPayment = ref<TaskCalculatorDto>();
const totalCompletedPayment = ref<TaskCalculatorDto>();

onMounted(async () => {

  currentUserTasks.value = await getTasks();
  await Promise.all([reloadCompleted(), reloadInProgress(), reloadPending()] );

  totalCompletedPayment.value = await calculatePaymentCompleted();
  totalInProgressPayment.value = await calculatePaymentInProgress();
});


// this should be a ref<TaskData> - you want to send TaskData to the backend - or maybe a new class like TaskEditRequestDto.ts
const formData = ref<TaskEditRequestDto>({
  description: ''
});

const reloadPending = async () => {
  pendingTasks.value = await getTasksForStatus(TaskStatus.PENDING);
};

const reloadInProgress = async () => {
  inProgressTasks.value = await getTasksForStatus(TaskStatus.IN_PROGRESS);
};

const reloadCompleted = async () => {
  completedTasks.value = await getTasksForStatus(TaskStatus.COMPLETED);
};

const refreshPendingAndInProgressLists = async () => {
  await Promise.all([reloadInProgress(), reloadPending()] );
}
const refreshInProgressAndCompletedLists = async () => {
  await Promise.all([reloadCompleted(), reloadInProgress()] );
}

const addTaskForm = async () => {
  await addTask(formData.value);
  await reloadInProgress();
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
    <TableTasksPending :taskList="pendingTasks"
                       @start-task="refreshPendingAndInProgressLists"
                       @delete-task="reloadPending"/>
    <br>
    <TableTasksInProgress :taskList="inProgressTasks"
                          @end-task="refreshInProgressAndCompletedLists"
                          @delete-task="reloadInProgress"/>
    <br>
    <TableTasksCompleted :taskList="completedTasks"
                          @delete-task="reloadCompleted"/>
  </q-page>
</div>
</template>
