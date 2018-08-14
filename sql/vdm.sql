DROP TABLE IF EXISTS vdm.SERVICE_CATALOG;
CREATE TABLE vdm.SERVICE_CATALOG(
    ITEM_SEQ         	SERIAL PRIMARY KEY,
    ITEM_ID          	VARCHAR,
    ITEM_NM          	VARCHAR,
    ITEM_PATH			VARCHAR
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA vdm TO root;
GRANT USAGE ON SCHEMA "vdm" TO root;
GRANT ALL PRIVILEGES  ON ALL SEQUENCES IN SCHEMA "vdm" TO root;