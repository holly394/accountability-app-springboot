<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {WalletDto} from 'components/dto/WalletDto.ts';
import TablePaymentHistory from 'components/tables/TablePaymentHistory.vue';
import { walletData } from 'src/composables/WalletData.ts';
import PurchaseForm from 'components/forms/PurchaseForm.vue';
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {PurchaseDto} from 'components/dto/PurchaseDto.ts';

const { getCurrentUserWallet, getCurrentUserPurchaseHistory } = walletData();

const purchasingHistory = ref<Page<PurchaseDto>>(DefaultPage as Page<PurchaseDto>);

const parentCurrentPage = ref<number>(0);
const maxPages = ref<number>(0);
const pageSize = 5;

async function changePage(currentPage: number) {
  purchasingHistory.value = await getCurrentUserPurchaseHistory(currentPage-1, pageSize);
  parentCurrentPage.value = purchasingHistory.value.pageNumber+1;
  maxPages.value = purchasingHistory.value.totalPages;
}

const wallet = ref<WalletDto>( {
  userId: 0,
  userName: '',
  balance: 0.00
});

defineOptions({
  name: 'WalletPage'
});

onMounted(async () => {
  await updateWalletPage();
});

const updateWalletPage = async () => {
  purchasingHistory.value = await getCurrentUserPurchaseHistory(parentCurrentPage.value, pageSize);
  wallet.value = await getCurrentUserWallet();
};

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

    <PurchaseForm
      @new-purchase="updateWalletPage" />

    <TablePaymentHistory
    @update-list="changePage"
    :purchaseHistory="purchasingHistory"
    :maxPages="maxPages"/>

  </div>
</template>
