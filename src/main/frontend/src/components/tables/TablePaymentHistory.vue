<script setup lang="ts">
import { PurchaseDto } from 'components/dto/PurchaseDto.ts';
import {QMarkupTable} from 'quasar';
import {Page} from "components/paging/Page.ts";

defineOptions({
  name: 'TablePaymentHistory',
});

const props = defineProps<{
  paymentHistory: Page<PurchaseDto>
}>()

</script>

<template>
  <q-card
    class="my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">Your purchasing history</div>
    </q-card-section>
    <q-card-section class="q-pt-none">
      <q-markup-table class="text-center" title="PURCHASED ITEMS">
        <thead>
        <tr><th>PURCHASED ITEMS</th></tr>
        <tr>
          <th>Purchase ID</th>
          <th>Item Description</th>
          <th>Price</th>
          <th>Time</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in props.paymentHistory.content" :key="item.id">
            <td v-text="item.id" />
            <td v-text="item.description" />
            <td v-text="item.price" />
            <td v-text="item.purchaseTimeString" />
        </tr>
        </tbody>
      </q-markup-table>
    </q-card-section>
  </q-card>
</template>
