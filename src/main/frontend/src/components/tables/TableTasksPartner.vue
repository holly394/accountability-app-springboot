<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {api} from 'boot/axios';
import {TaskData} from 'components/dto/TaskData.ts';
import {QMarkupTable} from 'quasar';
import {UserDto} from 'components/dto/UserDto.ts';
import {editTask} from "src/composables/tasks/editTask.ts";

const tasks = ref<TaskData[]>([]);

defineOptions({
  name: 'TableTasksPartner',
});

const partners = ref<UserDto[]>([]);
const partnerIds = ref<number[]>([]);

onMounted(async () => {
  await api.get<UserDto[]>('/relationships/get-approved-partners')
    .then(res => {
      partners.value = res.data;

      for(let i = 0; i < partners.value.length; i++) {
        partnerIds.value.push(partners.value[i].id);
      }
    }).catch(err => {
      console.error(err);
    })

  await api.get<TaskData[]>(`/tasks/get-tasks-by-partner-id?ids=${partnerIds.value}`)
    .then(res => tasks.value = res.data)

});

async function updateStatus(status: string, taskId: number) {
  const { updateStatus } = editTask(status, taskId);
  await updateStatus();
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
        <tr v-for="task in tasks" :key="task.id">
          <template v-if="task.status === 'COMPLETED'">
            <td v-text="task.userName" />
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td><q-btn @click="updateStatus('APPROVED', task.id)"
                       label="ACCEPT" type="submit" color="primary"/></td>
            <td><q-btn @click="updateStatus('REJECTED', task.id)"
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
