<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {TaskData} from 'components/dto/TaskData.ts';
import {QMarkupTable} from 'quasar';
import {UserDto} from 'components/dto/UserDto.ts';
import { taskData } from 'src/composables/taskData.ts'
import { relationshipData } from 'src/composables/relationshipData.ts'
import {TaskStatus} from "components/dto/TaskStatus.ts";
import {TaskStatusDto} from "components/dto/TaskStatusDto.ts";
import {DefaultPage, Page} from "components/paging/Page.ts";

const { updateTaskStatus, getTasksByUserId } = taskData();
const { getApprovedPartners } = relationshipData();


defineOptions({
  name: 'TableTasksPartner',
});

const partners = ref<UserDto[]>([]);
const partnerIds = ref<number[]>([]);
const partnerTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);

onMounted(async () => {

  // FIXME:
  // BAD INSECURE CODE
  // HOLLY NEEDS TO MAKE SURE FRONTEND DOESNT DECIDE WHICH RELATIONSHIPS IT ASKS THE BACKEND ABOUT
  // BACKEND HAS ACCESS TO RELATIONSHIPS - USE IN getTasksBzUserId MAYBE
  partners.value = await getApprovedPartners();

  partners.value.forEach((partner) => {
    partnerIds.value.push(partner.id);
  })

  partnerTasks.value = await getTasksByUserId(partnerIds.value);
});

async function updateStatus(statusEnum: TaskStatus, taskId: number) {
  let status = <TaskStatusDto>({
    status: statusEnum
  })
  await updateTaskStatus(taskId, status);
  // TODO REFRESH DATA
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
