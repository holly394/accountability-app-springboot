<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import { QMarkupTable } from 'quasar';
import { RelationshipData } from 'components/dto/RelationshipData.ts';
import { UserDto } from 'components/dto/UserDto.ts';
import { RelationshipStatusDto } from 'components/dto/RelationshipStatusDto.ts';

defineOptions({
  name: 'PartnershipsUnanswered'
});

const thisUser = ref<UserDto>({
  id: 0,
  username: ''
});

const everythingList = ref<RelationshipData[]>([]);

onMounted(async () => {
  thisUser.value = await api.get<UserDto>('/relationships/this-user').then(res => res.data)
  everythingList.value = await api.get<RelationshipData[]>('/relationships').then(res => res.data)
});

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
    <div>
      <q-card
        class="my-card text-white"
        style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
      >
        <q-card-section>
          <div class="text-h6">Pending requests</div>
          <div class="text-subtitle2">Answer partnership requests here</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-markup-table title="PARTNERSHIPS TO RESPOND TO">
            <thead>
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
        </q-card-section>
      </q-card>
    </div>
</template>
