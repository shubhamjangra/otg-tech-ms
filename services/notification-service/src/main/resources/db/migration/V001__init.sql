CREATE TABLE channels
  (
     id               VARCHAR(255) NOT NULL,
     created_at       TIMESTAMP,
     created_by       VARCHAR(255),
     last_modified_at TIMESTAMP,
     last_modified_by VARCHAR(255),
     version          INT4 NOT NULL,
     channel_type     VARCHAR(255),
     PRIMARY KEY (id)
  );

CREATE TABLE notification_events
  (
     id                        VARCHAR(255) NOT NULL,
     created_at                TIMESTAMP,
     created_by                VARCHAR(255),
     last_modified_at          TIMESTAMP,
     last_modified_by          VARCHAR(255),
     version                   INT4 NOT NULL,
     customer_data             JSON,
     event_data                JSON,
     event_type                VARCHAR(255),
     idempotency_key           VARCHAR(255),
     notification_event_status VARCHAR(255),
     retry_attempts int8       DEFAULT 0,
     PRIMARY KEY (id)
  );

CREATE TABLE notifications
  (
     id                    VARCHAR(255) NOT NULL,
     created_at            TIMESTAMP,
     created_by            VARCHAR(255),
     last_modified_at      TIMESTAMP,
     last_modified_by      VARCHAR(255),
     version               INT4 NOT NULL,
     channel               VARCHAR(255),
     notification_event_id VARCHAR(255),
     channel_id            VARCHAR(255),
     provider              VARCHAR(255),
     provider_id           VARCHAR(255),
     template_id           VARCHAR(255),
     language              VARCHAR(255),
     rule_id               VARCHAR(255),
     event_type            VARCHAR(255),
     read_status           VARCHAR(255),
     notification_body     character varying(10485760),
     response_body         VARCHAR(255),
     user_id               VARCHAR(255),
     PRIMARY KEY (id)
  );

CREATE TABLE provider_configs
  (
     id               VARCHAR(255) NOT NULL,
     created_at       TIMESTAMP,
     created_by       VARCHAR(255),
     last_modified_at TIMESTAMP,
     last_modified_by VARCHAR(255),
     version          INT4 NOT NULL,
     config_data_type VARCHAR(255),
     KEY              VARCHAR(255),
     value            VARCHAR(255),
     provider_id      VARCHAR(255),
     PRIMARY KEY (id)
  );

CREATE TABLE providers
  (
     id               VARCHAR(255) NOT NULL,
     created_at       TIMESTAMP,
     created_by       VARCHAR(255),
     last_modified_at TIMESTAMP,
     last_modified_by VARCHAR(255),
     version          INT4 NOT NULL,
     is_active        BOOLEAN NOT NULL,
     NAME             VARCHAR(255),
     channel_id       VARCHAR(255),
     PRIMARY KEY (id)
  );

CREATE TABLE rules
  (
     id                       VARCHAR(255) NOT NULL,
     created_at               TIMESTAMP,
     created_by               VARCHAR(255),
     last_modified_at         TIMESTAMP,
     last_modified_by         VARCHAR(255),
     version                  INT4 NOT NULL,
     event_type               VARCHAR(255),
     language                 VARCHAR(255),
     trigger_expression       VARCHAR(255),
     channel_id               VARCHAR(255),
     template_id              VARCHAR(255),
     is_retry_enabled boolean DEFAULT FALSE,
     PRIMARY KEY (id)
  );

CREATE TABLE templates
  (
     id               VARCHAR(255) NOT NULL,
     created_at       TIMESTAMP,
     created_by       VARCHAR(255),
     last_modified_at TIMESTAMP,
     last_modified_by VARCHAR(255),
     version          INT4 NOT NULL,
     language         VARCHAR(255),
     template         BYTEA,
     template_code    VARCHAR(255),
     subject          VARCHAR(255),
     PRIMARY KEY (id)
  );

CREATE TABLE otp_log
  (
     id                       VARCHAR(255) NOT NULL,
     created_at               TIMESTAMP WITH time zone NOT NULL,
     created_by               VARCHAR(255) NULL,
     last_modified_at         TIMESTAMP WITH time zone NULL,
     last_modified_by         VARCHAR(255) NULL,
     "version"                INT4 NOT NULL,
     mobile_no                VARCHAR(20) NOT NULL,
     otp_attempts             int8 NULL,
     invalid_otp_attempts     int8 NULL,
     otp_value                VARCHAR(255) NOT NULL,
     otp_id                   VARCHAR(50),
     send_otp_status_code     VARCHAR(255),
     send_otp_status_desc     VARCHAR(255),
     ip_address               VARCHAR(255) NOT NULL,
     valid_otp_flag           CHAR(1) NOT NULL,
     send_otp_at              TIMESTAMP,
     validate_otp_at          TIMESTAMP,
     validate_otp_status_code VARCHAR(255),
     validate_otp_status_desc VARCHAR(255),
     PRIMARY KEY (id)
  );

CREATE TABLE scheduled_tasks
  (
     task_name            VARCHAR(100),
     task_instance        VARCHAR(100),
     task_data            OID,
     execution_time       TIMESTAMP WITH time zone,
     picked               BOOLEAN,
     picked_by            VARCHAR(50),
     last_success         TIMESTAMP WITH time zone,
     last_failure         TIMESTAMP WITH time zone,
     consecutive_failures INT,
     last_heartbeat       TIMESTAMP WITH time zone,
     version              BIGINT,
     PRIMARY KEY (task_name, task_instance)
  );

ALTER TABLE notifications
  ADD CONSTRAINT fkq3yojuirhcrs8e5d00ggo8xrn FOREIGN KEY (notification_event_id)
  REFERENCES notification_events;

ALTER TABLE provider_configs
  ADD CONSTRAINT fkfaydgt37bkbjb4mcnp1wcnd7r FOREIGN KEY (provider_id)
  REFERENCES providers;

ALTER TABLE providers
  ADD CONSTRAINT fklfwrwv0ju748ee2097lyfq0w6 FOREIGN KEY (channel_id) REFERENCES
  channels;

ALTER TABLE rules
  ADD CONSTRAINT fk4gggokvc97kixn53q6j6d7ca3 FOREIGN KEY (channel_id) REFERENCES
  channels;

ALTER TABLE rules
  ADD CONSTRAINT fk3qgh08jj4ljwuo6u1yjwd4exj FOREIGN KEY (template_id)
  REFERENCES templates;

ALTER TABLE templates
  ADD CONSTRAINT unique_template_code UNIQUE (template_code);