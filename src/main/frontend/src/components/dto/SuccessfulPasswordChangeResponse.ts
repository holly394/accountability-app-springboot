import {ResetPasswordDto} from 'components/dto/ResetPasswordDto.ts';

export interface SuccessfulPasswordChangeResponse {
  message: string;
  changedPassword: ResetPasswordDto;
}
