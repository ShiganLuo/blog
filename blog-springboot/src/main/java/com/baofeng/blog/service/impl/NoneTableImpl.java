package com.baofeng.blog.service.impl;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.NoneTableService;
import com.baofeng.blog.service.sentence.SentenceProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NoneTableImpl implements NoneTableService {

    private static final String FALLBACK = "今天也要元气满满";

    private final List<SentenceProvider> providers;

    public NoneTableImpl(List<SentenceProvider> providers) {
        this.providers = providers;
    }

    @Override
    public ApiResponse<String> getOneSentence() {

        if (providers.isEmpty()) {
            return ApiResponse.success(FALLBACK);
        }

        Collections.shuffle(providers);

        for (SentenceProvider provider : providers) {
            String res = provider.fetch();
            if (res != null && !res.isBlank()) {
                return ApiResponse.success(res);
            }
        }

        return ApiResponse.success(FALLBACK);
    }
}
