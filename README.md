# pensjonskalkulator-backend

Backend-applikasjon for Navs pensjonskalkulator.

## API

Swagger/OpenAPI-dokumentasjon:

* [Produksjon](https://pensjonskalkulator-backend.intern.nav.no/swagger-ui/index.html)
* [Utvikling](https://pensjonskalkulator-backend.intern.dev.nav.no/swagger-ui/index.html)

## Teknologi

* [Java 25](https://openjdk.org/projects/jdk/25/)
* [Kotlin](https://kotlinlang.org/)
* [Spring Boot 4](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)
* [Nais](https://nais.io/)
* [Kotest](https://kotest.io/)

## Virksomhetssertifikat mot Norsk Pensjon

REST-integrasjonen bruker mTLS med en passordbeskyttet PKCS#12-keystore som lastes direkte fra Google Secret Manager ved oppstart. Keystoren må inneholde privatnøkkel, klientsertifikat og nødvendige mellomsertifikater. En separat credentials-secret inneholder JSON-feltene `password`, `alias` og `type`, hvor `type` skal være `pkcs12`. JVM-ens standard truststore brukes for Norsk Pensjons serversertifikat.

Secret-versjonene identifiseres som
`projects/${GCP_TEAM_PROJECT_ID}/secrets/${NORSK_PENSJON_CREDENTIALS_SECRET_ID}/versions/${NORSK_PENSJON_CREDENTIALS_SECRET_VERSION}`
og `projects/${GCP_TEAM_PROJECT_ID}/secrets/${NORSK_PENSJON_CERTIFICATE_SECRET_ID}/versions/${NORSK_PENSJON_CERTIFICATE_SECRET_VERSION}`.
Versjonene kan være `latest` eller eksplisitte versjonsnumre. Applikasjonens Google service account må ha
`roles/secretmanager.secretAccessor` kun på de to aktuelle secretene i hvert miljø.

Ved rotasjon opprettes en ny secret-versjon før poddene restartes/redeployes. Verifiser trafikken etter at alle podder har startet med den nye versjonen, og deaktiver deretter den gamle versjonen.

## Henvendelser

Nav-interne henvendelser kan sendes via Slack i kanalen [#team-planlegge-pensjon](https://nav-it.slack.com/archives/C09A5SC5KQF).
