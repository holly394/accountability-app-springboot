<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import { PurchaseDto } from 'components/dto/PurchaseDto.ts';
import {WalletDto} from 'components/dto/WalletDto.ts';
import TablePaymentHistory from "components/tables/TablePaymentHistory.vue";
import ThisUserWallet from 'layouts/ThisUserWallet.vue';
import { QForm } from 'quasar';

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

const formRef = ref<QForm>();

const makePurchase = async () => {
  const isValid = await formRef.value?.validate();
  if (isValid) {
    await api.post<PurchaseDto>('/wallet/makePurchase', formData.value)
      .then(response => {
        formData.value.description = '';
        formData.value.price = 0.00;
        purchasingHistory.value = [...purchasingHistory.value, response.data];
      })
      .catch(error => {
        console.log(error);
      })
    formRef.value?.resetValidation();
  } else
  {
    console.error('Form is invalid');
    formRef.value?.resetValidation();
    return;
  }
};

</script>

<template>
  <div class="q-pa-md row items-start q-gutter-md">
    <q-card
      class="my-card text-white"
      style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
    >
      <q-card-section>
        <div class="text-h6">Your balance: <ThisUserWallet /></div>
      </q-card-section>
    </q-card>

    <q-card
      class="my-card text-white"
      style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
    >
      <q-card-section>
        <div class="text-h6">Make a purchase</div>
        <div class="text-subtitle2">Describe item and add price</div>
      </q-card-section>

      <q-card-section>
        <q-form ref="formRef" @submit.prevent="makePurchase">
          <q-input
            v-model="formData.description"
            label="description"
            filled
            type="textarea"
            bg-color="white"
            :rules="[val => !!val || 'Field is required']"
               />
          <q-input
            v-model="formData.price"
            label="price"
            filled
            type="number"
            bg-color="white"
            :rules="[val => !!val || 'Field is required']"
               />

          <q-btn label="Submit"
                 type="submit" color="primary"/>
        </q-form>
      </q-card-section>
    </q-card>

    <TablePaymentHistory />
  </div>
</template>
