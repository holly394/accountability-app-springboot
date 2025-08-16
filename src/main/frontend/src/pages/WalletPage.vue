<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { PurchaseDto } from 'components/dto/PurchaseDto.ts';
import {WalletDto} from 'components/dto/WalletDto.ts';
import TablePaymentHistory from "components/tables/TablePaymentHistory.vue";
import { QForm } from 'quasar';
import { walletData } from 'src/composables/WalletData.ts';
import {DefaultPage, Page} from "components/paging/Page.ts";

const { getCurrentUserWallet, getCurrentUserPurchaseHistory, makePurchase } = walletData();

const wallet = ref<WalletDto>( {
  userId: 0,
  userName: '',
  balance: 0.00
});

const purchasingHistory = ref<Page<PurchaseDto>>(DefaultPage as Page<PurchaseDto>);

defineOptions({
  name: 'WalletPage'
});

onMounted(async () => {
  wallet.value = await getCurrentUserWallet();
  purchasingHistory.value = await getCurrentUserPurchaseHistory();
});

const formData = ref<PurchaseDto>({
  id: 0,
  price: 0.00,
  description: '',
  purchaseTimeString: ''
});

const formRef = ref<QForm>();

const makeNewPurchase = async () => {
  await makePurchase(formData.value);
  purchasingHistory.value = await getCurrentUserPurchaseHistory();
  wallet.value = await getCurrentUserWallet();
  await resetForm();
};

const resetForm = async () => {
  formData.value.price = 0;
  formData.value.description = '';
}

</script>

<template>
  <div class="q-pa-md row items-start q-gutter-md">
    <q-card
      class="my-card text-white"
      style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
    >
      <q-card-section>
        <div class="text-h6">Your balance: {{ wallet.balance.toFixed(2) }}</div>
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
        <q-form ref="formRef" @submit="makeNewPurchase">
          <q-input
            v-model="formData.description"
            label="description"
            filled
            type="textarea"
            bg-color="white"
            name="itemDescription"
          />
          <q-input
            v-model="formData.price"
            label="price"
            filled
            type="number"
            bg-color="white"
            name="itemPrice"
          />

          <q-btn label="Submit"
                 type="submit" color="primary"/>
        </q-form>
      </q-card-section>
    </q-card>

    <TablePaymentHistory
      :paymentHistory = "purchasingHistory"
    />
  </div>
</template>
