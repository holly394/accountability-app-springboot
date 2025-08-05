<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {api} from 'boot/axios';
import {TaskData} from 'components/dto/TaskData.ts';
import {QMarkupTable} from 'quasar';
import {UserDto} from 'components/dto/UserDto.ts';
import {TaskStatusDto} from "components/dto/TaskStatusDto.ts";

defineOptions({
  name: 'PartnerTaskPage'
});
const tasks = ref<TaskData[]>([]);
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
  await api.post<TaskData>(`/tasks/${taskId}/update-status`,
     {status: status} as TaskStatusDto)
  .then(res => {
    const index = tasks.value.findIndex(
      (task) => task.id === taskId
    );
    tasks.value.splice(index, 1, res.data);
  })
  .catch(error => {
    console.log(error);
  })
}


</script>

<template>
<div>
  <q-page>
    <q-markup-table title="PARTNER TASKS">
      <thead>
        <tr><th>PARTNER TASKS</th></tr>
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
          <td v-text="task.userName" />
          <td v-text="task.id" />
          <td v-text="task.description" />
          <td v-text="task.status" />
          <td v-text="task.durationString" />
          <template v-if="task.status === 'COMPLETED'">
            <td><q-btn @click="updateStatus('APPROVED', task.id)"
                       label="ACCEPT" type="submit" color="primary"/></td>
            <td><q-btn @click="updateStatus('REJECTED', task.id)"
                       label="REJECT" type="submit" color="primary"/></td>
          </template>
          <template v-else>
            <td></td>
            <td></td>
          </template>
        </tr>
      </tbody>
    </q-markup-table>
<br>
    <q-markup-table title="PARTNERS">
      <thead>
      <tr><th>PARTNERS</th></tr>
      <tr>
        <th>Partner ID</th>
        <th>Partner name</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in partners" :key="user.id">
        <td v-text="user.id" />
        <td v-text="user.username" />
      </tr>
      </tbody>
    </q-markup-table>

  </q-page>
</div>
</template>
