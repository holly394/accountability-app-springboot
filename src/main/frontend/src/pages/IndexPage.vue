<script setup lang="ts">
import PartnerWalletsGraph from 'components/charts/PartnerWalletsGraph.vue';
import PartnerTaskDurationGraph from 'components/charts/PartnerTaskDurationGraph.vue';
import TableTasksApproved from 'components/tables/tasks/TableTasksApproved.vue';
import TablePartnerTasksAll from 'components/tables/tasks/TablePartnerTasksAll.vue';


defineOptions({
  name: 'IndexPage'
});

</script>

<template>
  <q-layout>
    <q-page-container class="q-pa-md items-start q-gutter-md col-12 col-sm-6 col-md-4 col-lg-3">


        <PartnerTaskDurationGraph />
        <br>
        <PartnerWalletsGraph />

        <TableTasksApproved />
        <br>
        <TablePartnerTasksAll />


    </q-page-container>
  </q-layout>

</template>

