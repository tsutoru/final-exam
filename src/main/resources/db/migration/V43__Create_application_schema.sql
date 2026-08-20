CREATE TABLE IF NOT EXISTS promotion (
                                         id UUID PRIMARY KEY,
                                         libelle VARCHAR(255) NOT NULL,
    annee_entree INTEGER NOT NULL
    );




CREATE TABLE IF NOT EXISTS users (
                                     id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(31),
    promotion_id UUID,

    CONSTRAINT fk_users_promotion
    FOREIGN KEY (promotion_id)
    REFERENCES promotion(id)
    );




CREATE TABLE IF NOT EXISTS cours (
                                     id UUID PRIMARY KEY,
                                     ref VARCHAR(255),
    credit INTEGER
    );




CREATE TABLE IF NOT EXISTS course_teacher (
                                              course_id UUID NOT NULL,
                                              teacher_id VARCHAR(255) NOT NULL,

    CONSTRAINT pk_course_teacher
    PRIMARY KEY (course_id, teacher_id),

    CONSTRAINT fk_course_teacher_course
    FOREIGN KEY (course_id)
    REFERENCES cours(id),

    CONSTRAINT fk_course_teacher_teacher
    FOREIGN KEY (teacher_id)
    REFERENCES users(id)
    );




CREATE TABLE IF NOT EXISTS examen (
                                      id UUID PRIMARY KEY,
                                      name VARCHAR(255),
    date_time TIMESTAMP WITH TIME ZONE,
                            coefficient NUMERIC(5,4) NOT NULL,
    cours_id UUID,

    CONSTRAINT fk_examen_cours
    FOREIGN KEY (cours_id)
    REFERENCES cours(id)
    );




CREATE TABLE IF NOT EXISTS teams (
                                     id UUID PRIMARY KEY,
                                     ref VARCHAR(255)
    );




CREATE TABLE IF NOT EXISTS team_membership (
                                               id UUID PRIMARY KEY,
                                               student_id VARCHAR(255) NOT NULL,
    team_id UUID NOT NULL,
    start_date DATE,
    end_date DATE,

    CONSTRAINT fk_membership_student
    FOREIGN KEY (student_id)
    REFERENCES users(id),

    CONSTRAINT fk_membership_team
    FOREIGN KEY (team_id)
    REFERENCES teams(id)
    );




CREATE TABLE IF NOT EXISTS course_team_assignment (
                                                      id UUID PRIMARY KEY,
                                                      cours_id UUID NOT NULL,
                                                      teacher_id VARCHAR(255) NOT NULL,
    team_id UUID,
    annee_etude INTEGER NOT NULL,

    CONSTRAINT fk_assignment_cours
    FOREIGN KEY (cours_id)
    REFERENCES cours(id),

    CONSTRAINT fk_assignment_teacher
    FOREIGN KEY (teacher_id)
    REFERENCES users(id),

    CONSTRAINT fk_assignment_team
    FOREIGN KEY (team_id)
    REFERENCES teams(id)
    );




CREATE TABLE IF NOT EXISTS note (
                                    id UUID PRIMARY KEY,
                                    student_id VARCHAR(255) NOT NULL,
    examen_id UUID NOT NULL,
    valeur NUMERIC(19,2) NOT NULL,
    annee_etude INTEGER NOT NULL,

    CONSTRAINT fk_note_student
    FOREIGN KEY (student_id)
    REFERENCES users(id),

    CONSTRAINT fk_note_examen
    FOREIGN KEY (examen_id)
    REFERENCES examen(id)
    );



CREATE TABLE IF NOT EXISTS note_historique (
                                               id UUID PRIMARY KEY,
                                               note_id UUID NOT NULL,
                                               ancienne_valeur NUMERIC(19,2) NOT NULL,
    nouvelle_valeur NUMERIC(19,2) NOT NULL,
    raison VARCHAR(255) NOT NULL,
    modifie_par VARCHAR(255) NOT NULL,
    date_modification TIMESTAMP WITH TIME ZONE NOT NULL,

                                    CONSTRAINT fk_note_historique_note
                                    FOREIGN KEY (note_id)
    REFERENCES note(id)
    );


