<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {taskData} from "src/composables/TaskData.ts";
import {TaskDataDto} from "components/dto/task/TaskDataDto.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";
import {onMounted, ref} from "vue";
import {TaskStatus} from "components/dto/task/TaskStatus.ts";

const { deleteTask, getTasksByCurrentUserAndStatus } = taskData();

defineOptions({
  name: 'TableTasksApproved',
});

const currentPage = ref<number>(0);
const maxPages = ref<number>(0);
const pageSize = 5;
const approvedTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);

onMounted(async () => {
  await reloadApproved();
});

const reloadApproved = async () => {
  approvedTasks.value = await getTasksByCurrentUserAndStatus(
    TaskStatus.APPROVED, currentPage.value-1, pageSize);

  currentPage.value = approvedTasks.value.pageNumber+1;
  maxPages.value = approvedTasks.value.totalPages;
};

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  await reloadApproved();
}

async function changePage() {
  await reloadApproved();
}

</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Tasks approved</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
    <q-markup-table title="APPROVED TASKS" class="q-pt-none">
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
        <tr v-for="task in approvedTasks.content" :key="task.id">
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td />
            <td><button @click="deleteTaskButton(task.id)">Delete</button></td>
        </tr>
      </tbody>
      <q-pagination
        v-model="currentPage"
        :max="maxPages"
        @click="changePage()"
      />
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
