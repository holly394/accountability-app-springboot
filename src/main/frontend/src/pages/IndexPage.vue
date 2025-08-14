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

  </q-page>
</template>

<script setup lang="ts">
import PartnerWalletsGraph from 'components/charts/PartnerWalletsGraph.vue';

defineOptions({
  name: 'IndexPage'
});

</script>
