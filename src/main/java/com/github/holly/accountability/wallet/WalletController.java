package com.github.holly.accountability.wallet;

import com.github.holly.accountability.purchases.Purchase;
import com.github.holly.accountability.purchases.PurchaseDto;
import com.github.holly.accountability.purchases.PurchaseRepository;
import com.github.holly.accountability.relationships.Relationship;
import com.github.holly.accountability.relationships.RelationshipRepository;
import com.github.holly.accountability.relationships.RelationshipService;
import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/wallet")
@ResponseBody
public class WalletController {
    @Autowired
    WalletRepository walletRepository;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletService walletService;

    @Autowired
    RelationshipRepository relationshipRepository;

    @Autowired
    RelationshipService relationshipService;

    @GetMapping("")
    public WalletDto getWallet(@AuthenticationPrincipal AccountabilitySessionUser user){
        Wallet userWallet = walletRepository.findByUserId(user.getId());
        return walletService.convertWalletToWalletDto(userWallet);
    }

    @GetMapping("/get-partner-wallets")
    public List<WalletDto> getPartnerWallets(@AuthenticationPrincipal AccountabilitySessionUser user){
        List<Relationship> partnerships = relationshipRepository.getApprovedRelationshipsByUserIdBothDirections(user.getId());
        List<User> partners = relationshipService.getCleanPartnerList(partnerships, user.getId());

        return partners.stream()
                .map(response -> walletRepository.findByUserId(response.getId()))
                .map(res -> walletService.convertWalletToWalletDto(res))
                .toList();
    }

    @GetMapping("/getPurchases")
    public List<PurchaseDto> getPurchases(@AuthenticationPrincipal AccountabilitySessionUser user){
        List<Purchase> purchaseList = purchaseRepository.findByUserId(user.getId());
        return purchaseList.stream()
                .map(this::convertPurchaseToDto)
                .toList();
    }

    @PostMapping("/makePurchase")
    public PurchaseDto purchase(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody PurchaseDto purchaseDto){
        Wallet wallet = walletRepository.findByUserId(user.getId());
        float price = purchaseDto.getPrice();

        if(wallet.getBalance() < price){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        Purchase purchase = new Purchase();
        purchase.setPrice(price);
        purchase.setDescription(purchaseDto.getDescription());
        purchase.setUser(userRepository.findUserById(user.getId()));
        purchaseRepository.save(purchase);

        wallet.subtractBalance(price);
        walletRepository.save(wallet);
        return convertPurchaseToDto(purchase);
    }

    private PurchaseDto convertPurchaseToDto(Purchase purchase){
        PurchaseDto purchaseDto = new PurchaseDto();
        purchaseDto.setId(purchase.getId());
        purchaseDto.setDescription(purchase.getDescription());
        purchaseDto.setPrice(purchase.getPrice());
        if (purchase.getPurchaseTime() != null) {
            LocalDateTime purchaseTime = purchase.getPurchaseTime();
            String time = purchaseTime.toString();
            purchaseDto.setPurchaseTimeString(time);
        }
        return purchaseDto;
    }
}