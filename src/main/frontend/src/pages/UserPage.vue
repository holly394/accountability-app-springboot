<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import {AxiosError} from 'axios';
import {useRouter} from 'vue-router';
import {useQuasar} from 'quasar';
import {UserDto} from 'components/dto/UserDto.ts';

const user = ref<UserDto>( {
  username: '',
  id: 0
});

defineOptions({
  name: 'UserPage'
});

const router = useRouter();
const $q = useQuasar();

onMounted(async () => {
  user.value = await api.get<UserDto>('/user').then(res => res.data)
});

const attemptLogOut = async () => {
  await api.post('/logout')
    .then(() => router.push('/login'))
    .catch((err: AxiosError)  => {
      if(err.response?.status === 401) {
        $q.notify( {
          message: 'Log out failed.',
          position: 'top-right',
          color: 'red',
          badgeColor: 'red'
        })
      }
    })
}

</script>

<template>
  <div>
  <q-card dark bordered class="bg-grey-9 my-card">
    <q-card-section>
      <div class="text-h6">Log Out</div>
    </q-card-section>
    <q-card-section>
        <q-btn
          label="LogOut"
          type="button"
          color="primary"
          @click="attemptLogOut"
        />
    </q-card-section>
  </q-card>

    <q-card dark bordered class="bg-grey-9 my-card">
      <q-card-section>
        <div class="text-h6">Your partners</div>
      </q-card-section>
      <q-card-section>

      </q-card-section>
    </q-card>

  </div>
</template>
