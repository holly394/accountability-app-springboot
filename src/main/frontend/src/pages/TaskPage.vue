<script setup lang="ts">

import {onMounted, ref} from 'vue';
import TableTasksCompleted from 'components/tables/tasks/TableTasksCompleted.vue';
import TableTasksInProgress from "components/tables/tasks/TableTasksInProgress.vue";
import TableTasksPending from "components/tables/tasks/TableTasksPending.vue";
import {taskData} from 'src/composables/TaskData.ts'
import {TaskDataDto} from "components/dto/task/TaskDataDto.ts";
import {TaskStatus} from "components/dto/task/TaskStatus.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";
import {DefaultTaskCalculatorDto, TaskCalculatorDto} from "components/dto/task/TaskCalculatorDto.ts";
import TableTasksApproved from "components/tables/tasks/TableTasksApproved.vue";
import TableTasksRejected from "components/tables/tasks/TableTasksRejected.vue";
import {walletData} from "src/composables/WalletData.ts";
import {WalletDto} from "components/dto/WalletDto.ts";
import TaskForm from "components/forms/TaskForm.vue"

const { getTasksByCurrentUserAndStatus,
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

const pageSize = 5;

const pendingCurrentPage = ref<number>(0);
const inProgressCurrentPage = ref<number>(0);
const completedCurrentPage = ref<number>(0);

const pendingTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);
const inProgressTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);
const completedTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);

const inProgressPayment = ref<TaskCalculatorDto>(DefaultTaskCalculatorDto);
const completedPayment = ref<TaskCalculatorDto>(DefaultTaskCalculatorDto);

onMounted(async () => {
  wallet.value = await getCurrentUserWallet();
  await Promise.all([reloadCompleted(), reloadInProgress(), reloadPending()] );
});

const reloadPending = async () => {
  pendingTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.PENDING, pendingCurrentPage.value, pageSize);
};

async function changePendingPage(currentPage: number) {
  pendingTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.PENDING, currentPage-1, pageSize);
  pendingCurrentPage.value = pendingTasks.value.pageNumber+1;
}

const reloadInProgress = async () => {
  inProgressTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.IN_PROGRESS, inProgressCurrentPage.value, pageSize);
  inProgressPayment.value = await calculatePaymentInProgress();
};

async function changeInProgressPage(currentPage: number) {
  inProgressTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.IN_PROGRESS, currentPage-1, pageSize);
  inProgressCurrentPage.value = inProgressTasks.value.pageNumber+1;
}

const reloadCompleted = async () => {
  completedTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.COMPLETED, completedCurrentPage.value, pageSize);
  completedPayment.value = await calculatePaymentCompleted();
};

async function changeCompletedPage(currentPage: number) {
  completedTasks.value = await getTasksByCurrentUserAndStatus(TaskStatus.COMPLETED, currentPage-1, pageSize);
  completedCurrentPage.value = completedTasks.value.pageNumber+1;
}

const refreshPendingAndInProgressLists = async () => {
  await Promise.all([reloadInProgress(), reloadPending()]);
}
const refreshInProgressAndCompletedLists = async () => {
  await Promise.all([reloadCompleted(), reloadInProgress()]);
}

</script>

<template>
<div>
  <q-page class="column items-center justify-evenly">

    <TaskForm @new-task="reloadPending"/>

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
                       @start-task="refreshPendingAndInProgressLists"
                       @update-list="changePendingPage"/>
    <br>
    <TableTasksInProgress :taskList="inProgressTasks"
                          :payment="inProgressPayment"
                          @end-task="refreshInProgressAndCompletedLists"
                          @delete-task="reloadInProgress"
                          @update-list="changeInProgressPage"/>
    <br>
    <TableTasksCompleted :taskList="completedTasks"
                         :payment="completedPayment"
                          @delete-task="reloadCompleted"
                         @update-list="changeCompletedPage"/>
    <br>
    <TableTasksApproved />
    <br>
    <TableTasksRejected />
  </q-page>

</div>
</template>
