package com.github.holly.accountability.wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletService {
    public WalletDto convertWalletToWalletDto(Wallet wallet){
        WalletDto walletDto = new WalletDto();
        walletDto.setBalance(wallet.getBalance());
        walletDto.setUserId(wallet.getUser().getId());
        walletDto.setUserName(wallet.getUser().getUsername());
        return walletDto;
    }
}
