<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {QMarkupTable} from 'quasar';
import {RelationshipDto} from 'components/dto/relationship/RelationshipDto.ts';
import {UserDto} from 'components/dto/UserDto.ts';
import PartnershipsRequestsToWait from 'components/tables/partnerships/PartnershipsRequestsToWait.vue';
import PartnershipsRejected from 'components/tables/partnerships/PartnershipsRejected.vue';
import PartnershipsRequestsToAnswer from 'components/tables/partnerships/PartnershipsRequestsToAnswer.vue';
import {relationshipData} from 'src/composables/RelationshipData.ts'
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {RelationshipStatus} from 'components/dto/relationship/RelationshipStatus.ts';
import PartnershipsApproved from 'components/tables/partnerships/PartnershipsApproved.vue';
import {userData} from 'src/composables/UserData.ts';
import {RelationshipDirection} from 'components/dto/relationship/RelationshipDirection.ts';

const { getCurrentUserInfo } = userData();

const { search, sendRequest, getRelationshipsByStatus, getRequests } = relationshipData();

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

const searchResult = ref<RelationshipDto[]>([]);
const approvedRelationships = ref<Page<RelationshipDto>>(DefaultPage as Page<RelationshipDto>);

const requestsToAnswer = ref<Page<RelationshipDto>>(DefaultPage as Page<RelationshipDto>);
const requestsToWait = ref<Page<RelationshipDto>>(DefaultPage as Page<RelationshipDto>);

const rejectionsSent = ref<Page<RelationshipDto>>(DefaultPage as Page<RelationshipDto>);
const rejectionsReceived = ref<Page<RelationshipDto>>(DefaultPage as Page<RelationshipDto>);

onMounted( async () => {
  currentUser.value = await getCurrentUserInfo();
  approvedRelationships.value = await getRelationshipsByStatus(RelationshipStatus.APPROVED);

  requestsToAnswer.value = await getRequests(RelationshipStatus.PENDING, RelationshipDirection.SENDER);
  requestsToWait.value = await getRequests(RelationshipStatus.PENDING, RelationshipDirection.RECEIVER);

  rejectionsSent.value = await getRequests(RelationshipStatus.REJECTED, RelationshipDirection.SENDER);
  rejectionsReceived.value = await getRequests(RelationshipStatus.REJECTED, RelationshipDirection.RECEIVER);
})

const searchFriend = async () => {
  searchResult.value = await search(userSearch.value.username);
};

async function sendPartnershipRequest(partnerId: number) {
  await sendRequest(partnerId);
  await searchFriend();
}

async function reloadRequestsToAnswer() {
  requestsToAnswer.value = await getRequests(RelationshipStatus.PENDING, RelationshipDirection.SENDER);
}

async function reloadRequestsToWait() {
  requestsToAnswer.value = await getRequests(RelationshipStatus.PENDING, RelationshipDirection.RECEIVER);
}

async function reloadApprovedPartners() {
  approvedRelationships.value = await getRelationshipsByStatus(RelationshipStatus.APPROVED);
}

async function reloadRejectedPartners() {
  rejectionsSent.value = await getRequests(RelationshipStatus.REJECTED, RelationshipDirection.SENDER);
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
            <tr v-for="relationship in searchResult" :key="relationship.partner.id">
                <td v-text="relationship.partner.id" />
                <td v-text="relationship.partner.username" />
              <!-- relationship.status is a RelationshipStatusDto that also has a status -->
              <template v-if="relationship.status === null">
                <q-btn @click="sendPartnershipRequest(relationship.partner.id)" label="Request" type="submit" color="primary"/>
              </template>
              <template v-else>
                <td v-text="relationship.status" />
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
