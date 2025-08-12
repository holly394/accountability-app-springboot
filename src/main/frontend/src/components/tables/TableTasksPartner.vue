<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {TaskData} from 'components/dto/TaskData.ts';
import {QMarkupTable} from 'quasar';
import { taskData } from 'src/composables/taskData.ts'
import {TaskStatus} from "components/dto/TaskStatus.ts";
import {TaskStatusDto} from "components/dto/TaskStatusDto.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";

const { updateTaskStatus, getPartnerTasks } = taskData();


defineOptions({
  name: 'TableTasksPartner',
});

const partnerTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);

onMounted(async () => {
  partnerTasks.value = await getPartnerTasks();
});

async function updateStatus(statusEnum: TaskStatus, taskId: number) {
  let status = <TaskStatusDto>({
    status: statusEnum
  })
  await updateTaskStatus(taskId, status);
  await getPartnerTasks();
}


</script>

<template>
<div>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Partner tasks</div>
      <div class="text-subtitle2">Your partners' tasks</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
    <q-markup-table class="text-center" title="PARTNER TASKS">
      <thead>
        <tr>
          <th>Partner</th>
          <th>Task ID</th>
          <th>Task Description</th>
          <th>Status</th>
          <th>Time Spent</th>
          <th>APPROVE</th>
          <th>REJECT</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in partnerTasks.content" :key="task.id">
          <template v-if="task.status === 'COMPLETED'">
            <td v-text="task.userName" />
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td><q-btn @click="updateStatus(TaskStatus.APPROVED, task.id)"
                       label="ACCEPT" type="submit" color="primary"/></td>
            <td><q-btn @click="updateStatus(TaskStatus.REJECTED, task.id)"
                       label="REJECT" type="submit" color="primary"/></td>
          </template>
          <template v-else>
            <td v-text="task.userName" />
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td></td>
            <td></td>
          </template>
        </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</div>
</template>
