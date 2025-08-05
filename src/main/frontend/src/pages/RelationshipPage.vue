<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import { QMarkupTable } from 'quasar';
import { RelationshipData } from 'components/dto/RelationshipData.ts';
import { UserDto } from 'components/dto/UserDto.ts';
import { RelationshipStatusDto } from 'components/dto/RelationshipStatusDto.ts';


defineOptions({
  name: 'RelationshipPage'
});

const thisUser = ref<UserDto>({
  id: 0,
  username: ''
});

const userSearch = ref<UserDto>({
  id: 0,
  username: ''
});

const searchResult = ref<RelationshipData[]>([]);
const everythingList = ref<RelationshipData[]>([]);

onMounted(async () => {
  thisUser.value = await api.get<UserDto>('/relationships/this-user').then(res => res.data)
  everythingList.value = await api.get<RelationshipData[]>('/relationships').then(res => res.data)
});

const searchFriend = async () => {
  await api.get<RelationshipData[]>(`/relationships/search?username=${userSearch.value.username}`)
    .then(response => {
      searchResult.value = response.data;
    })
    .catch(error => {
      console.log(error);
    })
};

async function sendRequest(partnerId: number) {
  await api.put(`/relationships/request/${partnerId}`)
    .then (response => {
      for(let i=0; i < response.data.length; i++) {
        everythingList.value.splice(everythingList.value.length, 0, response.data[i]);
      }
    })
    .catch(error => {
      console.log(error);
    })
}

async function deleteRequest(relationshipId: number) {
  await api.delete(`/relationships/${relationshipId}`)
    .then (response => {
      for(let i=0; i < response.data.length; i++) {
        const index = everythingList.value.findIndex(
          (relationship) => relationship.id === response.data[i]
        );
        everythingList.value.splice(index, 1);
      }
    })
    .catch(error => {
      console.log(error);
    })
}

async function updateStatus(status: string, relationshipId: number) {
  await api.post<RelationshipData>(`/relationships/${relationshipId}`, {status: status} as RelationshipStatusDto)
    .then(response => {
      const index = everythingList.value.findIndex(
        (relationship) => relationship.id === relationshipId
      );
      everythingList.value.splice(index, 1, response.data);
    })
    .catch(error => {
      console.log(error);
    })
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
                <q-btn @click="sendRequest(partner.partnerId)" label="Request" type="submit" color="primary"/>
              </template>
              <template v-else>
                <td v-text="partner.status" />
              </template>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>

      <q-markup-table title="PARTNERSHIPS TO RESPOND TO">
        <thead>
        <tr><th>REQUESTS TO ANSWER</th></tr>
        <tr>
          <th>RELATIONSHIP ID</th>
          <th>PARTNER ID</th>
          <th>NAME</th>
          <th>STATUS</th>
          <th>ACCEPT</th>
          <th>REJECT</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="relationship in everythingList" :key="relationship.id">
          <template v-if="relationship.status === 'PENDING' ">
              <template v-if="relationship.userId === thisUser.id">
                <td v-text="relationship.id" />
                <td v-text="relationship.partnerId" />
                <td v-text="relationship.partnerName" />
                <td v-text="relationship.status" />
                <td><q-btn @click="updateStatus('APPROVED', relationship.id)"
                           label="ACCEPT" type="submit" color="primary"/></td>
                <td><q-btn @click="updateStatus('REJECTED', relationship.id)"
                           label="REJECT" type="submit" color="primary"/></td>
              </template>
          </template>
        </tr>
        </tbody>
      </q-markup-table>

      <q-markup-table title="APPROVED PARTNERSHIPS">
        <thead>
        <tr><th>APPROVED PARTNERS</th></tr>
        <tr>
          <th>RELATIONSHIP ID</th>
          <th>PARTNER ID</th>
          <th>NAME</th>
          <th>STATUS</th>
          <th>REMOVE</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="relationship in everythingList" :key="relationship.id">
          <template v-if="relationship.status === 'APPROVED' ">
            <td v-text="relationship.id" />
            <template v-if="relationship.userId === thisUser.id">
              <td v-text="relationship.partnerId" />
              <td v-text="relationship.partnerName" />
            </template>
            <template v-else>
              <td v-text="relationship.userId" />
              <td v-text="relationship.userName" />
            </template>
            <td v-text="relationship.status" />
            <td><q-btn @click="deleteRequest(relationship.id)" label="DELETE" type="submit" color="primary"/></td>
          </template>
        </tr>
        </tbody>
      </q-markup-table>

      <q-markup-table title="REJECTED PARTNERSHIPS">
        <thead>
        <tr><th>REJECTED PARTNERS</th></tr>
        <tr>
          <th>RELATIONSHIP ID</th>
          <th>PARTNER ID</th>
          <th>NAME</th>
          <th>STATUS</th>
          <th>REMOVE</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="relationship in everythingList" :key="relationship.id">
          <template v-if="relationship.status === 'REJECTED' ">
            <td v-text="relationship.id" />
            <template v-if="relationship.userId === thisUser.id">
              <td v-text="relationship.partnerId" />
              <td v-text="relationship.partnerName" />
              <td v-text="relationship.status" />
              <td><q-btn @click="deleteRequest(relationship.id)" label="DELETE" type="submit" color="primary"/></td>
            </template>
            <template v-else>
              <td v-text="relationship.userId" />
              <td v-text="relationship.userName" />
              <td v-text="relationship.status" />
              <td> </td>
            </template>
          </template>
        </tr>
        </tbody>
      </q-markup-table>

      <q-markup-table title="PARTNERSHIPS WAITING FOR ANSWER">
        <thead>
        <tr><th>REQUESTS STILL PENDING</th></tr>
        <tr>
          <th>RELATIONSHIP ID</th>
          <th>PARTNER ID</th>
          <th>NAME</th>
          <th>STATUS</th>
          <th>DELETE</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="relationship in everythingList" :key="relationship.id">
          <template v-if="relationship.partnerId === thisUser.id">
              <template v-if="relationship.status === 'PENDING'">
                <td v-text="relationship.id" />
                <td v-text="relationship.userId" />
                <td v-text="relationship.userName" />
                <td v-text="relationship.status" />
                <td><q-btn @click="deleteRequest(relationship.id)" label="DELETE" type="submit" color="primary"/></td>
              </template>
          </template>
        </tr>
        </tbody>
      </q-markup-table>


    </div>
</template>
