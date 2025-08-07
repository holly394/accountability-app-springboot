<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import {QMarkupTable} from 'quasar';
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";
import {taskData} from 'src/composables/taskData.ts'
import {TaskData} from "components/dto/TaskData.ts";

const { deleteTask, calculatePaymentCompleted } = taskData();

defineOptions({
  name: 'TableTasksCompleted',
});

const props = defineProps<{
  taskList: TaskData[]
  completedPayment: TaskCalculatorDto | undefined
}>()


const listCopy = ref(props.taskList)
const totalCompletedPayment = ref(props.completedPayment)

onMounted(async () => {
});

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  totalCompletedPayment.value = await calculatePaymentCompleted();
  for(let i=0; i<listCopy.value.length; i++) {
    if(listCopy.value[i].id === taskId) {
      listCopy.value.splice(i, 1);
    }
  }
}

watch (() => props.taskList, (newList) => {
  listCopy.value = newList;
})

watch (() => props.completedPayment, (newPayment) => {
  totalCompletedPayment.value = newPayment;
})

</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Tasks completed</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
    <q-markup-table title="COMPLETED TASKS" class="q-pt-none">
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
        <tr v-for="task in listCopy" :key="task.id">
          <template v-if="task.status === 'COMPLETED'">
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td />
            <td><button @click="deleteTaskButton(task.id)">Delete</button></td>
          </template>
        </tr>
        <tr>
          <td>TOTAL VALUE: </td>
          <td>{{ totalCompletedPayment?.payment.toFixed(2) }}</td>
        </tr>
      </tbody>
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
