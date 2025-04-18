package com.example.userservice.api.user.components;

import org.springframework.stereotype.Component;

import com.example.userservice.domain.entity.NickNameHistory;
import com.example.userservice.domain.repository.history.NickNameHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NickNameTagGenerator {

    private final NickNameHistoryRepository nickNameHistoryRepository;

    @Transactional
    public String generateTag(String baseNickName) {
        NickNameHistory nickNameHistory = nickNameHistoryRepository.findByNickNameForUpdate(baseNickName)
                .orElseGet(() -> {
                    NickNameHistory newHistory = new NickNameHistory(baseNickName, 0L);
                    return nickNameHistoryRepository.save(newHistory);
                });

        long newSeq = nickNameHistory.incrementSeqAndGet(); // 내부적으로 seq++
        nickNameHistoryRepository.save(nickNameHistory);

        return baseNickName + "_" + newSeq;
    }

}
