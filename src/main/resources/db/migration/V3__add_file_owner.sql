ALTER TABLE spring_files
    ADD COLUMN IF NOT EXISTS id_user bigint NOT NULL DEFAULT 1;

CREATE INDEX IF NOT EXISTS spring_files_id_user_idx
    ON spring_files (id_user);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'spring_files_spring_users_fk'
    ) THEN
        ALTER TABLE spring_files
            ADD CONSTRAINT spring_files_spring_users_fk
                FOREIGN KEY (id_user) REFERENCES spring_users(id_user) ON DELETE CASCADE;
    END IF;
END $$;
