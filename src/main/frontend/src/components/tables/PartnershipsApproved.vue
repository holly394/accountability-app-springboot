<script setup lang="ts">
//List of current user's approved partnerships
//Also includes buttons to delete partnership if desired

//WHY: having a table component for existing and approved partners
// will allow us to recreate this table in another page if needed

//Expects dependencies:

import { QMarkupTable } from 'quasar';
import { RelationshipData } from 'components/dto/RelationshipData.ts';
import { UserDto } from 'components/dto/UserDto.ts';
import {Page} from "components/paging/Page.ts";
import { relationshipData } from "src/composables/relationshipData.ts";

const { deleteRelationship } = relationshipData();

defineOptions({
  name: 'PartnershipsApproved'
});

const props = defineProps<{
  partnerList: Page<RelationshipData>
  currentUser: UserDto
}>()

const emit = defineEmits(['deleteRelationship'])

async function deleteRequest(relationshipId: number) {
  await deleteRelationship(relationshipId);
  emit('deleteRelationship');
}
</script>

<template>
    <div>
      <q-card
        class="my-card text-white"
        style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
      >
        <q-card-section>
          <div class="text-h6">Partnership overview</div>
          <div class="text-subtitle2">Your partners</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-markup-table class="text-center" title="APPROVED PARTNERSHIPS">
            <thead>
            <tr>
              <th>RELATIONSHIP ID</th>
              <th>PARTNER ID</th>
              <th>NAME</th>
              <th>STATUS</th>
              <th>REMOVE</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="relationship in props.partnerList.content" :key="relationship.id" v-ripple>
                <td v-text="relationship.id" />
                <template v-if="relationship.userId == props.currentUser.id">
                  <td v-text="relationship.partnerId" />
                  <td v-text="relationship.partnerName" />
                </template>
                <template v-else>
                  <td v-text="relationship.userId" />
                  <td v-text="relationship.userName" />
                </template>
                <td v-text="relationship.status" />
                <td><q-btn @click="deleteRequest(relationship.id)" label="DELETE" type="submit" color="primary"/></td>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>
    </div>
</template>
