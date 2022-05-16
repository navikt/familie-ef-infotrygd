# SQL-Spørringer

https://confluence.adeo.no/display/MODNAV/Databaser+-+EF

Tabellene er oppdelt i `T_`- og `SA_`-tabeller

Tabellene er i et annet schema, så får å ikke ta med schema i navnet på tabellen så kan man kjøre denne for å enklere kunne spørre
mot tabellene.

```sql
ALTER
SESSION
SET current_schema = infotrygd_ef<<miljø>>; --(miljø = q/p)
```

## T-tabeller

### Tabeller

De viktigste tabellene som har data for Enslig Forsørger

- `T_LOPENR_FNR`, kobling mellom fnr og løpenr, felles for alle stønader
- `T_STONAD`, fellestabell for alle typer stønader
- `T_VEDTAK`, fellestabell for alle typer stønader
- `T_EF`, tabell kun for Enslig forsørger
- `T_ENDRING`, fellestabell
- `T_DELYTELSE`, fellestabell for alle typer stønader
- `T_ROLLE`, tabekk kun for barnetilsyn, inneholder ident og fom/tom for barnen på vedtaket

#### T_LOPENR_FNR

Inneholder fnr for t_lopenr_r

#### T_ENDRING

Hver vedtak kan ha 1-2 (3) endringer Vanligvis har man først en `F` (førstegangsbehandling) som senere kan få `O` (opphør). Denne
tabellen inneholder ikke noe dato så man kan strengt tatt ikke vite eksakt hva som skjedde først. Her er det kun logiske regler
som gjelder. Eks så kan man aldri ha et opphør før førstegangsbehandling.

#### KODE_RUTINE

https://confluence.adeo.no/display/MODNAV/EF+i+Sak-rutinen

Stønadskodene er:

| KODE | Verdi           |
| ---- | ----            |
| EO   | Overgangsstønad |
| EB   | Barnetilsyn     |
| EU   | Skolepenger     |

## SA-tabeller

`SA`-tabeller er "sakstabeller", som inneholder informasjon om en sak. Disse kommer fra DL1-database.

I disse tabellene er formatet litt annerledes, eks datoer og FNR. I tillegg så hvis en kolonne er 2 char lang, og inneholder
verdiet `S` så er verdiet i kolonnen `S<space>`

### Tabeller

- SA_PERSON_01
- SA_SAKSBLOKK_05
- SA_SAK_10
- SA_STATUS_15
- SA_HENDELSE_20

#### FNR

FNR har formatet `yymmddXXXXX`, så får å finne en sak for en gitt person kan man eks gjøre

```sql
SELECT *
FROM sa_sak_10 s
WHERE personnr = REGEXP_REPLACE(f_nr, '(\d\d)(\d\d)(\d\d)(\d\d\d\d\d)', '\3\2\1\4')
```

#### Datoer

Datoer kan ha formatet `ddMMyyyy`, der `01.01.2000` har verdiet `1012000` i databasen
