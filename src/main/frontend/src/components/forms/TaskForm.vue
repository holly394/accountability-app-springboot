<script setup lang="ts">

import { ref } from 'vue';
import {TaskEditRequestDto} from 'components/dto/task/TaskEditRequestDto.ts';
import {taskData} from 'src/composables/TaskData.ts';

const { addTask } = taskData();

defineOptions({
  name: 'TaskPage'
});

// this should be a ref<TaskData> - you want to send TaskData to the backend - or maybe a new class like TaskEditRequestDto.ts
const formData = ref<TaskEditRequestDto>({
  description: ''
});
const emit = defineEmits(['newTask']);

const addTaskForm = async () => {
  await addTask(formData.value);
  formData.value.description = '';
  emit('newTask');
};

</script>

<template>

    <q-form>
      <q-input
        v-model="formData.description"
        label="description"
        filled
        @keyup.enter="addTaskForm"
        type="textarea"
        name=""
      />
      <!-- Don't use SUBMIT, it will reload the page before this asynchronous request via Axios can finish -->
      <q-btn label="Submit" @click="addTaskForm" color="primary"/>
    </q-form>

</template>
