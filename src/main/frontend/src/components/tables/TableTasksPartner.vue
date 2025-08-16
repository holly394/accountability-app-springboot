<script setup lang="ts">
//Table of tasks belonging to the partners of the current user
//Includes buttons where the current user can approve or reject their partners'
//completed tasks.

//Expects as dependencies: TaskData composable (updateTaskStatus, getPartnerTasks)

import {TaskData} from 'components/dto/TaskData.ts';
import {QMarkupTable} from 'quasar';
import {TaskStatus} from "components/dto/TaskStatus.ts";
import {TaskStatusDto} from "components/dto/TaskStatusDto.ts";
import {Page} from "components/paging/Page.ts";
import {taskData} from "src/composables/TaskData.ts";

const { processTaskForPartner } = taskData();

defineOptions({
  name: 'TableTasksPartner'
});

const props = defineProps<{
  taskList: Page<TaskData>
}>()

const emit = defineEmits(['updateStatus'])

async function updateStatusButton(taskId: number, statusEnum: TaskStatus) {
  let status = <TaskStatusDto>({
    status: statusEnum
  })

  await processTaskForPartner(taskId, status);
  emit('updateStatus');
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
        <tr v-for="task in props.taskList.content" :key="task.id">
            <td v-text="task.userName" />
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td><q-btn @click="updateStatusButton(task.id, TaskStatus.APPROVED)"
                       label="ACCEPT" type="submit" color="primary"/></td>
            <td><q-btn @click="updateStatusButton(task.id, TaskStatus.REJECTED)"
                       label="REJECT" type="submit" color="primary"/></td>
        </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</div>
</template>
