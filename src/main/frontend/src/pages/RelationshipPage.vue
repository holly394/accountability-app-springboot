<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {QMarkupTable} from 'quasar';
import {RelationshipData} from 'components/dto/RelationshipData.ts';
import {UserDto} from 'components/dto/UserDto.ts';
import PartnershipsRequestsToWait from 'components/tables/PartnershipsRequestsToWait.vue';
import PartnershipsRejected from 'components/tables/PartnershipsRejected.vue';
import PartnershipsRequestsToAnswer from 'components/tables/PartnershipsRequestsToAnswer.vue';
import {relationshipData} from 'src/composables/RelationshipData.ts'
import {DefaultPage, Page} from "components/paging/Page.ts";
import {RelationshipStatus} from "components/dto/RelationshipStatus.ts";
import PartnershipsApproved from "components/tables/PartnershipsApproved.vue";
import {userData} from "src/composables/UserData.ts";

const { getCurrentUserInfo } = userData();

const { search, sendRequest, getPartnersByStatus, getRequestsToAnswer,
  getRequestsToWait, getRejectionsSent, getRejectionsReceived } = relationshipData();

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

const requestsToAnswer = ref<Page<RelationshipData>>(DefaultPage as Page<RelationshipData>);
const requestsToWait = ref<Page<RelationshipData>>(DefaultPage as Page<RelationshipData>);

const rejectionsSent = ref<Page<RelationshipData>>(DefaultPage as Page<RelationshipData>);
const rejectionsReceived = ref<Page<RelationshipData>>(DefaultPage as Page<RelationshipData>);

onMounted( async () => {
  currentUser.value = await getCurrentUserInfo();
  approvedRelationships.value = await getPartnersByStatus(RelationshipStatus.APPROVED);

  requestsToAnswer.value = await getRequestsToAnswer();
  requestsToWait.value = await getRequestsToWait();

  rejectionsSent.value = await getRejectionsSent();
  rejectionsReceived.value = await getRejectionsReceived();
})

const searchFriend = async () => {
  searchResult.value = await search(userSearch.value.username);
};

async function sendPartnershipRequest(partnerId: number) {
  await sendRequest(partnerId);
  await searchFriend();
}

async function reloadRequestsToAnswer() {
  requestsToAnswer.value = await getRequestsToAnswer();
}

async function reloadRequestsToWait() {
  requestsToAnswer.value = await getRequestsToWait();
}

async function reloadApprovedPartners() {
  approvedRelationships.value = await getPartnersByStatus(RelationshipStatus.APPROVED);
}

async function reloadRejectedPartners() {
  rejectionsSent.value = await getRejectionsSent();
}

async function reloadListsAfterAnswering() {
  await Promise.all([reloadRequestsToAnswer(), reloadApprovedPartners(), reloadRejectedPartners()]);
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
              name="partner search bar"
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

      <PartnershipsRequestsToAnswer
        :partnerList="requestsToAnswer"
        @update-relationship="reloadListsAfterAnswering"
      />

      <PartnershipsRequestsToWait
        :partnerList="requestsToWait"
        @delete-relationship="reloadRequestsToWait"
      />

      <PartnershipsApproved
        :currentUser="currentUser"
        :partnerList="approvedRelationships"
        @delete-relationship="reloadApprovedPartners"
      />

      <!-- remember this is how comments in HTML work! -->

      <PartnershipsRejected
       :rejectionsSent="rejectionsSent"
       :rejectionsReceived="rejectionsReceived"
       @delete-relationship="reloadRejectedPartners"
      />

    </div>
</template>
