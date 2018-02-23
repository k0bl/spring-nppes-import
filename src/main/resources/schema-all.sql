DROP TABLE IF EXISTS taxonomies;

CREATE TABLE taxonomies  (
    code VARCHAR(20),
    grouping VARCHAR(255),
    classification VARCHAR(255),
    specialization VARCHAR(255)

)ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;
