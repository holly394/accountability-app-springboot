<script setup lang="ts">
import TableTasksPartner from 'components/tables/TableTasksPartner.vue';
import PartnershipsApproved from "components/tables/PartnershipsApproved.vue";

defineOptions({
  name: 'PartnerTaskPage'
});
</script>

<template>
  <div>
    <q-page class="q-pa-md row items-start q-gutter-md">
      <TableTasksPartner />
      <br>
      <PartnershipsApproved />
    </q-page>
  </div>
</template>
