package com.github.holly.accountability.wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void addTaskToWallet(Long userId, Double payment){
        Wallet wallet = walletRepository.findByUserId(userId);
        wallet.addBalance(payment);
        walletRepository.save(wallet);
    }

    public Wallet findWalletByUserId(Long userId){
        return walletRepository.findByUserId(userId);
    }

    public Page<Wallet> findWalletsByUserIds(List<Long> userIds, Pageable pageable){
        return walletRepository.findByUserList(userIds, pageable);
    }

    public boolean subtractBalance(Long userId, Double price){
        Wallet wallet = walletRepository.findByUserId(userId);

        if (wallet.getBalance() < price){
            return false;
        }

        wallet.subtractBalance(price);
        walletRepository.save(wallet);
        return true;
    }

}
