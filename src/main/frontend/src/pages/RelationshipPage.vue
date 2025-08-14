<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { QMarkupTable } from 'quasar';
import { RelationshipData } from 'components/dto/RelationshipData.ts';
import { UserDto } from 'components/dto/UserDto.ts';
import PartnershipsPending from 'components/tables/PartnershipsPending.vue';
import PartnershipsRejected from 'components/tables/PartnershipsRejected.vue';
import PartnershipsUnanswered from 'components/tables/PartnershipsUnanswered.vue';
import {relationshipData} from 'src/composables/relationshipData.ts'
import {DefaultPage, Page} from "components/paging/Page.ts";
import {RelationshipStatus} from "components/dto/RelationshipStatus.ts";
import PartnershipsApproved from "components/tables/PartnershipsApproved.vue";
import {userData} from "src/composables/UserData.ts";
const { getCurrentUserInfo } = userData();

const { search, sendRequest, getPartnersByStatus, getUnansweredRelationshipData } = relationshipData();

defineOptions({
  name: 'RelationshipPage'
});

const currentUser = ref<UserDto>({
  id: 0,
  username: ''
});

const userSearch = ref<UserDto>({
  id: 0,
  username: ''
});

const searchResult = ref<RelationshipData[]>([]);
const approvedRelationships = ref<Page<RelationshipData>>(DefaultPage as Page<RelationshipData>);
const unansweredRelationships = ref<Page<RelationshipData>>(DefaultPage as Page<RelationshipData>);

onMounted( async () => {
  currentUser.value = await getCurrentUserInfo();
  approvedRelationships.value = await getPartnersByStatus(RelationshipStatus.APPROVED);
  unansweredRelationships.value = await getUnansweredRelationshipData();
})

const searchFriend = async () => {
  searchResult.value = await search(userSearch.value.username);
};

async function sendPartnershipRequest(partnerId: number) {
  await sendRequest(partnerId);
  await searchFriend();
}

async function reloadApprovedPartnerList() {
  approvedRelationships.value = await getPartnersByStatus(RelationshipStatus.APPROVED);
}

async function reloadUnansweredList() {
  unansweredRelationships.value = await getUnansweredRelationshipData();
}

async function reloadApprovedAndUnansweredList() {
  await reloadApprovedPartnerList();
  await reloadUnansweredList();
}

</script>

<template>
    <div class="q-pa-md row items-start q-gutter-md">

      <q-card dark bordered class="bg-grey-9 my-card">
        <q-card-section>
          <div class="text-h6">Find a friend</div>
        </q-card-section>

        <q-card-section>
            <q-input
              v-on:keyup="searchFriend"
              v-model="userSearch.username"
              label="search for a friend"
              filled
              type="textarea"
            />
        </q-card-section>

        <q-card-section>
          <q-markup-table title="EXISTING USERS">
            <thead>
            <tr><th>EXISTING USERS</th></tr>
            <tr>
              <th>ID</th>
              <th>USERNAME</th>
              <th>REQUEST</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="partner in searchResult" :key="partner.id">
                <td v-text="partner.partnerId" />
                <td v-text="partner.partnerName" />
              <template v-if="partner.status === null">
                <q-btn @click="sendPartnershipRequest(partner.partnerId)" label="Request" type="submit" color="primary"/>
              </template>
              <template v-else>
                <td v-text="partner.status" />
              </template>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>

      <PartnershipsApproved
        :currentUser="currentUser"
        :partnerList="approvedRelationships"
        @delete-relationship="reloadApprovedPartnerList"
      />

      <PartnershipsUnanswered
        :partnerList="unansweredRelationships"
        @update-relationship="reloadApprovedAndUnansweredList"
      />

      <PartnershipsPending />
      <PartnershipsRejected />

    </div>
</template>
