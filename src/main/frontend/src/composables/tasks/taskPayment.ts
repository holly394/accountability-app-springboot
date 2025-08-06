import { api } from 'boot/axios.ts';
import {ref} from "vue";
import {TaskCalculatorDto} from "components/dto/TaskCalculatorDto.ts";

export function taskPayment() {
  const totalCompletedPayment = ref<TaskCalculatorDto>({
    payment: 0
  });
  const totalInProgressPayment = ref<TaskCalculatorDto>({
    payment: 0
  });

  const getTotalCompletedPayment = async () => {
    await api.get<TaskCalculatorDto>('/tasks/calculatePaymentCompleted')
      .then(res => {
        totalCompletedPayment.value = res.data;
      })
      .catch(err => {
        console.error(err);
      })
  }

  const getTotalInProgressPayment = async () => {
    await api.get<TaskCalculatorDto>('/tasks/calculatePaymentInProgress')
      .then(res => {
        totalInProgressPayment.value = res.data;
      })
      .catch(err => {
        console.error(err);
      })
  }

  return { totalCompletedPayment, getTotalCompletedPayment,
    totalInProgressPayment, getTotalInProgressPayment };
}
