CREATE TABLE client_profile (
    guid VARCHAR(255) NOT NULL,
    agency VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(255),
    dob VARCHAR(255),
    created_date_time TIMESTAMP,
    CONSTRAINT pk_clientprofile PRIMARY KEY (guid)
);
CREATE TABLE client_note (
    guid VARCHAR(255) NOT NULL,
    client_guid VARCHAR(255),
    comments VARCHAR(4000),
    logged_user VARCHAR(255),
    datetime VARCHAR(255),
    created_date_time TIMESTAMP,
    modified_date_time TIMESTAMP,
    CONSTRAINT pk_clientnote PRIMARY KEY (guid)
);

ALTER TABLE client_note
ADD CONSTRAINT FK_CLIENTNOTE_ON_CLIENT_GUID FOREIGN KEY (client_guid) REFERENCES client_profile(guid);
