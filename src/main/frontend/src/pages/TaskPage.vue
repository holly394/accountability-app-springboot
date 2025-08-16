<script setup lang="ts">
//Main Task Page for current user
//Lists all tasks belonging to the current user
//As well as has a form to add new tasks

//Expects composable:
// TaskData() from TaskData.ts composable with
// methods:
// getTasksForStatus (gets all tasks with given status for current user)
// addTask (adds new task for current user, sets tasks to PENDING)
// calculatePaymentInProgress (calculates tasks in progress for current user)
// calculatePaymentCompleted (calculates tasks completed for current user)

//Children components:
//TableTasksCompleted, TableTasksInProgress, TableTasksPending


import {onMounted, ref} from 'vue';
import {TaskEditRequestDto} from 'components/dto/TaskEditRequestDto.ts';
import TableTasksCompleted from 'components/tables/TableTasksCompleted.vue';
import TableTasksInProgress from "components/tables/TableTasksInProgress.vue";
import TableTasksPending from "components/tables/TableTasksPending.vue";
import {taskData} from 'src/composables/TaskData.ts'
import {TaskData} from "components/dto/TaskData.ts";
import {TaskStatus} from "components/dto/TaskStatus.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";
import {DefaultTaskCalculatorDto, TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";

const { getTasksForStatus, addTask, calculatePaymentInProgress, calculatePaymentCompleted } = taskData();

defineOptions({
  name: 'TaskPage'
});

const pendingTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const inProgressTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const completedTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);

const inProgressPayment = ref<TaskCalculatorDto>(DefaultTaskCalculatorDto);
const completedPayment = ref<TaskCalculatorDto>(DefaultTaskCalculatorDto);

onMounted(async () => {
  inProgressPayment.value = await calculatePaymentInProgress();
  completedPayment.value = await calculatePaymentCompleted();
  await Promise.all([reloadCompleted(), reloadInProgress(), reloadPending()] );
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
  inProgressPayment.value = await calculatePaymentInProgress();
  completedPayment.value = await calculatePaymentCompleted();
};

const reloadCompleted = async () => {
  completedTasks.value = await getTasksForStatus(TaskStatus.COMPLETED);
  completedPayment.value = await calculatePaymentCompleted();
};

const refreshPendingAndInProgressLists = async () => {
  await Promise.all([reloadInProgress(), reloadPending()] );
}
const refreshInProgressAndCompletedLists = async () => {
  await Promise.all([reloadCompleted(), reloadInProgress()] );
}

const addTaskForm = async () => {
  await addTask(formData.value);
  await reloadPending();
  formData.value.description = '';
};

</script>

<template>
<div>
  <q-page class="column items-center justify-evenly">

    <q-form>
      <q-input
        v-model="formData.description"
        label="description"
        filled
        @keyup.enter="addTaskForm"
        @submit="addTaskForm"
        type="textarea"
        name=""
      />
      <q-btn label="Submit" type="submit" color="primary"/>
    </q-form>

    <br>
    <TableTasksPending :taskList="pendingTasks"
                       @start-task="refreshPendingAndInProgressLists"
                       @delete-task="reloadPending"/>
    <br>
    <TableTasksInProgress :taskList="inProgressTasks"
                          :payment="inProgressPayment"
                          @end-task="refreshInProgressAndCompletedLists"
                          @delete-task="reloadInProgress"/>
    <br>
    <TableTasksCompleted :taskList="completedTasks"
                         :payment="completedPayment"
                          @delete-task="reloadCompleted"/>
  </q-page>
</div>
</template>
