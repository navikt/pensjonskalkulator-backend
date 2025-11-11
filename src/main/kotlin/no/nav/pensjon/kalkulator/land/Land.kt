package no.nav.pensjon.kalkulator.land

/**
 * Enumerasjon av land basert på ISO 3166-1 alpha-3.
 * Kilder:
 * - pen.T_K_LAND_3_TEGN, pen.T_K_AVTALELAND
 * - pensjon-pen: LandMedRettTilOpptjeningAvTrygdetidCode (kravOmArbeid)
 * - https://navno.sharepoint.com/sites/fag-og-ytelser-eos-lovvalg-medlemskap/SitePages/Oversikt-over-land-og-omr%C3%A5der-som-er-omfattet-av-E%C3%98S-reglene-om-trygdekoordinering.aspx
 * - https://no.wikipedia.org/wiki/ISO_3166-1
 * - https://en.wikipedia.org/wiki/ISO_3166-1
 * - https://no.wikipedia.org/wiki/De_nederlandske_Antiller
 * - https://www.ssb.no/klass/klassifikasjoner/552/versjon/1842
 * Sortert alfabetisk etter bokmålsnavn (unntatt tilleggsland, som er lagt til nederst i listen)
 */
enum class Land(
    val erAvtaleland: Boolean = false,
    val kravOmArbeid: Boolean? = null, // only relevant if erAvtaleland = true
    val erHistorisk: Boolean = false,
    val bokmaalNavn: String,
    val engelskNavn: String
) {
    AFG(
        bokmaalNavn = "Afghanistan",
        engelskNavn = "Afghanistan"
    ),
    ALB(
        bokmaalNavn = "Albania",
        engelskNavn = "Albania"
    ),
    DZA(
        bokmaalNavn = "Algerie",
        engelskNavn = "Algeria"
    ),
    ASM(
        bokmaalNavn = "Amerikansk Samoa",
        engelskNavn = "American Samoa"
    ),
    AND(
        bokmaalNavn = "Andorra",
        engelskNavn = "Andorra"
    ),
    AGO(
        bokmaalNavn = "Angola",
        engelskNavn = "Angola"
    ),
    AIA(
        bokmaalNavn = "Anguilla",
        engelskNavn = "Anguilla"
    ),
    ATA(
        bokmaalNavn = "Antarktis",
        engelskNavn = "Antarctica"
    ),
    ATG(
        bokmaalNavn = "Antigua og Barbuda",
        engelskNavn = "Antigua and Barbuda"
    ),
    ARG(
        bokmaalNavn = "Argentina",
        engelskNavn = "Argentina"
    ),
    ARM(
        bokmaalNavn = "Armenia",
        engelskNavn = "Armenia"
    ),
    ABW(
        bokmaalNavn = "Aruba",
        engelskNavn = "Aruba"
    ),
    AZE(
        bokmaalNavn = "Aserbajdsjan",
        engelskNavn = "Azerbaijan"
    ),
    AUS(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Australia",
        engelskNavn = "Australia"
    ),
    BHS(
        bokmaalNavn = "Bahamas",
        engelskNavn = "Bahamas"
    ),
    BHR(
        bokmaalNavn = "Bahrain",
        engelskNavn = "Bahrain"
    ),
    BGD(
        bokmaalNavn = "Bangladesh",
        engelskNavn = "Bangladesh"
    ),
    BRB(
        bokmaalNavn = "Barbados",
        engelskNavn = "Barbados"
    ),
    BLR(
        bokmaalNavn = "Belarus",
        engelskNavn = "Belarus"
    ),
    BEL(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Belgia",
        engelskNavn = "Belgium"
    ),
    BLZ(
        bokmaalNavn = "Belize",
        engelskNavn = "Belize"
    ),
    BEN(
        bokmaalNavn = "Benin",
        engelskNavn = "Benin"
    ),
    BMU(
        bokmaalNavn = "Bermuda",
        engelskNavn = "Bermuda"
    ),
    BTN(
        bokmaalNavn = "Bhutan",
        engelskNavn = "Bhutan"
    ),
    BOL(
        bokmaalNavn = "Bolivia",
        engelskNavn = "Bolivia"
    ),
    BES(
        bokmaalNavn = "Bonaire, Sint Eustatius og Saba",
        engelskNavn = "Bonaire, Sint Eustatius and Saba"
    ),
    BIH(
        bokmaalNavn = "Bosnia-Hercegovina",
        engelskNavn = "Bosnia and Herzegovina"
    ),
    BWA(
        bokmaalNavn = "Botswana",
        engelskNavn = "Botswana"
    ),
    BVT(
        bokmaalNavn = "Bouvetøya",
        engelskNavn = "Bouvet Island"
    ),
    BRA(
        bokmaalNavn = "Brasil",
        engelskNavn = "Brazil"
    ),
    BRN(
        bokmaalNavn = "Brunei",
        engelskNavn = "Brunei"
    ),
    BGR(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Bulgaria",
        engelskNavn = "Bulgaria"
    ),
    BFA(
        bokmaalNavn = "Burkina Faso",
        engelskNavn = "Burkina Faso"
    ),
    BDI(
        bokmaalNavn = "Burundi",
        engelskNavn = "Burundi"
    ),
    CAN(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Canada (utenom Quebec)",
        engelskNavn = "Canada (except Quebec)"
    ),
    QEB(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Canada (Quebec)",
        engelskNavn = "Canada (Quebec)"
    ), // provinsen Quebec har egen trygdeavtale med Norge
    CYM(
        bokmaalNavn = "Caymanøyene",
        engelskNavn = "Cayman Islands"
    ),
    CHL(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Chile",
        engelskNavn = "Chile"
    ),
    CXR(
        bokmaalNavn = "Christmasøya",
        engelskNavn = "Christmas Island"
    ),
    COL(
        bokmaalNavn = "Colombia",
        engelskNavn = "Colombia"
    ),
    COK(
        bokmaalNavn = "Cookøyene",
        engelskNavn = "Cook Islands"
    ),
    CRI(
        bokmaalNavn = "Costa Rica",
        engelskNavn = "Costa Rica"
    ),
    CUB(
        bokmaalNavn = "Cuba",
        engelskNavn = "Cuba"
    ),
    CUW(
        bokmaalNavn = "Curaçao",
        engelskNavn = "Curaçao"
    ),
    DNK(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Danmark",
        engelskNavn = "Denmark"
    ),
    ARE(
        bokmaalNavn = "De forente arabiske emirater",
        engelskNavn = "United Arab Emirates"
    ),
    ATF(
        bokmaalNavn = "De franske sørterritorier",
        engelskNavn = "French Southern Territories"
    ),
    ANT(
        bokmaalNavn = "De nederlandske Antiller",
        engelskNavn = "Netherlands Antilles"
    ), // oppløst 10. oktober 2010
    DOM(
        bokmaalNavn = "Den dominikanske republikk",
        engelskNavn = "Dominican Republic"
    ),
    CAF(
        bokmaalNavn = "Den sentralafrikanske republikk",
        engelskNavn = "Central African Republic"
    ),
    IOT(
        bokmaalNavn = "Det britiske territoriet i Indiahavet",
        engelskNavn = "British Indian Ocean Territory"
    ),
    DJI(
        bokmaalNavn = "Djibouti",
        engelskNavn = "Djibouti"
    ),
    DMA(
        bokmaalNavn = "Dominica",
        engelskNavn = "Dominica"
    ),
    ECU(
        bokmaalNavn = "Ecuador",
        engelskNavn = "Ecuador"
    ),
    EGY(
        bokmaalNavn = "Egypt",
        engelskNavn = "Egypt"
    ),
    GNQ(
        bokmaalNavn = "Ekvatorial-Guinea",
        engelskNavn = "Equatorial Guinea"
    ),
    SLV(
        bokmaalNavn = "El Salvador",
        engelskNavn = "El Salvador"
    ),
    CIV(
        bokmaalNavn = "Elfenbenskysten",
        engelskNavn = "Ivory Coast"
    ),
    ERI(
        bokmaalNavn = "Eritrea",
        engelskNavn = "Eritrea"
    ),
    EST(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Estland",
        engelskNavn = "Estonia"
    ),
    SWZ(
        bokmaalNavn = "Eswatini",
        engelskNavn = "Eswatini"
    ),
    ETH(
        bokmaalNavn = "Etiopia",
        engelskNavn = "Ethiopia"
    ),
    FLK(
        bokmaalNavn = "Falklandsøyene",
        engelskNavn = "Falkland Islands (Malvinas)"
    ),
    FJI(
        bokmaalNavn = "Fiji",
        engelskNavn = "Fiji"
    ),
    PHL(
        bokmaalNavn = "Filippinene",
        engelskNavn = "Philippines"
    ),
    FIN(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Finland",
        engelskNavn = "Finland"
    ),
    FRA(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Frankrike",
        engelskNavn = "France"
    ),
    GUF(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Fransk Guyana",
        engelskNavn = "French Guiana"
    ),
    PYF(
        bokmaalNavn = "Fransk Polynesia",
        engelskNavn = "French Polynesia"
    ),
    FRO(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Færøyene",
        engelskNavn = "Faroe Islands"
    ),
    GAB(
        bokmaalNavn = "Gabon",
        engelskNavn = "Gabon"
    ),
    GMB(
        bokmaalNavn = "Gambia",
        engelskNavn = "Gambia"
    ),
    GEO(
        bokmaalNavn = "Georgia",
        engelskNavn = "Georgia"
    ),
    GHA(
        bokmaalNavn = "Ghana",
        engelskNavn = "Ghana"
    ),
    GIB(
        bokmaalNavn = "Gibraltar",
        engelskNavn = "Gibraltar"
    ),
    GRD(
        bokmaalNavn = "Grenada",
        engelskNavn = "Grenada"
    ),
    GRL(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Grønland",
        engelskNavn = "Greenland"
    ),
    GLP(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Guadeloupe",
        engelskNavn = "Guadeloupe"
    ),
    GUM(
        bokmaalNavn = "Guam",
        engelskNavn = "Guam"
    ),
    GTM(
        bokmaalNavn = "Guatemala",
        engelskNavn = "Guatemala"
    ),
    GGY(
        bokmaalNavn = "Guernsey",
        engelskNavn = "Guernsey"
    ),
    GIN(
        bokmaalNavn = "Guinea",
        engelskNavn = "Guinea"
    ),
    GNB(
        bokmaalNavn = "Guinea-Bissau",
        engelskNavn = "Guinea-Bissau"
    ),
    GUY(
        bokmaalNavn = "Guyana",
        engelskNavn = "Guyana"
    ),
    HTI(
        bokmaalNavn = "Haiti",
        engelskNavn = "Haiti"
    ),
    HMD(
        bokmaalNavn = "Heard- og McDonaldøyene",
        engelskNavn = "Heard Island and McDonald Islands"
    ),
    GRC(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Hellas",
        engelskNavn = "Greece"
    ),
    HND(
        bokmaalNavn = "Honduras",
        engelskNavn = "Honduras"
    ),
    HKG(
        bokmaalNavn = "Hongkong",
        engelskNavn = "Hong Kong"
    ),
    IND(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "India",
        engelskNavn = "India"
    ),
    IDN(
        bokmaalNavn = "Indonesia",
        engelskNavn = "Indonesia"
    ),
    IRQ(
        bokmaalNavn = "Irak",
        engelskNavn = "Iraq"
    ),
    IRN(
        bokmaalNavn = "Iran",
        engelskNavn = "Iran"
    ),
    IRL(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Irland",
        engelskNavn = "Ireland"
    ),
    ISL(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Island",
        engelskNavn = "Iceland"
    ),
    ISR(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Israel",
        engelskNavn = "Israel"
    ),
    ITA(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Italia",
        engelskNavn = "Italy"
    ),
    JAM(
        bokmaalNavn = "Jamaica",
        engelskNavn = "Jamaica"
    ),
    JPN(
        bokmaalNavn = "Japan",
        engelskNavn = "Japan"
    ),
    YEM(
        bokmaalNavn = "Jemen",
        engelskNavn = "Yemen"
    ),
    JEY(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Jersey",
        engelskNavn = "Jersey"
    ),
    VIR(
        bokmaalNavn = "Jomfruøyene (De amerikanske)",
        engelskNavn = "Virgin Islands (U.S.)"
    ),
    VGB(
        bokmaalNavn = "Jomfruøyene (De britiske)",
        engelskNavn = "Virgin Islands (British)"
    ),
    JOR(
        bokmaalNavn = "Jordan",
        engelskNavn = "Jordan"
    ),
    KHM(
        bokmaalNavn = "Kambodsja",
        engelskNavn = "Cambodia"
    ),
    CMR(
        bokmaalNavn = "Kamerun",
        engelskNavn = "Cameroon"
    ),
    CPV(
        bokmaalNavn = "Kapp Verde",
        engelskNavn = "Cabo Verde"
    ),
    KAZ(
        bokmaalNavn = "Kasakhstan",
        engelskNavn = "Kazakhstan"
    ),
    KEN(
        bokmaalNavn = "Kenya",
        engelskNavn = "Kenya"
    ),
    CHN(
        bokmaalNavn = "Kina",
        engelskNavn = "China"
    ),
    KGZ(
        bokmaalNavn = "Kirgisistan",
        engelskNavn = "Kyrgyzstan"
    ),
    KIR(
        bokmaalNavn = "Kiribati",
        engelskNavn = "Kiribati"
    ),
    CCK(
        bokmaalNavn = "Kokosøyene (Keelingøyene)",
        engelskNavn = " Cocos (Keeling) Islands"
    ),
    COM(
        bokmaalNavn = "Komorene",
        engelskNavn = "Comoros"
    ),
    COG(
        bokmaalNavn = "Kongo-Brazzaville",
        engelskNavn = "Congo-Brazzaville"
    ),
    COD(
        bokmaalNavn = "Kongo-Kinshasa",
        engelskNavn = "Congo-Kinshasa"
    ),
    XXK(
        bokmaalNavn = "Kosovo",
        engelskNavn = "Kosovo"
    ),
    HRV(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Kroatia",
        engelskNavn = "Croatia"
    ),
    KWT(
        bokmaalNavn = "Kuwait",
        engelskNavn = "Kuwait"
    ),
    CYP(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Kypros",
        engelskNavn = "Cyprus"
    ),
    LAO(
        bokmaalNavn = "Laos",
        engelskNavn = "Laos"
    ),
    LVA(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Latvia",
        engelskNavn = "Latvia"
    ),
    LSO(
        bokmaalNavn = "Lesotho",
        engelskNavn = "Lesotho"
    ),
    LBN(
        bokmaalNavn = "Libanon",
        engelskNavn = "Lebanon"
    ),
    LBR(
        bokmaalNavn = "Liberia",
        engelskNavn = "Liberia"
    ),
    LBY(
        bokmaalNavn = "Libya",
        engelskNavn = "Libya"
    ),
    LIE(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Liechtenstein",
        engelskNavn = "Liechtenstein"
    ),
    LTU(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Litauen",
        engelskNavn = "Lithuania"
    ),
    LUX(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Luxembourg",
        engelskNavn = "Luxembourg"
    ),
    MAC(
        bokmaalNavn = "Macao",
        engelskNavn = "Macao"
    ),
    MDG(
        bokmaalNavn = "Madagaskar",
        engelskNavn = "Madagascar"
    ),
    MWI(
        bokmaalNavn = "Malawi",
        engelskNavn = "Malawi"
    ),
    MYS(
        bokmaalNavn = "Malaysia",
        engelskNavn = "Malaysia"
    ),
    MDV(
        bokmaalNavn = "Maldivene",
        engelskNavn = "Maldives"
    ),
    MLI(
        bokmaalNavn = "Mali",
        engelskNavn = "Mali"
    ),
    MLT(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Malta",
        engelskNavn = "Malta"
    ),
    IMN(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Man",
        engelskNavn = "Isle of Man"
    ),
    MAR(
        bokmaalNavn = "Marokko",
        engelskNavn = "Morocco"
    ),
    MHL(
        bokmaalNavn = "Marshalløyene",
        engelskNavn = "Marshall Islands"
    ),
    MTQ(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Martinique",
        engelskNavn = "Martinique"
    ),
    MRT(
        bokmaalNavn = "Mauritania",
        engelskNavn = "Mauritania"
    ),
    MUS(
        bokmaalNavn = "Mauritius",
        engelskNavn = "Mauritius"
    ),
    MYT(
        bokmaalNavn = "Mayotte",
        engelskNavn = "Mayotte"
    ),
    MEX(
        bokmaalNavn = "Mexico",
        engelskNavn = "Mexico"
    ),
    FSM(
        bokmaalNavn = "Mikronesiaføderasjonen",
        engelskNavn = "Micronesia"
    ),
    MDA(
        bokmaalNavn = "Moldova",
        engelskNavn = "Moldova"
    ),
    MCO(
        bokmaalNavn = "Monaco",
        engelskNavn = "Monaco"
    ),
    MNG(
        bokmaalNavn = "Mongolia",
        engelskNavn = "Mongolia"
    ),
    MNE(
        bokmaalNavn = "Montenegro",
        engelskNavn = "Montenegro"
    ),
    MSR(
        bokmaalNavn = "Montserrat",
        engelskNavn = "Montserrat"
    ),
    MOZ(
        bokmaalNavn = "Mosambik",
        engelskNavn = "Mozambique"
    ),
    MMR(
        bokmaalNavn = "Myanmar (Burma)",
        engelskNavn = "Myanmar (Burma)"
    ),
    NAM(
        bokmaalNavn = "Namibia",
        engelskNavn = "Namibia"
    ),
    NRU(
        bokmaalNavn = "Nauru",
        engelskNavn = "Nauru"
    ),
    NLD(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Nederland",
        engelskNavn = "Netherlands"
    ),
    NPL(
        bokmaalNavn = "Nepal",
        engelskNavn = "Nepal"
    ),
    NZL(
        bokmaalNavn = "New Zealand",
        engelskNavn = "New Zealand"
    ),
    NIC(
        bokmaalNavn = "Nicaragua",
        engelskNavn = "Nicaragua"
    ),
    NER(
        bokmaalNavn = "Niger",
        engelskNavn = "Niger"
    ),
    NGA(
        bokmaalNavn = "Nigeria",
        engelskNavn = "Nigeria"
    ),
    NIU(
        bokmaalNavn = "Niue",
        engelskNavn = "Niue"
    ),
    PRK(
        bokmaalNavn = "Nord-Korea",
        engelskNavn = "North Korea"
    ),
    MKD(
        bokmaalNavn = "Nord-Makedonia",
        engelskNavn = "North Macedonia"
    ),
    MNP(
        bokmaalNavn = "Nord-Marianene",
        engelskNavn = "Northern Mariana Islands"
    ),
    NFK(
        bokmaalNavn = "Norfolkøya",
        engelskNavn = "Norfolk Island"
    ),
    NOR(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Norge",
        engelskNavn = "Norway"
    ),
    NCL(
        bokmaalNavn = "Ny-Caledonia",
        engelskNavn = "New Caledonia"
    ),
    OMN(
        bokmaalNavn = "Oman",
        engelskNavn = "Oman"
    ),
    PAK(
        bokmaalNavn = "Pakistan",
        engelskNavn = "Pakistan"
    ),
    PLW(
        bokmaalNavn = "Palau",
        engelskNavn = "Palau"
    ),
    PSE(
        bokmaalNavn = "Palestina",
        engelskNavn = "Palestina"
    ),
    PAN(
        bokmaalNavn = "Panama",
        engelskNavn = "Panama"
    ),
    PNG(
        bokmaalNavn = "Papua Ny-Guinea",
        engelskNavn = "Papua New Guinea"
    ),
    PRY(
        bokmaalNavn = "Paraguay",
        engelskNavn = "Paraguay"
    ),
    PER(
        bokmaalNavn = "Peru",
        engelskNavn = "Peru"
    ),
    PCN(
        bokmaalNavn = "Pitcairn",
        engelskNavn = "Pitcairn"
    ),
    POL(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Polen",
        engelskNavn = "Poland"
    ),
    PRT(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Portugal",
        engelskNavn = "Portugal"
    ),
    PRI(
        bokmaalNavn = "Puerto Rico",
        engelskNavn = "Puerto Rico"
    ),
    QAT(
        bokmaalNavn = "Qatar",
        engelskNavn = "Qatar"
    ),
    REU(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Réunion",
        engelskNavn = "Réunion"
    ),
    ROU(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Romania",
        engelskNavn = "Romania"
    ),
    RUS(
        bokmaalNavn = "Russland",
        engelskNavn = "Russia"
    ),
    RWA(
        bokmaalNavn = "Rwanda",
        engelskNavn = "Rwanda"
    ),
    BLM(
        bokmaalNavn = "Saint Barthélemy",
        engelskNavn = "Saint Barthélemy"
    ),
    SHN(
        bokmaalNavn = "St. Helena, Ascension og Tristan da Cunha",
        engelskNavn = "Saint Helena, Ascension and Tristan da Cunha"
    ),
    KNA(
        bokmaalNavn = "Saint Kitts og Nevis",
        engelskNavn = "Saint Kitts and Nevis"
    ),
    LCA(
        bokmaalNavn = "Saint Lucia",
        engelskNavn = "Saint Lucia"
    ),
    MAF(
        erAvtaleland = true,
        bokmaalNavn = "Saint-Martin",
        engelskNavn = "Saint Martin"
    ),
    SPM(
        bokmaalNavn = "Saint-Pierre og Miquelon",
        engelskNavn = "Saint Pierre and Miquelon"
    ),
    VCT(
        bokmaalNavn = "Saint Vincent og Grenadinene",
        engelskNavn = "Saint Vincent and the Grenadines"
    ),
    SLB(
        bokmaalNavn = "Salomonøyene",
        engelskNavn = "Solomon Islands"
    ),
    WSM(
        bokmaalNavn = "Samoa",
        engelskNavn = "Samoa"
    ),
    SMR(
        bokmaalNavn = "San Marino",
        engelskNavn = "San Marino"
    ),
    STP(
        bokmaalNavn = "São Tomé og Príncipe",
        engelskNavn = "Sao Tome and Principe"
    ),
    SAU(
        bokmaalNavn = "Saudi-Arabia",
        engelskNavn = "Saudi Arabia"
    ),
    SEN(
        bokmaalNavn = "Senegal",
        engelskNavn = "Senegal"
    ),
    SRB(
        bokmaalNavn = "Serbia",
        engelskNavn = "Serbia"
    ),
    SYC(
        bokmaalNavn = "Seychellene",
        engelskNavn = "Seychelles"
    ),
    SLE(
        bokmaalNavn = "Sierra Leone",
        engelskNavn = "Sierra Leone"
    ),
    SGP(
        bokmaalNavn = "Singapore",
        engelskNavn = "Singapore"
    ),
    SXM(
        bokmaalNavn = "Sint Maarten",
        engelskNavn = "Sint Maarten"
    ),
    SVK(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Slovakia",
        engelskNavn = "Slovakia"
    ),
    SVN(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Slovenia",
        engelskNavn = "Slovenia"
    ),
    SOM(
        bokmaalNavn = "Somalia",
        engelskNavn = "Somalia"
    ),
    ESP(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Spania",
        engelskNavn = "Spain"
    ),
    LKA(
        bokmaalNavn = "Sri Lanka",
        engelskNavn = "Sri Lanka"
    ),
    XXX( // ref. www.ssb.no/klass/klassifikasjoner/552/versjon/1842
        bokmaalNavn = "statsløs",
        engelskNavn = "stateless"
    ),
    GBR(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Storbritannia",
        engelskNavn = "Britain (United Kingdom)"
    ),
    SDN(
        bokmaalNavn = "Sudan",
        engelskNavn = "Sudan"
    ),
    SUR(
        bokmaalNavn = "Surinam",
        engelskNavn = "Suriname"
    ),
    SJM(
        bokmaalNavn = "Svalbard og Jan Mayen",
        engelskNavn = "Svalbard and Jan Mayen"
    ),
    CHE(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Sveits",
        engelskNavn = "Switzerland"
    ),
    SWE(
        erAvtaleland = true,
        kravOmArbeid = false,
        bokmaalNavn = "Sverige",
        engelskNavn = "Sweden"
    ),
    SYR(
        bokmaalNavn = "Syria",
        engelskNavn = "Syria"
    ),
    ZAF(
        bokmaalNavn = "Sør-Afrika",
        engelskNavn = "South Africa"
    ),
    SGS(
        bokmaalNavn = "Sør-Georgia og Sør-Sandwichøyene",
        engelskNavn = "South Georgia and the South Sandwich Islands"
    ),
    KOR(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Sør-Korea",
        engelskNavn = "South Korea"
    ),
    SSD(
        bokmaalNavn = "Sør-Sudan",
        engelskNavn = "South Sudan"
    ),
    TJK(
        bokmaalNavn = "Tadsjikistan",
        engelskNavn = "Tajikistan"
    ),
    TWN(
        bokmaalNavn = "Taiwan",
        engelskNavn = "Taiwan"
    ),
    TZA(
        bokmaalNavn = "Tanzania",
        engelskNavn = "Tanzania"
    ),
    TCD(
        bokmaalNavn = "Tchad",
        engelskNavn = "Chad"
    ),
    THA(
        bokmaalNavn = "Thailand",
        engelskNavn = "Thailand"
    ),
    TGO(
        bokmaalNavn = "Togo",
        engelskNavn = "Togo"
    ),
    TKL(
        bokmaalNavn = "Tokelau",
        engelskNavn = "Tokelau"
    ),
    TON(
        bokmaalNavn = "Tonga",
        engelskNavn = "Tonga"
    ),
    TTO(
        bokmaalNavn = "Trinidad og Tobago",
        engelskNavn = "Trinidad and Tobago"
    ),
    CZE(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Tsjekkia",
        engelskNavn = "Czechia"
    ),
    TUN(
        bokmaalNavn = "Tunisia",
        engelskNavn = "Tunisia"
    ),
    TKM(
        bokmaalNavn = "Turkmenistan",
        engelskNavn = "Turkmenistan"
    ),
    TCA(
        bokmaalNavn = "Turks- og Caicosøyene",
        engelskNavn = "Turks and Caicos Islands"
    ),
    TUV(
        bokmaalNavn = "Tuvalu",
        engelskNavn = "Tuvalu"
    ),
    TUR(
        bokmaalNavn = "Tyrkia",
        engelskNavn = "Turkey"
    ),
    DEU(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Tyskland",
        engelskNavn = "Germany"
    ),
    UGA(
        bokmaalNavn = "Uganda",
        engelskNavn = "Uganda"
    ),
    UKR(
        bokmaalNavn = "Ukraina",
        engelskNavn = "Ukraine"
    ),
    HUN(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Ungarn",
        engelskNavn = "Hungary"
    ),
    URY(
        bokmaalNavn = "Uruguay",
        engelskNavn = "Uruguay"
    ),
    USA(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "USA",
        engelskNavn = "USA"
    ),
    UMI(
        bokmaalNavn = "USAs ytre småøyer",
        engelskNavn = "United States Minor Outlying Islands"
    ),
    UZB(
        bokmaalNavn = "Usbekistan",
        engelskNavn = "Uzbekistan"
    ),
    VUT(
        bokmaalNavn = "Vanuatu",
        engelskNavn = "Vanuatu"
    ),
    VAT(
        bokmaalNavn = "Vatikanstaten",
        engelskNavn = "Vatican City"
    ),
    VEN(
        bokmaalNavn = "Venezuela",
        engelskNavn = "Venezuela"
    ),
    ESH(
        bokmaalNavn = "Vest-Sahara",
        engelskNavn = "Western Sahara"
    ),
    VNM(
        bokmaalNavn = "Vietnam",
        engelskNavn = "Vietnam"
    ),
    WLF(
        bokmaalNavn = "Wallis og Futuna",
        engelskNavn = "Wallis and Futuna"
    ),
    ZMB(
        bokmaalNavn = "Zambia",
        engelskNavn = "Zambia"
    ),
    ZWE(
        bokmaalNavn = "Zimbabwe",
        engelskNavn = "Zimbabwe"
    ),
    TLS(
        bokmaalNavn = "Øst-Timor",
        engelskNavn = "East Timor"
    ),
    AUT(
        erAvtaleland = true,
        kravOmArbeid = true,
        bokmaalNavn = "Østerrike",
        engelskNavn = "Austria"
    ),
    ALA(
        erAvtaleland = true, // NB: ikke avtaleland i PEN (pr. september 2024)
        kravOmArbeid = false,
        bokmaalNavn = "Åland",
        engelskNavn = "Åland"
    ),
    //--- Tilleggsland i pensjon-regler: ---
    CSK(
        erHistorisk = true, // splittet i to land 1. januar 1993
        bokmaalNavn = "Tsjekkoslovakia",
        engelskNavn = "Czechoslovakia"
    ),
    DDR(
        erHistorisk = true, // inngikk i Tyskland fra 1990
        bokmaalNavn = "Øst-Tyskland (DDR)",
        engelskNavn = "East Germany (GDR)"
    ),
    SCG(
        erHistorisk = true, // splittet i to land i 2006
        bokmaalNavn = "Serbia og Montenegro",
        engelskNavn = "Serbia and Montenegro"
    ),
    SUN(
        erHistorisk = true, // splittet i flere land i 1991
        bokmaalNavn = "Sovjetunionen",
        engelskNavn = "Soviet Union"
    ),
    WAK(
        erHistorisk = true,
        bokmaalNavn = "Wakeøya",
        engelskNavn = "Wake Island"
    ),
    YUG(
        erHistorisk = true, // splittet i flere land i 1992
        bokmaalNavn = "Jugoslavia",
        engelskNavn = "Yugoslavia"
    ),
    XUK( // ref. www.ssb.no/klass/klassifikasjoner/552/versjon/1842
        bokmaalNavn = "uoppgitt",
        engelskNavn = "unspecified"
    ),
    P_PANAMAKANALSONEN(
        bokmaalNavn = "Panamakanalsonen",
        engelskNavn = "Panama Canal Zone"
    ),
    P_669(
        erHistorisk = true,
        bokmaalNavn = "Panamakanalsonen 2",
        engelskNavn = "Panama Canal Zone 2"
    ),
    P_SIKKIM( // Sikkim hadde ISO-kode SKM, ref. en.wikipedia.org/wiki/ISO_3166-1_alpha-3
        erHistorisk = true, // del av India siden 1975
        bokmaalNavn = "Sikkim",
        engelskNavn = "Sikkim"
    ),
    P_546(
        erHistorisk = true,
        bokmaalNavn = "Sikkim 2",
        engelskNavn = "Sikkim 2"
    ),
    P_SPANSKE_OMR_AFRIKA(
        bokmaalNavn = "Spansk Nord-Afrika",
        engelskNavn = "Spanish North Africa"
    ),
    P_349(
        erHistorisk = true,
        bokmaalNavn = "Spansk Nord-Afrika 2",
        engelskNavn = "Spanish North Africa 2"
    ),
    P_YEMEN( // Jemen har ISO-kode YEM
        erHistorisk = true,
        bokmaalNavn = "Jemen 2",
        engelskNavn = "Yemen 2"
    ),
    P_556(
        erHistorisk = true,
        bokmaalNavn = "Jemen 3",
        engelskNavn = "Yemen 3"
    ),
    P_UKJENT(
        bokmaalNavn = "ukjent",
        engelskNavn = "unknown"
    ),
    P_UnkUnkUnk(
        erHistorisk = true,
        bokmaalNavn = "ukjent 2",
        engelskNavn = "unknown 2"
    )
}
