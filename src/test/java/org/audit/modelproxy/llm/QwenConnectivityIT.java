package org.audit.modelproxy.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class QwenConnectivityIT {
    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Test
    public void generate_shouldWork_whenDashScopeApiKeyProvided() {
        log.info("Starting Qwen connectivity test...");
        long startTimeMillis = System.currentTimeMillis();
        Response<AiMessage> response = chatLanguageModel.generate(
                UserMessage.from("Reply with exactly: pong"));
        long costMillis = System.currentTimeMillis() - startTimeMillis;

        String contentText = response == null || response.content() == null ? null : response.content().text();
        String preview = contentText == null ? null : contentText.substring(0, Math.min(200, contentText.length()));
        log.info("Qwen response received. costMillis={}, preview={}", costMillis, preview);
        System.out.println("[QwenConnectivityIT] costMillis=" + costMillis + ", preview=" + preview);
        log.info("response{}",response);
        assertThat(response).isNotNull();
        assertThat(response.content()).isNotNull();
        assertThat(response.content().text()).isNotBlank();
    }
}
