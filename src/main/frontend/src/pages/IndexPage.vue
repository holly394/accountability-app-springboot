<script setup lang="ts">
import PartnerWalletsGraph from 'components/charts/PartnerWalletsGraph.vue';
import TableTasksApproved from "components/tables/tasks/TableTasksApproved.vue";
import TableTasksGeneral from "components/tables/tasks/TableTasksGeneral.vue";
import {TaskData} from "components/dto/task/TaskData.ts";
import {onMounted, ref} from "vue";
import {DefaultPage, Page} from "components/paging/Page.ts";
import {taskData} from "src/composables/TaskData.ts";
import {relationshipData} from "src/composables/RelationshipData.ts";
const { getAllTasksByUserList } = taskData();
const { getPartnerIdList } = relationshipData();

defineOptions({
  name: 'IndexPage'
});

const recentPartnerTasks = ref<Page<TaskData>>(DefaultPage as Page<TaskData>);
const partnerList = ref<number[]>([]);
onMounted(async () => {
  partnerList.value = await getPartnerIdList();
  recentPartnerTasks.value = await getAllTasksByUserList(partnerList.value);
})

</script>

<template>
  <q-page class="q-pa-md row items-start q-gutter-md">

    <q-card
      class="my-card text-white"
      style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
    >
      <q-card-section>
        <div class="text-h6">Wallet overview</div>
        <div class="text-subtitle2">How you compare</div>
      </q-card-section>
      <q-card-section class="q-pt-none">
        <PartnerWalletsGraph />
      </q-card-section>
    </q-card>

    <br>
    <TableTasksApproved />
    <br>
    <TableTasksGeneral :taskList="recentPartnerTasks" :tableTitle="'Recent tasks by partners'" />

  </q-page>
</template>

