Vedtak i Infotrygd legges på en t_stonad-entitet, slik jeg skjønt det så har man vanligvis en innvilget søknad for eks
`01.2021 - 06.2021 200kr`. Hvis man då skulle få en revurdering på denne blir det et nytt vedtak på samme periode, dvs samme
stønad-id. Eks der man får innvilget et nytt beløp fra april så avkortes første vedtaket med nytt tom-dato og ett nytt vedtak fra
april.

| fom     | tom     | beløp |
| ---     | ---     | ---   |
| 01.2021 | 03.2021 | 200kr |
| 04.2021 | 06.2021 | 300kr |

2 stønader på en og samme person overlapper ikke. Det kan betyde at man kan ha en t_stonad med høyere stonad_id på en person som
har dato innvilget før tidligere stonad_id pga att man har innvilget dette bak i tiden, før forrige periode. Eks:

|stonad_id | vedtak_id | fom | tom | beløp | 
| --- | --- | --- | --- | --- | 
| 2 | 3 | 01.2021 | 03.2021 | 200kr | 
| 1 | 4 | 04.2021 | 06.2021 | 300kr |
| 3 | 5 | 08.2021 | 08.2021 | 300kr |