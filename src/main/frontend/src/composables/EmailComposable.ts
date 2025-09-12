import {GenericResponseDto} from 'components/dto/GenericResponseDto.ts';
import axios from 'axios';
import {ResetPasswordDto} from "components/dto/ResetPasswordDto.ts";

export function email() {

  const api = axios.create({
    baseURL: '/',
    headers: { 'Content-Type': 'application/json', },
    maxRedirects: 0
  });

  const sendEmailResetPassword = async (email: string): Promise<GenericResponseDto> => {
    return (await api.get<GenericResponseDto>('/email/send-token', {
      params: {
        email: email
      },
      paramsSerializer: {
        indexes: null
      }
    })).data;
  }

  const setNewPassword = async (token: string,
                                resetPasswordDto: ResetPasswordDto):
    Promise<GenericResponseDto> => {
    return (await api.post<GenericResponseDto>(`/email/set-new-password/${token}`,
      resetPasswordDto)).data;
  }

  return { sendEmailResetPassword, setNewPassword };
}
