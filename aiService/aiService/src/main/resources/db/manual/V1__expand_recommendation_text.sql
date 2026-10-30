-- Expand recommendation text columns without dropping or rewriting existing rows.
ALTER TABLE recommendations
    MODIFY COLUMN recommendation LONGTEXT NULL;

ALTER TABLE recommendation_improvements
    MODIFY COLUMN improvements LONGTEXT NULL;

ALTER TABLE recommendation_suggestions
    MODIFY COLUMN suggestions LONGTEXT NULL;

ALTER TABLE recommendation_safety
    MODIFY COLUMN safety LONGTEXT NULL;
