ALTER TABLE dokument ADD timestamp_upload DATETIME NOT NULL DEFAULT now();
ALTER TABLE dokument_aud ADD timestamp_upload DATETIME;

UPDATE dokument SET timestamp_upload = timestamp_mutiert;