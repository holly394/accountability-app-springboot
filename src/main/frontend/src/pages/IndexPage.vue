<script setup lang="ts">
import PartnerWalletsGraph from 'components/charts/PartnerWalletsGraph.vue';
import TableTasksApproved from "components/tables/tasks/TableTasksApproved.vue";

defineOptions({
  name: 'IndexPage'
});

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

  </q-page>
</template>

