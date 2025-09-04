<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import {WalletDto} from 'components/dto/WalletDto.js';
import {ECharts} from 'echarts';
import {walletData} from 'src/composables/WalletData.ts';
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {relationshipData} from 'src/composables/RelationshipData.ts';

const {getCurrentUserWallet, getWalletsByUserIds} = walletData();
const { getPartnerIdList } = relationshipData();

const expanded = ref<boolean>(false);

defineOptions({
  name: 'TestGraph'
});

// ECharts instance (typed as ECharts or null)
let chartInstance: ECharts | null = null;

//in template, we have <div> with ref="barchart"
// DOM reference (strictly typed as HTMLDivElement or null)
const walletBarChart = ref<HTMLDivElement | null>(null);

// Define interface for API response
interface TooltipParams {
  name: string,
  balance: number,
  data: walletData;
}

interface walletData {
  name: string;
  value: number;
}

const currentUserWallet = ref<WalletDto>({
  userId: 0,
  userName: '',
  balance: 0
});

const partners = ref<number[]>([]);
const partnerWallets = ref<Page<WalletDto>>(DefaultPage as Page<WalletDto>);

// Fetch data and initialize chart
onMounted(async () => {
  if (walletBarChart.value) {
    // 1. Fetch data from API
    currentUserWallet.value = await getCurrentUserWallet();
    partners.value = await getPartnerIdList();
    partnerWallets.value = await getWalletsByUserIds(partners.value);

    const walletDataset: walletData[] = partnerWallets.value.content.map(walletDto => ({
      name: walletDto.userName,
      value: walletDto.balance
    }));

    walletDataset.push({
      name: 'You',
      value: currentUserWallet.value.balance
    });

    // 2. Initialize ECharts and attach to DOM element <div ref="barchart">
    // barchart.value = points to DOM element
    chartInstance = echarts.init(walletBarChart.value);

    // 3. Update chart with API data
    chartInstance.setOption({
      dataset: {source: walletDataset},
      xAxis: {type: 'category'},
      yAxis: {type: 'value'},
      series: [{
        type: 'bar',
        dimensions: ['name', 'value'],
        encode: {
          x: 'name',
          y: 'value'
        }
      }],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: (params: unknown) => {
          const tooltipParams = params as TooltipParams[] | TooltipParams;
          const param = Array.isArray(tooltipParams) ? tooltipParams[0] : tooltipParams;

          if (param && param.data) {
            const data = param.data as walletData;
            let thisBalance = data.value.toFixed(2);
            return `
                <strong>${param.name}</strong><br/>
                Balance: ${thisBalance}<br/>
              `;
          }
          return 'No data available';
        }
      }
    });

    // Use ResizeObserver for container resizing
    const resizeObserver = new ResizeObserver(() => {
      if (chartInstance) {
        chartInstance.resize();
      }
    });

    resizeObserver.observe(walletBarChart.value);
  }
});

async function onResize(){
  if(chartInstance != null){
    chartInstance.resize();
  }
}
//to avoid memory leaks, dispose chart on unmount
onUnmounted(() => {
  chartInstance?.dispose();
});

</script>

<template>
  <div class="col-12 col-sm-6 col-md-4 col-lg-3">

    <q-card class="outer-card" bordered>
      <div class="col column">

        <q-card-section class="col-8 col-sm-6">
          <div class="text-h6 header-text q-my-md">Wallet overview</div>
          <div class="text-subtitle2">How you compare</div>

          <q-card-actions>
            <q-btn
              color="grey"
              round
              flat
              dense
              :icon="expanded ? 'keyboard_arrow_down' : 'keyboard_arrow_up'"
              @click="expanded = !expanded"
            />

          </q-card-actions>
        </q-card-section>

        <q-card-section class="col-8 col-sm-6" style="flex-wrap: wrap; align-items: center;" bordered>
          <q-slide-transition v-show="expanded">
            <div ref="walletBarChart" class="inner-card-section chart-container" @resize="onResize"/>
          </q-slide-transition>
        </q-card-section>

      </div>
    </q-card>

  </div>
</template>

<style lang="scss" scoped>
@import 'src/css/quasar.variables.scss';

.outer-card {
  @include outer-card;
}

.inner-card-section {
  @include inner-card-section;
}

.chart-container {
  @include chart-container;
}


</style>
