import {api} from "boot/axios.ts";
import {WalletDto} from "components/dto/WalletDto.ts";

export function walletData() {

  const getCurrentUserWallet = async (): Promise<WalletDto> => {
    return (await api.get<WalletDto>('/wallet')).data;
  }


  return { getCurrentUserWallet };
}
