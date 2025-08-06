<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import { PurchaseDto } from 'components/dto/PurchaseDto.ts';
import {WalletDto} from 'components/dto/WalletDto.ts';
import {QMarkupTable} from 'quasar';


const wallet = ref<WalletDto>( {
  userId: 0,
  userName: '',
  balance: 0.00
});

const purchasingHistory = ref<PurchaseDto[]>([]);

defineOptions({
  name: 'WalletPage'
});

onMounted(async () => {
  wallet.value = await api.get<WalletDto>('/wallet').then(res => res.data)
  purchasingHistory.value = await api.get<PurchaseDto[]>('/wallet/getPurchases').then(res => res.data)
});

const formData = ref<PurchaseDto>({
  id: 0,
  price: 0.00,
  description: '',
  purchaseTimeString: ''
});

const makePurchase = async () => {
  await api.post<PurchaseDto>('/wallet/makePurchase', formData.value)
    .then(response => {
      formData.value.description = '';
      formData.value.price = 0.00;
      purchasingHistory.value.push(response.data);
    })
    .catch(error => {
      console.log(error);
    })
};

</script>

<template>

    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="my-card">
        <q-card-section>

        </q-card-section>
      </q-card>

      <q-card
        class="my-card text-white"
        style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
      >
        <q-card-section>
          <div class="text-h6">Your current balance</div>
          <div class="text-subtitle2"> {{ wallet.balance.toFixed(2) }} </div>
        </q-card-section>

        <q-card-section class="q-pt-none">

        </q-card-section>
      </q-card>

      <q-card dark bordered class="bg-grey-9 my-card">
        <q-card-section>
          <div class="text-h6">Make a purchase</div>
          <div class="text-subtitle2">Describe item and add price</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit="makePurchase">
            <q-input
              v-model="formData.description"
              label="itemDescription"
              filled
              type="textarea"
            />
            <q-input
              v-model="formData.price"
              label="itemPrice"
              filled
              type="number"
            />
            <q-btn label="Submit" type="submit" color="primary"/>
          </q-form>
        </q-card-section>
      </q-card>

      <q-markup-table title="PURCHASED ITEMS">
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
        <tr v-for="item in purchasingHistory" :key="item.id">
            <td v-text="item.id" />
            <td v-text="item.description" />
            <td v-text="item.price" />
            <td v-text="item.purchaseTimeString" />
        </tr>
        </tbody>
      </q-markup-table>

    </div>

</template>
