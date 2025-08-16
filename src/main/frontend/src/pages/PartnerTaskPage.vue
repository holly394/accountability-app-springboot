<script setup lang="ts">
//Page where everything related to the current user's partners are shown
//Includes a table for their partners' tasks that they can approve or reject
//and a list of their approved partnerships

//Expects components:
// TableTasksPartner component (lists of partners' tasks)
// & PartnershipsApproved component (list of partners)

//Expects composable:
// TaskData() from TaskData.ts composable with
// methods: updateTaskStatus (updates task if approved/rejected),
// getPartnerTasks (gets complete list of partners' tasks)

import TableTasksPartner from 'components/tables/TableTasksPartner.vue';
import {taskData} from "src/composables/TaskData.ts";
import {ref, onMounted} from "vue";
import {DefaultPage, Page} from "components/paging/Page.ts";
import {TaskData} from "components/dto/TaskData.ts";
import {TaskStatus} from "components/dto/TaskStatus.ts";
const { getPartnerTasks } = taskData();

defineOptions({
  name: 'PartnerPage'
});

const partnerTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);

onMounted(async () => {
  partnerTasks.value = await getPartnerTasks(TaskStatus.COMPLETED);
})

const reloadPartnerTasks = async () => {
  partnerTasks.value = await getPartnerTasks(TaskStatus.COMPLETED);
};


</script>

<template>
    <q-page class="q-pa-md row items-start q-gutter-md">
      <TableTasksPartner
        :taskList="partnerTasks"
        @update-status="reloadPartnerTasks"/>
    </q-page>
</template>
