package com.baofeng.blog.service.sentence.impl;

import com.baofeng.blog.dto.common.OneSentenceDTO;
import com.baofeng.blog.service.sentence.SentenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class JinrishiciProvider implements SentenceProvider {

    private static final Logger log =
            LoggerFactory.getLogger(JinrishiciProvider.class);

    private static final String API =
            "https://v2.jinrishici.com/one.json";

    private final RestClient restClient;

    public JinrishiciProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String fetch() {
        try {
            OneSentenceDTO.PoetryToday res =
                    restClient.get()
                            .uri(API)
                            .retrieve()
                            .body(OneSentenceDTO.PoetryToday.class);

            if (res != null
                    && "success".equals(res.getStatus())
                    && res.getData() != null) {
                return res.getData().getContent();
            }
            return null;

        } catch (RestClientException e) {
            log.warn("[Jinrishici] request failed: {}", e.getMessage());
            return null;
        }
    }
}
