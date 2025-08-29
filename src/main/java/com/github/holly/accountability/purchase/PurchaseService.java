package com.github.holly.accountability.purchase;

import com.github.holly.accountability.user.UserService;
import com.github.holly.accountability.wallet.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserService userService;
    private final WalletService walletService;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           UserService userService,
                           WalletService walletService) {
        this.purchaseRepository = purchaseRepository;
        this.userService = userService;
        this.walletService = walletService;
    }

    public Page<Purchase> getPurchasesByUserIdDescTime(Long userId, Pageable pageable){
        return purchaseRepository.findByUserIdOrderByPurchaseTimeDesc(userId, pageable);
    }

    public Purchase makePurchase(Long userId, Double price, String description){
        Purchase purchase = new Purchase();

        if(!walletService.subtractBalance(userId, price)){
            return null;
        }

        purchase.setPrice(price);
        purchase.setDescription(description);
        purchase.setUser(userService.findUserById(userId));
        purchaseRepository.save(purchase);
        return purchase;
    }

}
