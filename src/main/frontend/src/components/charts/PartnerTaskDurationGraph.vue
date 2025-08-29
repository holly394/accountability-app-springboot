<template>
  <div>
    <q-card
      class="rounded-outer-card text-white no-shadow q-pt-none"
      bordered>
      <q-card-section>
        <div class="text-h6">Longest approved tasks</div>
        <div class="text-subtitle2">Among yours and your partners' tasks</div>
      </q-card-section>
      <q-card-section class="rounded-inner-card" bordered>
        <div ref="partnerTaskBarChart" style="width: 500px; height: 500px;" />
      </q-card-section>
    </q-card>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import {ECharts} from 'echarts';
import {DefaultPage, Page} from "components/paging/Page.ts";
import { taskData } from 'src/composables/TaskData.ts';
import { relationshipData } from "src/composables/RelationshipData.ts";
import { TaskDataDto } from "components/dto/task/TaskDataDto.ts";
import {TaskStatus} from "components/dto/task/TaskStatus.ts";
import {userData} from "src/composables/UserData.ts";
import {UserDto} from "components/dto/UserDto.ts";

const {getCurrentUserInfo} = userData();
const {getTasksByUserListAndStatusOrderByDuration} = taskData();
const { getPartnerIdList } = relationshipData();

defineOptions({
  name: 'PartnerTaskDurationGraph'
});

// ECharts instance (typed as ECharts or null)
let chartInstance: ECharts | null = null;

//in template, we have <div> with ref="barchart"
// DOM reference (strictly typed as HTMLDivElement or null)
const partnerTaskBarChart = ref<HTMLDivElement | null>(null);

const userIdList = ref<number[]>([]);

const currentUserId = ref<UserDto>({
  id: 0,
  username: ''
});

const tasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);

// Fetch data and initialize chart
onMounted(async () => {
  // 1. Fetch data from API
  currentUserId.value = await getCurrentUserInfo();
  userIdList.value = await getPartnerIdList();
  userIdList.value.push(currentUserId.value.id);

  tasks.value = await getTasksByUserListAndStatusOrderByDuration(userIdList.value, TaskStatus.APPROVED);
  // 2. Initialize ECharts and attach to DOM element <div ref="barchart">
  // barchart.value = points to DOM element
  chartInstance = echarts.init(partnerTaskBarChart.value);

  // 3. Update chart with API data
  chartInstance.setOption({
    dataset: {
      dimensions: [{name: 'description', type: 'ordinal'},
        {name:'durationNumber', type: 'number'},
        {name: 'userName', type: 'ordinal'},
        {name: 'durationString', type: 'ordinal'}],
      source: tasks.value.content,
    },
    xAxis: {type: 'value'},
    yAxis: {type: 'category', inverse: true},
    series: [{
      realtimeSort: true,
      type: 'bar',
      encode: {
        x: 'durationNumber',
        y: 'description',
        tooltip: ['userName', 'durationString']
      }
    }],
    tooltip: {trigger: 'item'}
  });

});

//to avoid memory leaks, dispose chart on unmount
onUnmounted(() => {
  chartInstance?.dispose();
});

</script>

<style lang="scss" scoped>
@import 'src/css/quasar.variables.scss';

.rounded-outer-card {
  @include card-style;
}
.rounded-inner-card {
  @include card-section-style;
}

</style>
