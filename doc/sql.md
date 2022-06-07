Hvis `s.dato_opphor` (t_stonad) finnes og den er før `v.dato_innv_tom` (t_vedtak) så er det `dato_opphor` som er gjeldende, av den grunnen burde man
bruke opphør hvis den finnes og er før tom-dato.

```sql
(CASE WHEN (nvl(dato_opphor, dato_innv_tom) < dato_innv_tom) THEN dato_opphor ELSE dato_innv_tom END) AS tom
```

For å finne alle vedtak til en person

```sql
SELECT l.personnr,
       v.kode_rutine,
       e.kode,
       v.brukerid,
       s.stonad_id,
       v.vedtak_id,
       v.tidspunkt_reg,
       ef.stonad_belop,
       ef.innt_fradrag,
       ef.sam_fradrag,
       ef.netto_belop,
       ef.aktivitet,
       ef.kode_overg,
       s.dato_start,
       v.type_sak,
       v.dato_innv_fom,
       v.dato_innv_tom,
       s.dato_opphor
FROM t_lopenr_fnr l
         JOIN t_stonad s ON s.person_lopenr = l.person_lopenr
         JOIN t_vedtak v ON v.stonad_id = s.stonad_id
         JOIN t_endring e ON e.vedtak_id = v.vedtak_id
         JOIN t_ef ef ON ef.vedtak_id = v.vedtak_id
WHERE l.personnr IN ('<fnr>')
  AND s.oppdrag_id IS NOT NULL          -- Har blir sendt i vei til oppdrag 
  AND v.kode_rutine IN ('EO')           -- Stønadstype
  AND e.kode <> 'AN'                    -- Annulerte
  AND e.kode <> 'UA'                    -- Uaktuelle
  AND v.dato_innv_fom < v.dato_innv_tom -- Hvis man ønsker kun aktuelle kan det være relevant å filtrere på opphørsdato og
ORDER BY s.stonad_id DESC, vedtak_id DESC, dato_innv_fom DESC
;
```

## Gyldige datoer og opphørsdato

Av en eller annen grunn finnes ugyldige datoer i infotrygd, så får å filtrere bort de kan man bruke

Typiskt tilfelle der opphørsdatoet er før fom-dato er når perioden aldri blitt utbetalt, eller en eller annen feil i infotrygd.

```sql
AND v.dato_innv_fom < v.dato_innv_tom
```

Man kan også bruke opphørsdatoet, som iblant har blitt satt

```sql
AND v.dato_innv_fom < (case when (nvl(dato_opphor, dato_innv_tom) < dato_innv_tom) then dato_opphor else dato_innv_tom end)
```

Men det går også å filtrere samme sak med:

```sql
 AND v.dato_innv_fom < v.dato_innv_tom
 AND (dato_opphor is null OR dato_opphor > dato_innv_fom)
```

#### Kommentar om datoer

Når vi henter ut vedtak kan det fortsatt være interessant å hente alle vedtak uavhengig dato-filtrering, fordi det har fortsatt
blitt innvilget et vedtak der en gang. Men for å vise faktiskt aktive perioder så må de filtreres vekk. 