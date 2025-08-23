<script setup lang="ts">

import {onMounted, ref} from 'vue';
import {TaskEditRequestDto} from 'components/dto/task/TaskEditRequestDto.ts';
import TableTasksCompleted from 'components/tables/tasks/TableTasksCompleted.vue';
import TableTasksInProgress from "components/tables/tasks/TableTasksInProgress.vue";
import TableTasksPending from "components/tables/tasks/TableTasksPending.vue";
import {taskData} from 'src/composables/TaskData.ts'
import {TaskData} from "components/dto/task/TaskData.ts";
import {TaskStatus} from "components/dto/task/TaskStatus.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";
import {DefaultTaskCalculatorDto, TaskCalculatorDto} from "components/dto/task/TaskCalculatorDto.ts";
import TableTasksApproved from "components/tables/tasks/TableTasksApproved.vue";
import TableTasksRejected from "components/tables/tasks/TableTasksRejected.vue";
import {walletData} from "src/composables/WalletData.ts";
import {WalletDto} from "components/dto/WalletDto.ts";

const { getTasksByCurrentUserAndStatus, addTask,
  calculatePaymentInProgress, calculatePaymentCompleted } = taskData();

const { getCurrentUserWallet } = walletData();

defineOptions({
  name: 'TaskPage'
});

const wallet = ref<WalletDto>( {
  userId: 0,
  userName: '',
  balance: 0.00
});

const pendingTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const inProgressTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const completedTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);

const inProgressPayment = ref<TaskCalculatorDto>(DefaultTaskCalculatorDto);
const completedPayment = ref<TaskCalculatorDto>(DefaultTaskCalculatorDto);

onMounted(async () => {
  wallet.value = await getCurrentUserWallet();
  inProgressPayment.value = await calculatePaymentInProgress();
  completedPayment.value = await calculatePaymentCompleted();
  await Promise.all([reloadCompleted(), reloadInProgress(), reloadPending()] );
});

// this should be a ref<TaskData> - you want to send TaskData to the backend - or maybe a new class like TaskEditRequestDto.ts
const formData = ref<TaskEditRequestDto>({
  description: ''
});

//have page and max size for each? pending, in progress, completed here??
const reloadPending = async () => {
  pendingTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.PENDING, 0);
};

const reloadInProgress = async () => {
  inProgressTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.IN_PROGRESS, 0);
  inProgressPayment.value = await calculatePaymentInProgress();
  completedPayment.value = await calculatePaymentCompleted();
};

const reloadCompleted = async () => {
  completedTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.COMPLETED, 0);
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
        type="textarea"
        name=""
      />
      <!-- Don't use SUBMIT, it will reload the page before this asynchronous request via Axios can finish -->
      <q-btn label="Submit" @click="addTaskForm" color="primary"/>
    </q-form>

    <q-card
      class="my-card text-white"
      style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
    >
      <q-card-section>
        <div class="text-h6">Your current wallet balance: {{ wallet.balance.toFixed(2) }}</div>
      </q-card-section>
    </q-card>

    <br>
    <TableTasksPending :taskList="pendingTasks"
                       @delete-task="reloadPending"
                       @start-task="refreshPendingAndInProgressLists"/>
    <br>
    <TableTasksInProgress :taskList="inProgressTasks"
                          :payment="inProgressPayment"
                          @end-task="refreshInProgressAndCompletedLists"
                          @delete-task="reloadInProgress"/>
    <br>
    <TableTasksCompleted :taskList="completedTasks"
                         :payment="completedPayment"
                          @delete-task="reloadCompleted"/>
    <br>
    <TableTasksApproved />
    <br>
    <TableTasksRejected />
  </q-page>

</div>
</template>
