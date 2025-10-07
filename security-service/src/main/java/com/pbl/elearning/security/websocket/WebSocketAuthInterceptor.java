package com.pbl.elearning.security.websocket;

import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;



@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = getJwtFromRequest(accessor);

            if (StringUtils.hasText(token) ) {
                User user = tokenProvider.getUserFromToken(token);
                if (user != null) {
                    accessor.setUser(new StompPrincipal(user.getId().toString()));
                }
            } else {
                log.warn("Invalid or missing JWT token in WebSocket connection");
            }
        }
        log.info("🔑 [WebSocketAuthInterceptor] Principal set: {}",
                accessor.getUser() != null ? accessor.getUser().getName() : "null");
        return message;
    }

    private String getJwtFromRequest(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}