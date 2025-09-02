package com.github.holly.accountability.wallet;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;


public class WalletEntityListener {

    private static WalletHistoryRepository repository;

    @Autowired
    public void setRepository(WalletHistoryRepository repository) {
        WalletEntityListener.repository = repository;
    }

    @PostPersist
    public void postPersist(Wallet target) {
        var history = new WalletHistory(target.getUser().getId(), target.getId(), target.getBalance(), LocalDateTime.now());
        repository.save(history);
    }

    @PostUpdate
    public void postUpdate(Wallet target) {
        var history = new WalletHistory(target.getUser().getId(), target.getId(), target.getBalance(), LocalDateTime.now());
        repository.save(history);
    }

    @PostRemove
    public void postDelete(Wallet target) {
    }
}