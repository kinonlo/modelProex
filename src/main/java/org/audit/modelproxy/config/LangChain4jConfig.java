package org.audit.modelproxy.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${langchain4j.dashscope.api-key}") String apiKey,
            @Value("${langchain4j.dashscope.chat-model.model-name}") String modelName,
            @Value("${langchain4j.dashscope.chat-model.temperature:0.7}") Float temperature) {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
    }
}
