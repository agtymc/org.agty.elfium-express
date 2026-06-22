DO $$
DECLARE
    system_user_id bigint;
    target_user_id bigint;
    real_users_count bigint;
BEGIN
    SELECT id_user
    INTO system_user_id
    FROM spring_users
    WHERE email = 'system@localhost' AND disabled = true
    ORDER BY id_user
    LIMIT 1;

    IF system_user_id IS NULL THEN
        RETURN;
    END IF;

    SELECT COUNT(*)
    INTO real_users_count
    FROM spring_users
    WHERE id_user <> system_user_id;

    IF real_users_count = 0 THEN
        DELETE FROM spring_users_roles WHERE id_user = system_user_id;
        DELETE FROM spring_groups WHERE id_group = 1 AND id_user = system_user_id;
        DELETE FROM spring_users WHERE id_user = system_user_id;
        RETURN;
    END IF;

    SELECT id_user
    INTO target_user_id
    FROM spring_users
    WHERE id_user <> system_user_id
    ORDER BY id_user
    LIMIT 1;

    UPDATE spring_groups
    SET id_user = target_user_id
    WHERE id_user = system_user_id;

    UPDATE spring_express
    SET id_user = target_user_id
    WHERE id_user = system_user_id;

    UPDATE spring_files
    SET id_user = target_user_id
    WHERE id_user = system_user_id;

    DELETE FROM spring_users_roles WHERE id_user = system_user_id;
    DELETE FROM spring_users WHERE id_user = system_user_id;
END $$;
