<script setup lang="ts">
import { ref } from 'vue';
import { PurchaseDto } from 'components/dto/PurchaseDto.ts';
import { QForm } from 'quasar';
import { walletData } from 'src/composables/WalletData.ts';

const { makePurchase } = walletData();

defineOptions({
  name: 'PurchaseForm'
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
  await resetForm();
  emit('newPurchase');
};

const resetForm = async () => {
  formData.value.price = 0;
  formData.value.description = '';
}

const emit = defineEmits(['newPurchase'])

</script>

<template>
  <div class="q-pa-md row items-start q-gutter-md">
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
  </div>
</template>
