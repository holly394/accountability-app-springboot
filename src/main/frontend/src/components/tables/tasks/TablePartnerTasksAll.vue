<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {TaskDataDto} from 'components/dto/task/TaskDataDto.ts';
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {onMounted, ref} from 'vue';
import {taskData} from 'src/composables/TaskData.ts';
import {relationshipData} from 'src/composables/RelationshipData.ts';
const { getAllTasksByUserList } = taskData();
const { getPartnerIdList } = relationshipData();

defineOptions({
  name: 'TablePartnerTasksAll',
});

const expanded = ref<boolean>(false);
const currentPage = ref<number>(0);
const maxPages = ref<number>(0);
const pageSize = 5;

const recentPartnerTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);
const partnerList = ref<number[]>([]);

onMounted(async () => {
  await reloadPartnerTaskList();
})

const reloadPartnerTaskList = async() => {
  partnerList.value = await getPartnerIdList();
  recentPartnerTasks.value = await getAllTasksByUserList(partnerList.value, currentPage.value-1, pageSize);
  currentPage.value = recentPartnerTasks.value.pageNumber+1;
  maxPages.value = recentPartnerTasks.value.totalPages;
}

async function changePage() {
  await reloadPartnerTaskList();
}

</script>

<template>
  <div class="col-12 col-sm-6 col-md-4 col-lg-3 container">

    <q-card class="outer-card col-auto" bordered>
      <div class="col column ">

        <q-card-section class="col-8 col-sm-6"
                        style="flex-wrap: wrap;
                        align-items: center;"
                        bordered>

          <div class="text-h6 header-text q-my-md">Recent tasks by partners</div>
          <div class="text-subtitle2">See most recent tasks from your partners</div>

          <q-card-actions>
            <q-btn
              color="grey"
              round
              flat
              dense
              :icon="expanded ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
              @click="expanded = !expanded"
            />
          </q-card-actions>

        </q-card-section>

        <q-card-section class="col-8 col-sm-6"
                        style="flex-wrap: wrap;
                        align-items: center;"
                        bordered>

          <q-slide-transition>
            <div v-show="expanded">

              <q-markup-table title="TASKS" class="col-8 col-sm-6">

                <thead>
                  <tr>
                    <th>User</th>
                    <th>Task Description</th>
                    <th>Status</th>
                    <th>Time Spent</th>
                  </tr>
                </thead>

                <tbody>
                  <tr v-for="task in recentPartnerTasks.content"
                      :key="task.id" class="text-center">
                      <td v-text="task.userName" />
                      <td v-text="task.description" />
                      <td v-text="task.status" />
                      <td v-text="task.durationString" />
                  </tr>
                </tbody>

                <q-pagination
                  v-model="currentPage"
                  :max="maxPages"
                  @click="changePage()"
                />

              </q-markup-table>

            </div>
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

</style>

