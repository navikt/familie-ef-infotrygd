# Infotrygd Enslig Forsørger

Dette er en REST-tjeneste som gir tilgang til historiske data fra Infotrygd
via replikert database.

http://localhost:8093/swagger-ui.html

Tabeller og kolonner som er i bruk: http://localhost:8080/tables 

Bygging:

Noen sliter med at de får 401 Unauthorized ved bygging, som antakeligvis skyldes at følgende ikke er satt i $HOME/.gradle/gradle.properties (sjekk også Gradle versjon):

gpr.user = GITHUB_USERNAME</br>
gpr.key = GITHUB_TOKEN

Confluence:
- https://confluence.adeo.no/display/MODNAV/Databaser

Dokumentasjon:
https://navno.sharepoint.com/:x:/s/TeamAllosaurus/EcoV3LDNJPRJjlKpCXJutk4Bb5u4PNJIvjb9IEEe38ZpCQ?e=DH53jq


