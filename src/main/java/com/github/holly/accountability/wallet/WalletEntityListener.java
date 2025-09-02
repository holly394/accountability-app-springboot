package com.github.holly.accountability.wallet;

import com.github.holly.accountability.config.BeanContextHelper;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.time.LocalDateTime;

public class WalletEntityListener {

    @PostPersist
    public void postPersist(Wallet target) {
        WalletHistoryRepository repository = BeanContextHelper.getBean(WalletHistoryRepository.class);
        var history = new WalletHistory(target.getId(), target.getUser().getId(), target.getBalance(), LocalDateTime.now());
        repository.save(history);
    }

    @PostUpdate
    public void postUpdate(Wallet target) {
        WalletHistoryRepository repository = BeanContextHelper.getBean(WalletHistoryRepository.class);
        var history = new WalletHistory(target.getId(), target.getUser().getId(), target.getBalance(), LocalDateTime.now());
        repository.save(history);
    }

    @PostRemove
    public void postDelete(Wallet target) {
    }

}