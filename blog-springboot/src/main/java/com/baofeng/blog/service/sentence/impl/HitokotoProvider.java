package com.baofeng.blog.service.sentence.impl;

import com.baofeng.blog.dto.common.OneSentenceDTO;
import com.baofeng.blog.service.sentence.SentenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HitokotoProvider implements SentenceProvider {

    private static final Logger log =
            LoggerFactory.getLogger(HitokotoProvider.class);

    private static final String API = "https://v1.hitokoto.cn/";

    private final RestClient restClient;

    public HitokotoProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String fetch() {
        try {
            OneSentenceDTO.OneSentenceAPI res =
                    restClient.get()
                            .uri(API)
                            .retrieve()
                            .body(OneSentenceDTO.OneSentenceAPI.class);

            return res != null ? res.getHitokoto() : null;

        } catch (RestClientException e) {
            log.warn("[Hitokoto] request failed: {}", e.getMessage());
            return null;
        }
    }
}
