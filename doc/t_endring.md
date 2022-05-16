Kommentar om T_ENDRING
fra [slack](https://nav-it.slack.com/archives/CQZFBH4GZ/p1642765828043300?thread_ts=1642757959.039800&cid=CQZFBH4GZ)

Hvis man utgår fra att O (opphør) kun er gyldig som alene på ett vedtak, eller som nr 2 (hvis det kan være riktig). Samme sak
gjelder OO Så får man disse, noen kombinasjoner av E, G, UA som kan være vanskelige å tolke her hva som kan komme først/etterpå

| Kode  | Kode2 | Antall    |
| ----- | ----- | --------- |
| AN    |       | 674       |
| E     | O     | 17561     |
| E     | UA    | 6         |
| E     |       | 382033    |
| F     | O     | 25303     |
| F     | OO    | 2         |
| F     |       | 353784    |
| G     | O     | 23617     |
| G     | UA    | 1         |
| G     |       | 218811    |
| NY    | O     | 3673      |
| NY    |       | 35404     |
| O     |       | 1         |
| S     | O     | 47        |
| S     |       | 1069      |
| UA    | E     | 6         |
| UA    | G     | 1         |
| UA    | O     | 3         |
| UA    |       | 83955     |

```sql
WITH q AS (
    SELECT vedtak_id, kode, ROW_NUMBER() OVER (PARTITION BY vedtak_id ORDER BY kode) rn FROM t_endring)
SELECT q1.kode, q2.kode kode2, COUNT(*)
FROM q q1
         LEFT JOIN q q2 ON q1.vedtak_id = q2.vedtak_id AND q1.rn <> q2.rn
WHERE (q2.kode IS NULL OR (q1.kode <> 'O ' AND q1.kode <> '
OO'))
GROUP BY q1.kode, q2.kode
ORDER BY q1.kode, q2.kode;
```