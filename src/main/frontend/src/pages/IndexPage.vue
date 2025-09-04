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
    <q-page-container class="q-pa-md q-gutter-md">

      <div class="row q-gutter-md">
        <div class="col-12 col-md-4">
          <PartnerTaskDurationGraph />
        </div>
        <div class="col-12 col-md-4">
          <PartnerWalletsGraph  />
        </div>
      </div>

      <div class="row q-gutter-md">
        <div class="col-12 col-md-4">
          <TableTasksApproved  />
        </div>
        <div class="col-12 col-md-4">
          <TablePartnerTasksAll  />
        </div>
      </div>

    </q-page-container>
  </q-layout>

</template>

<style lang="scss" scoped>

</style>

