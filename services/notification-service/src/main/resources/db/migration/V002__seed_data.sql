--------------------------------------------------------
--  SEED
--------------------------------------------------------
INSERT INTO channels
(id, created_at, created_by, last_modified_at, last_modified_by, "version", channel_type)
VALUES
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'EMAIL'),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'SMS');

INSERT INTO providers
(id, created_at, created_by, last_modified_at, last_modified_by, "version", is_active, "name", channel_id)
VALUES
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, true, 'OTG_EMAIL',(select c.id from channels c where c.channel_type='EMAIL')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, true, 'OTG_SMS',(select c.id from channels c where c.channel_type='SMS'));

INSERT INTO provider_configs
(id, created_at, created_by, last_modified_at, last_modified_by, "version", config_data_type, "key", value, provider_id)
VALUES
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'STRING', 'apiUrl', 'https://www.google.com/CommunicationRestService/mail', (select c.id from providers c where c."name"='OTG_EMAIL')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'STRING', 'channel', 'NACH', (select c.id from providers c where c."name"='OTG_EMAIL')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'STRING', 'apiUrl', 'https:///www.google.com/CommunicationRestService/sendSMS', (select c.id from providers c where c."name"='OTG_SMS')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'STRING', 'channel', 'HFT', (select c.id from providers c where c."name"='OTG_SMS'));

INSERT INTO templates
(id, created_at, created_by, last_modified_at, last_modified_by, "version", "language", "template", subject, template_code)
VALUES
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'en',
'A secret code only between us. Use OTP {{event.otpValue}} for Login to Merchant Banking App. Valid till 00:02:58. - Bank'::bytea
, '','T001'),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'en',
'A secret code only between us. Use OTP {{event.otpValue}} for Login to Merchant Banking Web. Valid till 00:02:58. - Bank'::bytea
, '','T002'),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'en',
'A secret code only between us. Use OTP {{event.otpValue}} for Login to Merchant Banking App. Valid till 00:02:58. - Bank'::bytea
, 'A secret code for Login to Merchant Banking App','T003'),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'en',
'A secret code only between us. Use OTP {{event.otpValue}} for Login to Merchant Banking Web. Valid till 00:02:58. - Bank'::bytea
, 'A secret code for Login to Merchant Banking Web','T004');

INSERT INTO rules
(id, created_at, created_by, last_modified_at, last_modified_by, "version", event_type, "language", trigger_expression, channel_id, template_id)
VALUES
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'APP_OTP_SMS', 'en', '', (select c.id from channels c where c.channel_type='SMS'), (select c.id from templates c where c.template_code='T001')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'WEB_OTP_SMS', 'en', '', (select c.id from channels c where c.channel_type='SMS'), (select c.id from templates c where c.template_code='T002')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'APP_OTP_EMAIL', 'en', '', (select c.id from channels c where c.channel_type='EMAIL'), (select c.id from templates c where c.template_code='T003')),
(md5(random()::text || clock_timestamp()::text)::uuid, now(), 'system', now(),'system', 0, 'WEB_OTP_EMAIL', 'en', '', (select c.id from channels c where c.channel_type='EMAIL'), (select c.id from templates c where c.template_code='T004'));