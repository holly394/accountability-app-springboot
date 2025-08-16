import {api} from "boot/axios.ts";
import {WalletDto} from "components/dto/WalletDto.ts";
import {Page} from "components/paging/Page.ts";
import {PurchaseDto} from "components/dto/PurchaseDto.ts";

export function walletData() {

  const makePurchase = async (purchase: PurchaseDto): Promise<PurchaseDto> => {
    return (await api.post<PurchaseDto>('/wallet/makePurchase', purchase)).data;
  }

  const getCurrentUserWallet = async (): Promise<WalletDto> => {
    return (await api.get<WalletDto>('/wallet')).data;
  }

  const getCurrentUserPurchaseHistory = async (): Promise<Page<PurchaseDto>> => {
    return (await api.get<Page<PurchaseDto>>('/wallet/getPurchases')).data;
  }

  return { getCurrentUserWallet, getCurrentUserPurchaseHistory, makePurchase };
}
