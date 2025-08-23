<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {TaskData} from "components/dto/task/TaskData.ts";
import {Page} from "components/paging/Page.ts";

defineOptions({
  name: 'TableTasksGeneral',
});

const props = defineProps<{
  taskList: Page<TaskData>,
  tableTitle: string,
}>()

</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">{{props.tableTitle}}</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
    <q-markup-table title="TASKS" class="q-pt-none">
      <thead>
        <tr>
          <th>Task ID</th>
          <th>Task Description</th>
          <th>Status</th>
          <th>Time Spent</th>
          <th>User</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in props.taskList.content" :key="task.id">
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td v-text="task.userName" />
        </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
