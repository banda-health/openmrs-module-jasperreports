select distinct o.patient_id, o.encounter_id, o.obs_datetime from obs o, patient p, (
  -- most recent observation of <concept_id> between two dates for all patients
  select patient_id, max(obs_datetime) as obs_datetime from obs
  where concept_id = 1419 -- DTHF ARV DRUG LIST
  and obs_datetime <=  date(<end-date>) -- end date
  and voided = 0
  group by patient_id
) as recent
where o.patient_id = recent.patient_id
and o.obs_datetime = recent.obs_datetime
and o.concept_id = 1419
-- C
and o.patient_id in (
  -- C
  -- age calculation
  select patient_id from
  (
    select distinct patient_id, (YEAR( date(<age-calc-date>))-YEAR(birthdate))
       - (RIGHT(date(<age-calc-date>),5)<RIGHT(birthdate,5))
    as age from patient
  )as ages
  where age >= <min-age> -- min age
  and age < <max-age>      -- max age
)
and o.patient_id = p.patient_id
and
-- B+N+
--  B
(p.dead = 0 or p.death_date > date(<end-date>)
)
and p.patient_id not in
(
  select distinct patient_id from obs o
  where

  (
   o.concept_id = 1372  --  Transferred out
   or
   o.concept_id = 1375  --  Lost to follow up
  )
  and o.obs_datetime <= date(<end-date>)
)
-- N+
and o.patient_id not in
(
-- find most recent ARV plan obs
  select distinct patient_id from obs o,
  (
   select  patient_id as pat, max(obs_datetime) as datet from obs
   where
   concept_id = 1255 -- ANTIRETROVIRAL PLAN
   and obs_datetime <= date(<end-date>)-- end date
   and voided = 0
   group by patient_id
   order by patient_id
   ) as lastARV
   where
   o.patient_id = lastARV.pat
   and obs_datetime = lastARV.datet
   and concept_id = 1255
   and value_coded = 1260 -- stop all
   and voided = 0
)
group by patient_id;