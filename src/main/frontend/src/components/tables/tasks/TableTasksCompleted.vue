<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {taskData} from 'src/composables/TaskData.ts';
import {TaskDataDto} from 'components/dto/task/TaskDataDto.ts';
import {Page} from 'components/paging/Page.ts';
import {TaskCalculatorDto} from 'components/dto/task/TaskCalculatorDto.ts';
import {onMounted, ref} from 'vue';
import {TaskEditRequestDto} from 'components/dto/task/TaskEditRequestDto.ts';

const { deleteTask, editTaskDescription } = taskData();

defineOptions({
  name: 'TableTasksCompleted',
});

onMounted(async () => {
  await changePage();
});

const props = defineProps<{
  taskList: Page<TaskDataDto>
  payment: TaskCalculatorDto
}>()

const emit = defineEmits(['deleteTask', 'updateList', 'editTask'])

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  emit('deleteTask');
}

const currentPage = ref<number>(0);

async function changePage() {
  emit('updateList', currentPage.value);
}

async function editTaskButton(taskId: number, description: string) {
  let newDescription = ref<TaskEditRequestDto>({
    description: ''
  });

  newDescription.value.description = description;
  await editTaskDescription(taskId, newDescription.value);
  emit('editTask');
}


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
        <tr v-for="task in props.taskList.content" :key="task.id">
            <td v-text="task.id" />
            <td>{{ task.description }}
              <q-popup-edit v-model="task.description" title="Edit Description" auto-save v-slot="scope">
                <q-input v-model="scope.value" dense autofocus counter @keyup.enter="scope.set"
                         @keydown.enter="editTaskButton(task.id, scope.value)" name=""/>
              </q-popup-edit>
            </td>
            <td v-text="task.status" />
            <td>{{ task.durationString }}</td>
            <td><button @click="deleteTaskButton(task.id)">Delete</button></td>
        </tr>
        <tr>
          <td>TOTAL STANDING VALUE: </td>
          <td>{{ props.payment.payment.toFixed(2) }}</td>
        </tr>
      </tbody>
      <q-pagination
        v-model="currentPage"
        :max="props.taskList.totalPages"
        @click="changePage()"
      />
    </q-markup-table>
    </q-card-section>
  </q-card>
</template>
