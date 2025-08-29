<script setup lang="ts">
import PartnerWalletsGraph from 'components/charts/PartnerWalletsGraph.vue';
import PartnerTaskDurationGraph from 'components/charts/PartnerTaskDurationGraph.vue';
import TableTasksApproved from "components/tables/tasks/TableTasksApproved.vue";
import TablePartnerTasksAll from "components/tables/tasks/TablePartnerTasksAll.vue";


defineOptions({
  name: 'IndexPage'
});

</script>

<template>
  <q-page class="q-pa-md row items-start q-gutter-md">

    <PartnerTaskDurationGraph />
    <br>
    <PartnerWalletsGraph />
    <br>
    <TableTasksApproved />
    <br>
    <TablePartnerTasksAll />
    <br>

  </q-page>
</template>

