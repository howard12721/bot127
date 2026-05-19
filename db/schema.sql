CREATE TABLE IF NOT EXISTS forward_rules (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    channel_id VARCHAR(36) NOT NULL,
    pattern_text VARCHAR(1000) NOT NULL,
    target_user_id VARCHAR(36) NOT NULL,
    exclude_own_messages BOOLEAN NOT NULL DEFAULT TRUE,
    exclude_bot_messages BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_forward_rules_channel_id (channel_id)
);
